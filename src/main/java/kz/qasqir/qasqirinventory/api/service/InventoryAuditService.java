package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.model.dto.InventoryAuditDTO;
import kz.qasqir.qasqirinventory.api.model.entity.InventoryAudit;
import kz.qasqir.qasqirinventory.api.repository.InventoryAuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryAuditService {
    private final InventoryAuditRepository inventoryAuditRepository;

    public InventoryAuditService(InventoryAuditRepository inventoryAuditRepository) {
        this.inventoryAuditRepository = inventoryAuditRepository;
    }

    public List<InventoryAuditDTO> getAllInventoryAudit() {
        return inventoryAuditRepository.findAll().stream()
                .map(this::convertInventoryAudit)
                .collect(Collectors.toList());
    }

    public List<InventoryAuditDTO> getAllInProgressInventoryAudit() {
        return inventoryAuditRepository.findByStatus("IN_PROGRESS").stream()
                .map(this::convertInventoryAudit)
                .collect(Collectors.toList());
    }

    public List<InventoryAuditDTO> getAllInCompletedInventoryAudit() {
        return inventoryAuditRepository.findByStatus("COMPLETES").stream()
                .map(this::convertInventoryAudit)
                .collect(Collectors.toList());
    }

    public InventoryAuditDTO getById(Long inventoryId) {
        return convertInventoryAudit(inventoryAuditRepository.findById(inventoryId).orElseThrow(() -> new RuntimeException("Инвентаризация не найдена")));
    }

    private InventoryAuditDTO convertInventoryAudit(InventoryAudit inventoryAudit) {
        return new InventoryAuditDTO(inventoryAudit.getId(), inventoryAudit.getWarehouse().getId() ,inventoryAudit.getWarehouse().getName(), inventoryAudit.getAuditDate(), inventoryAudit.getStatus(), inventoryAudit.getCreatedBy().getUserName(), inventoryAudit.getCreatedAt());
    }
}
