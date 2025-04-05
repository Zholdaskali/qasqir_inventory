package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.EmailIsAlreadyRegisteredException;
import kz.qasqir.qasqirinventory.api.model.dto.InventoryAuditDTO;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseStructureDTO;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseZoneStructureDTO;
import kz.qasqir.qasqirinventory.api.model.entity.*;
import kz.qasqir.qasqirinventory.api.model.request.InventoryAuditResultRequest;
import kz.qasqir.qasqirinventory.api.repository.InventoryAuditRepository;
import kz.qasqir.qasqirinventory.api.repository.InventoryAuditResultRepository;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
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
            InventoryAudit audit = inventoryAuditRepository.findById(auditId)
                    .orElseThrow(() -> new RuntimeException("Инвентаризация не найдена"));
            if (results.isEmpty()) {
                throw new RuntimeException("Массив товаров пустой");
            }
            long count = 0L;
            for (InventoryAuditResultRequest result : results) {
                Nomenclature nomenclature = nomenclatureService.getById(result.getNomenclatureId());
                WarehouseZone warehouseZone = warehouseZoneService.getById(result.getWarehouseZoneId());

                Inventory inventory = inventoryRepository.findByNomenclatureIdAndWarehouseZoneId(nomenclature.getId(), warehouseZone.getId())
                        .orElse(new Inventory(nomenclature, BigDecimal.ZERO, warehouseZone));

                BigDecimal expectedQuantity = inventory.getQuantity();
                BigDecimal actualQuantity = result.getActualQuantity();
                BigDecimal discrepancy = actualQuantity.subtract(expectedQuantity);

                // Обновляем только инвентарь без изменения емкости зон
                capacityControlService.updateInventory(inventory, actualQuantity);

                InventoryAuditResult auditResult = new InventoryAuditResult();
                auditResult.setAudit(audit);
                auditResult.setNomenclature(nomenclature);
                auditResult.setWarehouseZone(warehouseZone);
                auditResult.setExpectedQuantity(expectedQuantity);
                auditResult.setActualQuantity(actualQuantity);
                auditResult.setDiscrepancy(discrepancy);
                inventoryRepository.save(inventory);

                inventoryAuditResultRepository.save(auditResult);
                count++;
            }
            WarehouseStructureDTO warehouseStructureDTO = warehouseService.getWarehouseDetails(audit.getWarehouse().getId());

            warehouseStructureDTO.getZones()
                    .stream()
                    .filter(zone -> zone.getCanStoreItems() == true)
                    .sorted(Comparator.comparing(WarehouseZoneStructureDTO::getName))
                    .collect(Collectors.toList());

            long countZones =  warehouseStructureDTO.getZones().size();
            if (countZones == count) {
                audit.setStatus("COMPLETED");
            }
            inventoryAuditRepository.save(audit);
            return "Инвентаризация успешно завершена!!!";
        } catch (Exception e) {
            throw e;
        }
    }
}