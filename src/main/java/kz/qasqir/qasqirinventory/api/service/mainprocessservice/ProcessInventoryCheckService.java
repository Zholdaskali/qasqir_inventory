package kz.qasqir.qasqirinventory.api.service.mainprocessservice;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.model.dto.InventoryAuditDTO;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseStructureDTO;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseZoneStructureDTO;
import kz.qasqir.qasqirinventory.api.model.entity.*;
import kz.qasqir.qasqirinventory.api.model.request.InventoryAuditResultRequest;
import kz.qasqir.qasqirinventory.api.repository.InventoryAuditRepository;
import kz.qasqir.qasqirinventory.api.repository.InventoryAuditResultRepository;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
import kz.qasqir.qasqirinventory.api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional(rollbackOn = Exception.class)
    public InventoryAuditDTO startInventoryCheck(Long warehouseId, Long createdBy) {
        Warehouse warehouse = warehouseService.getById(warehouseId);
        InventoryAudit audit = new InventoryAudit();
        audit.setWarehouse(warehouse);
        audit.setAuditDate(LocalDate.now());
        audit.setStatus("IN_PROGRESS");
        audit.setCreatedBy(userService.getByUserId(createdBy));
        return inventoryAuditService.convertInventoryAudit(inventoryAuditRepository.save(audit));
    }

    @Transactional(rollbackOn = Exception.class)
    public String processInventoryCheck(Long auditId, List<InventoryAuditResultRequest> results) {
        try {

            if (auditId == null) {
                throw new RuntimeException("ID инвентаризации не может быть пустым");
            }
            if (results == null || results.isEmpty()) {
                throw new RuntimeException("Результаты инвентаризации не могут быть пустыми");
            }

            InventoryAudit audit = inventoryAuditRepository.findById(auditId)
                    .orElseThrow(() -> new RuntimeException("Инвентаризация не найдена"));

            if (!"IN_PROGRESS".equals(audit.getStatus())) {
                throw new RuntimeException("Инвентаризация уже завершена или не начата");
            }

            long count = 0L + inventoryAuditResultRepository.findByAuditId(auditId).size();
            for (InventoryAuditResultRequest result : results) {
                validateAuditResultRequest(result);

                Nomenclature nomenclature = nomenclatureService.getById(result.getNomenclatureId());
                WarehouseZone warehouseZone = warehouseZoneService.getById(result.getWarehouseZoneId());
                WarehouseContainer container = result.getContainerId() != null ?
                        warehouseContainerService.getById(result.getContainerId()) : null;

                if (!warehouseZone.getCanStoreItems()) {
                    throw new RuntimeException("Зона " + warehouseZone.getName() + " не предназначена для хранения товаров");
                }

                Inventory inventory = inventoryRepository.findByNomenclatureIdAndWarehouseZoneIdAndWarehouseContainerId(
                                nomenclature.getId(), warehouseZone.getId(), result.getContainerId())
                        .orElse(new Inventory(nomenclature, BigDecimal.ZERO, warehouseZone, container));
                BigDecimal expectedQuantity = inventory.getQuantity();
                BigDecimal actualQuantity = result.getActualQuantity();
                BigDecimal discrepancy = actualQuantity.subtract(expectedQuantity);

                BigDecimal requiredVolume = capacityControlService.calculateVolume(nomenclature, actualQuantity);

                if (container != null) {
                    BigDecimal currentOccupiedVolume = capacityControlService.getContainerOccupiedVolume(container);
                    BigDecimal totalContainerCapacity = container.getCapacity();
                    BigDecimal availableCapacity = totalContainerCapacity.subtract(currentOccupiedVolume);

                    BigDecimal currentItemVolume = capacityControlService.calculateVolume(nomenclature, expectedQuantity);
                    BigDecimal netVolumeChange = requiredVolume.subtract(currentItemVolume);

                    if (netVolumeChange.compareTo(BigDecimal.ZERO) > 0 &&
                            availableCapacity.compareTo(netVolumeChange) < 0) {
                        throw new RuntimeException("Недостаточно места в контейнере для размещения " +
                                actualQuantity + " единиц товара " + nomenclature.getName() +
                                ". Доступно: " + availableCapacity + ", требуется: " + netVolumeChange);
                    }

                    if (discrepancy.compareTo(BigDecimal.ZERO) > 0) {
                        capacityControlService.reserveContainerCapacity(container, nomenclature, discrepancy);
                    } else if (discrepancy.compareTo(BigDecimal.ZERO) < 0) {
                        BigDecimal freedVolume = capacityControlService.calculateVolume(nomenclature, discrepancy.abs());
                        capacityControlService.freeContainerCapacity(container, freedVolume);
                    }
                } else {
                    BigDecimal remainingZoneCapacity = BigDecimal.valueOf(warehouseZone.getHeight() * warehouseZone.getLength() * warehouseZone.getWidth());
                    if (remainingZoneCapacity.compareTo(requiredVolume) < 0) {
                        throw new RuntimeException("Недостаточно места в зоне " + warehouseZone.getName() + " для размещения " +
                                actualQuantity + " единиц товара " + nomenclature.getName());
                    }

                    if (discrepancy.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal additionalVolume = capacityControlService.calculateVolume(nomenclature, discrepancy);
                        capacityControlService.reserveZoneCapacity(warehouseZone, additionalVolume);
                    } else if (discrepancy.compareTo(BigDecimal.ZERO) < 0) {
                        BigDecimal freedVolume = capacityControlService.calculateVolume(nomenclature, discrepancy.abs());
                        capacityControlService.freeZoneCapacity(warehouseZone, freedVolume);
                    }
                }

                capacityControlService.updateInventory(inventory, actualQuantity);

                InventoryAuditResult auditResult = new InventoryAuditResult();
                auditResult.setAudit(audit);
                auditResult.setNomenclature(nomenclature);
                auditResult.setWarehouseZone(warehouseZone);
                auditResult.setExpectedQuantity(expectedQuantity);
                auditResult.setActualQuantity(actualQuantity);
                auditResult.setDiscrepancy(discrepancy);
                inventoryAuditResultRepository.save(auditResult);

                count++;
            }

// Получаем зоны, пригодные для хранения
            WarehouseStructureDTO warehouseStructureDTO = warehouseService.getWarehouseDetails(audit.getWarehouse().getId());
            List<WarehouseZoneStructureDTO> zones = warehouseStructureDTO.getZones()
                    .stream()
                    .filter(zone -> zone.getCanStoreItems())
                    .sorted(Comparator.comparing(WarehouseZoneStructureDTO::getName))
                    .collect(Collectors.toList());

// Считаем зоны, где есть товары
            long countZonesWithItems = zones.stream()
                    .filter(zone -> inventoryRepository.existsByWarehouseZoneId(zone.getId()))
                    .count();

            if (countZonesWithItems == count) {
                audit.setStatus("COMPLETED");
                inventoryAuditRepository.save(audit);
                return "Инвентаризация успешно завершена!!!";
            } else {
                return "Инвентаризация обработана частично: " + count + " из " + countZonesWithItems + " зон с товарами";
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private void validateAuditResultRequest(InventoryAuditResultRequest result) {
        if (result.getNomenclatureId() == null || result.getWarehouseZoneId() == null || result.getActualQuantity() == null) {
            throw new RuntimeException("Неверные данные о результатах инвентаризационного аудита: все поля должны быть заполнены");
        }
        if (result.getActualQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Фактическое количество не может быть отрицательным");
        }
    }
}