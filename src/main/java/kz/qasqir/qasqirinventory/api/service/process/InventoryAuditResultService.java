package kz.qasqir.qasqirinventory.api.service.process;

import kz.qasqir.qasqirinventory.api.model.dto.InventoryAuditResultDTO;
import kz.qasqir.qasqirinventory.api.model.entity.InventoryAuditResult;
import kz.qasqir.qasqirinventory.api.repository.InventoryAuditResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryAuditResultService {
    private final InventoryAuditResultRepository inventoryAuditResultRepository;

    public List<InventoryAuditResultDTO> getAllByAuditId(Long auditId) {
        return inventoryAuditResultRepository.findByAuditId(auditId).stream().map(this::convertToDTO).toList();
    }

    public List<LocalDateTime> getLast10AuditDatesByNomenclatureCode(String nomenclatureCode) {
        return inventoryAuditResultRepository.findTop10ByNomenclatureCodeOrderByCreatedAtDesc(nomenclatureCode, PageRequest.of(0, 10));
    }

    protected InventoryAuditResultDTO convertToDTO(InventoryAuditResult inventoryAuditResult) {
        return new InventoryAuditResultDTO(inventoryAuditResult.getId(), inventoryAuditResult.getNomenclature().getId(),
                inventoryAuditResult.getNomenclature().getName(), inventoryAuditResult.getWarehouseZone().getId(),
                inventoryAuditResult.getWarehouseZone().getName(), inventoryAuditResult.getExpectedQuantity(),
                inventoryAuditResult.getActualQuantity(), inventoryAuditResult.getDiscrepancy(),
                inventoryAuditResult.getCreatedAt(), inventoryAuditResult.getNomenclature().getCode());
    }
}
