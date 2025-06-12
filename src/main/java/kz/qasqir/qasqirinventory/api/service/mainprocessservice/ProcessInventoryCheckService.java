package kz.qasqir.qasqirinventory.api.service.mainprocessservice;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.model.dto.InventoryAuditDTO;
import kz.qasqir.qasqirinventory.api.model.entity.*;
import kz.qasqir.qasqirinventory.api.model.request.InventoryAuditResultRequest;
import kz.qasqir.qasqirinventory.api.repository.*;
import kz.qasqir.qasqirinventory.api.service.defaultservice.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessInventoryCheckService {
    private final WarehouseService warehouseService;
    private final InventoryAuditRepository inventoryAuditRepository;
    private final UserService userService;
    private final InventoryRepository inventoryRepository;
    private final NomenclatureService nomenclatureService;
    private final WarehouseZoneService warehouseZoneService;
    private final WarehouseContainerService warehouseContainerService;
    private final InventoryAuditResultRepository inventoryAuditResultRepository;
    private final InventoryAuditService inventoryAuditService;
    private final CapacityControlService capacityControlService;
    private final InventoryAuditSystemService inventoryAuditSystemService;
    private final InventoryAuditSystemRepository inventoryAuditSystemRepository;
    private final WarehouseRepository warehouseRepository;

    @Transactional(rollbackOn = Exception.class)
    public InventoryAuditDTO startInventoryCheck(Long warehouseId, Long createdBy, Long inventoryAuditSystemId) {
        Warehouse warehouse = warehouseService.getById(warehouseId);
        InventoryAuditSystem auditSystem = inventoryAuditSystemService.getById(inventoryAuditSystemId);

        if (inventoryAuditRepository.existsByInventoryAuditSystemIdAndWarehouseId(inventoryAuditSystemId, warehouseId)) {
            throw new RuntimeException("Для этого склада уже инвентаризация начата в этой системной инвентаризации");
        }

        InventoryAudit audit = new InventoryAudit();
        audit.setWarehouse(warehouse);
        audit.setInventoryAuditSystem(auditSystem);
        audit.setAuditDate(LocalDate.now());
        audit.setStatus("IN_PROGRESS");
        audit.setCreatedBy(userService.getByUserId(createdBy));

        return inventoryAuditService.convertInventoryAudit(inventoryAuditRepository.save(audit));
    }

    @Transactional(rollbackOn = Exception.class)
    public String processInventoryCheck(Long auditId, List<InventoryAuditResultRequest> results) {
        validateInput(auditId, results);

        InventoryAudit audit = getActiveAudit(auditId);
        long processedCount = processAuditResults(audit, results);

        return completeInventoryIfFinished(audit, processedCount);
    }

    private void validateInput(Long auditId, List<InventoryAuditResultRequest> results) {
        if (auditId == null) {
            throw new IllegalArgumentException("Inventory audit ID cannot be null");
        }
        if (results == null || results.isEmpty()) {
            throw new IllegalArgumentException("Inventory audit results cannot be empty");
        }
    }

    private InventoryAudit getActiveAudit(Long auditId) {
        InventoryAudit audit = inventoryAuditRepository.findById(auditId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory audit not found"));

        if (!"IN_PROGRESS".equals(audit.getStatus())) {
            throw new IllegalStateException("Inventory audit is already completed or not started");
        }
        return audit;
    }

    private long processAuditResults(InventoryAudit audit, List<InventoryAuditResultRequest> results) {
        long processedCount = inventoryAuditResultRepository.countByAuditId(audit.getId());

        for (InventoryAuditResultRequest result : results) {
            validateAuditResultRequest(result);
            processedCount += processSingleResult(audit, result);
        }

        return processedCount;
    }

    private long processSingleResult(InventoryAudit audit, InventoryAuditResultRequest result) {
        Nomenclature nomenclature = nomenclatureService.getById(result.getNomenclatureId());
        WarehouseZone zone = validateAndGetZone(result.getWarehouseZoneId());
        WarehouseContainer container = getContainerIfExists(result.getContainerId());

        Inventory inventory = getOrCreateInventory(nomenclature, zone, container, result.getContainerId());
        BigDecimal discrepancy = calculateDiscrepancy(inventory, result.getActualQuantity());

        validateAndUpdateCapacity(nomenclature, zone, container, result.getActualQuantity(), discrepancy, inventory);
        saveAuditResult(audit, nomenclature, zone, inventory.getQuantity(), result.getActualQuantity(), discrepancy);

        return 1;
    }

    private WarehouseZone validateAndGetZone(Long zoneId) {
        WarehouseZone zone = warehouseZoneService.getById(zoneId);
        if (!zone.getCanStoreItems()) {
            throw new IllegalStateException("Zone " + zone.getName() + " is not designated for item storage");
        }
        return zone;
    }

    private WarehouseContainer getContainerIfExists(Long containerId) {
        return containerId != null ? warehouseContainerService.getById(containerId) : null;
    }

    private Inventory getOrCreateInventory(Nomenclature nomenclature, WarehouseZone zone,
                                           WarehouseContainer container, Long containerId) {
        return inventoryRepository.findByNomenclatureIdAndWarehouseZoneIdAndWarehouseContainerId(
                        nomenclature.getId(), zone.getId(), containerId)
                .orElse(new Inventory(nomenclature, BigDecimal.ZERO, zone, container));
    }

    private BigDecimal calculateDiscrepancy(Inventory inventory, BigDecimal actualQuantity) {
        return actualQuantity.subtract(inventory.getQuantity());
    }

    private void validateAndUpdateCapacity(Nomenclature nomenclature, WarehouseZone zone,
                                           WarehouseContainer container, BigDecimal actualQuantity,
                                           BigDecimal discrepancy, Inventory inventory) {
        BigDecimal requiredVolume = capacityControlService.calculateVolume(nomenclature, actualQuantity);

        if (container != null) {
            validateContainerCapacity(nomenclature, container, inventory.getQuantity(), requiredVolume, discrepancy);
        } else {
            validateZoneCapacity(nomenclature, zone, requiredVolume, discrepancy);
        }

        capacityControlService.updateInventory(inventory, actualQuantity);
    }

    private void validateContainerCapacity(Nomenclature nomenclature, WarehouseContainer container,
                                           BigDecimal expectedQuantity, BigDecimal requiredVolume,
                                           BigDecimal discrepancy) {
        BigDecimal currentOccupiedVolume = capacityControlService.getContainerOccupiedVolume(container);
        BigDecimal availableCapacity = container.getCapacity().subtract(currentOccupiedVolume);
        BigDecimal currentItemVolume = capacityControlService.calculateVolume(nomenclature, expectedQuantity);
        BigDecimal netVolumeChange = requiredVolume.subtract(currentItemVolume);

        if (netVolumeChange.compareTo(BigDecimal.ZERO) > 0 &&
                availableCapacity.compareTo(netVolumeChange) < 0) {
            throw new IllegalStateException(String.format(
                    "Insufficient container space for %s units of %s. Available: %s, Required: %s",
                    discrepancy, nomenclature.getName(), availableCapacity, netVolumeChange));
        }

        updateContainerCapacity(container, nomenclature, discrepancy);
    }

    private void updateContainerCapacity(WarehouseContainer container, Nomenclature nomenclature,
                                         BigDecimal discrepancy) {
        if (discrepancy.compareTo(BigDecimal.ZERO) > 0) {
            capacityControlService.reserveContainerCapacity(container, nomenclature, discrepancy);
        } else if (discrepancy.compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal freedVolume = capacityControlService.calculateVolume(nomenclature, discrepancy.abs());
            capacityControlService.freeContainerCapacity(container, freedVolume);
        }
    }

    private void validateZoneCapacity(Nomenclature nomenclature, WarehouseZone zone,
                                      BigDecimal requiredVolume, BigDecimal discrepancy) {
        BigDecimal zoneCapacity = BigDecimal.valueOf(zone.getHeight() * zone.getLength() * zone.getWidth());

        if (zoneCapacity.compareTo(requiredVolume) < 0) {
            throw new IllegalStateException(String.format(
                    "Insufficient space in zone %s for %s units of %s",
                    zone.getName(), requiredVolume, nomenclature.getName()));
        }

        updateZoneCapacity(zone, nomenclature, discrepancy);
    }

    private void updateZoneCapacity(WarehouseZone zone, Nomenclature nomenclature, BigDecimal discrepancy) {
        if (discrepancy.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal additionalVolume = capacityControlService.calculateVolume(nomenclature, discrepancy);
            capacityControlService.reserveZoneCapacity(zone, additionalVolume);
        } else if (discrepancy.compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal freedVolume = capacityControlService.calculateVolume(nomenclature, discrepancy.abs());
            capacityControlService.freeZoneCapacity(zone, freedVolume);
        }
    }

    private void saveAuditResult(InventoryAudit audit, Nomenclature nomenclature, WarehouseZone zone,
                                 BigDecimal expectedQuantity, BigDecimal actualQuantity, BigDecimal discrepancy) {
        InventoryAuditResult auditResult = new InventoryAuditResult();
        auditResult.setAudit(audit);
        auditResult.setNomenclature(nomenclature);
        auditResult.setWarehouseZone(zone);
        auditResult.setExpectedQuantity(expectedQuantity);
        auditResult.setActualQuantity(actualQuantity);
        auditResult.setDiscrepancy(discrepancy);
        inventoryAuditResultRepository.save(auditResult);
    }

    private String completeInventoryIfFinished(InventoryAudit audit, long processedCount) {
        Long warehouseId = audit.getWarehouse().getId();
        long totalInventoryCount = inventoryRepository.countByWarehouseZoneWarehouseId(warehouseId);
        long totalWarehouseCount = warehouseRepository.count();

        if (totalInventoryCount == processedCount) {
            completeAudit(audit);
            checkAndCompleteAuditSystem(audit, totalWarehouseCount);
            return "Inventory check completed successfully!";
        }
        return String.format("Inventory partially processed: %d of %d items", processedCount, totalInventoryCount);
    }

    private void completeAudit(InventoryAudit audit) {
        audit.setStatus("COMPLETED");
        inventoryAuditRepository.save(audit);
    }

    private void checkAndCompleteAuditSystem(InventoryAudit audit, long totalWarehouseCount) {
        Long auditSystemId = audit.getInventoryAuditSystem().getId();
        if (!inventoryAuditRepository.existsByStatusAndInventoryAuditSystemId("IN_PROGRESS", auditSystemId) &&
                totalWarehouseCount == inventoryAuditRepository.countByInventoryAuditSystemId(auditSystemId)) {
            InventoryAuditSystem auditSystem = inventoryAuditSystemRepository.findById(auditSystemId)
                    .orElseThrow(() -> new IllegalStateException("Audit system not found"));
            auditSystem.setStatus("COMPLETED");
            inventoryAuditSystemRepository.save(auditSystem);
        }
    }

    private void validateAuditResultRequest(InventoryAuditResultRequest result) {
        if (result.getNomenclatureId() == null ||
                result.getWarehouseZoneId() == null ||
                result.getActualQuantity() == null) {
            throw new IllegalArgumentException("Invalid audit result data: all fields must be filled");
        }
        if (result.getActualQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Actual quantity cannot be negative");
        }
    }
}