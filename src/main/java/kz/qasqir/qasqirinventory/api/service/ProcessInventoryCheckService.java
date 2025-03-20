package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.EmailIsAlreadyRegisteredException;
import kz.qasqir.qasqirinventory.api.model.dto.InventoryAuditDTO;
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
    private final TransactionService transactionService;
    private final InventoryAuditService inventoryAuditService;

    // Начало инвентаризации
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
                inventory.setQuantity(actualQuantity);
                inventoryRepository.save(inventory);

                inventoryAuditResultRepository.save(auditResult);
            }
            audit.setStatus("COMPLETED");
            inventoryAuditRepository.save(audit); // Обновляем статус инвентаризации
            return "Инвентаризация успешно завершена!!!";
        } catch (Exception e) {
            throw e; // Пробрасываем исключение, чтобы транзакция откатилась
        }
    }

}
