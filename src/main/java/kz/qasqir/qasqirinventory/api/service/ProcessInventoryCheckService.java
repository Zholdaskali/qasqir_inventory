package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.model.entity.*;
import kz.qasqir.qasqirinventory.api.model.request.InventoryAuditResultRequest;
import kz.qasqir.qasqirinventory.api.repository.InventoryAuditRepository;
import kz.qasqir.qasqirinventory.api.repository.InventoryAuditResultRepository;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
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
    private final InventoryAuditResultRepository inventoryAuditResultRepository;

    // Начало инвентаризации
    @Transactional(rollbackOn = Exception.class)
    public String startInventoryCheck(Long warehouseId, Long createdBy) {
        Warehouse warehouse = warehouseService.getById(warehouseId);
        InventoryAudit audit = new InventoryAudit();
        audit.setWarehouse(warehouse);
        audit.setAuditDate(LocalDate.now());
        audit.setStatus("IN_PROGRESS");
        audit.setCreatedBy(userService.getByUserId(createdBy));
        inventoryAuditRepository.save(audit);
        return "Инвентаризация началось";
    }

    // Процесс инвентаризации
    @Transactional(rollbackOn = Exception.class)
    public void processInventoryCheck(Long auditId, List<InventoryAuditResultRequest> results) {
        InventoryAudit audit = inventoryAuditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Inventory audit not found"));

        for (InventoryAuditResultRequest result : results) {
            Nomenclature nomenclature = nomenclatureService.getById(result.getNomenclatureId());
            WarehouseZone warehouseZone = warehouseZoneService.getById(result.getWarehouseZoneId());

            Inventory inventory = inventoryRepository.findByNomenclatureIdAndWarehouseZoneId(nomenclature.getId(), warehouseZone.getId())
                    .orElse(new Inventory(nomenclature, BigDecimal.ZERO, warehouseZone));

            BigDecimal expectedQuantity = inventory.getQuantity();
            BigDecimal actualQuantity = result.getActualQuantity();
            BigDecimal discrepancy = actualQuantity.subtract(expectedQuantity);

            InventoryAuditResult auditResult = new InventoryAuditResult();
            auditResult.setAudit(audit);
            auditResult.setNomenclature(nomenclature);
            auditResult.setWarehouseZone(warehouseZone);
            auditResult.setExpectedQuantity(expectedQuantity);
            auditResult.setActualQuantity(actualQuantity);
            auditResult.setDiscrepancy(discrepancy);
            inventoryAuditResultRepository.save(auditResult);
        }

        audit.setStatus("COMPLETED");
        inventoryAuditRepository.save(audit);
    }
}
