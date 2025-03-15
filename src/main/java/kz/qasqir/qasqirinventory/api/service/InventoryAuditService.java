package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.model.dto.InventoryAuditDTO;
import kz.qasqir.qasqirinventory.api.model.entity.InventoryAudit;
import kz.qasqir.qasqirinventory.api.repository.InventoryAuditRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public List<InventoryAuditDTO> getAllInventoryAuditByStatus(LocalDate startDate, LocalDate endDate, String status) {
        LocalDateTime startDateTime = startDate.atStartOfDay(); // Начало дня
        LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1).minusSeconds(1); // Конец дня

        return inventoryAuditRepository.findAllByCreatedAtBetweenAndStatus(startDateTime, endDateTime, status)
                .stream()
                .map(this::convertInventoryAudit)
                .toList();
    }

    public List<InventoryAuditDTO> getAllInProgressInventoryAudit(LocalDate startDate, LocalDate endDate) {
        return getAllInventoryAuditByStatus(startDate, endDate, "IN_PROGRESS");
    }

    public List<InventoryAuditDTO> getAllInCompletedInventoryAudit(LocalDate startDate, LocalDate endDate) {
        return getAllInventoryAuditByStatus(startDate, endDate, "COMPLETED");
    }

    public InventoryAuditDTO getById(Long inventoryId) {
        return convertInventoryAudit(inventoryAuditRepository.findById(inventoryId).orElseThrow(() -> new RuntimeException("Инвентаризация не найдена")));
    }

    private InventoryAuditDTO convertInventoryAudit(InventoryAudit inventoryAudit) {
        return new InventoryAuditDTO(inventoryAudit.getId(), inventoryAudit.getWarehouse().getId() ,inventoryAudit.getWarehouse().getName(), inventoryAudit.getAuditDate(), inventoryAudit.getStatus(), inventoryAudit.getCreatedBy().getUserName(), inventoryAudit.getCreatedAt());
    }
}
