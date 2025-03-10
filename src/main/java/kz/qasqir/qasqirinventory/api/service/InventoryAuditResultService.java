package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.model.dto.InventoryAuditResultDTO;
import kz.qasqir.qasqirinventory.api.model.entity.InventoryAuditResult;
import kz.qasqir.qasqirinventory.api.repository.InventoryAuditResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryAuditResultService {
    private final InventoryAuditResultRepository inventoryAuditResultRepository;

    public List<InventoryAuditResultDTO> getAllByAuditId(Long auditId) {
        return inventoryAuditResultRepository.findByAuditId(auditId).stream().map(this::convertToDTO).toList();
    }

    private InventoryAuditResultDTO convertToDTO(InventoryAuditResult inventoryAuditResult) {
        return new InventoryAuditResultDTO(inventoryAuditResult.getId(), inventoryAuditResult.getNomenclature().getId(), inventoryAuditResult.getNomenclature().getName(), inventoryAuditResult.getWarehouseZone().getId(), inventoryAuditResult.getWarehouseZone().getName(), inventoryAuditResult.getExpectedQuantity(), inventoryAuditResult.getActualQuantity(), inventoryAuditResult.getDiscrepancy(), inventoryAuditResult.getCreatedAt());
    }
}
