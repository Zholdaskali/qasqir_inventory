package kz.qasqir.qasqirinventory.api.service.process;

import kz.qasqir.qasqirinventory.api.model.dto.InventoryAuditDTO;
import kz.qasqir.qasqirinventory.api.model.dto.InventoryAuditResultDTO;
import kz.qasqir.qasqirinventory.api.model.entity.InventoryAudit;
import kz.qasqir.qasqirinventory.api.model.enums.Status;
import kz.qasqir.qasqirinventory.api.repository.InventoryAuditRepository;
import kz.qasqir.qasqirinventory.api.repository.InventoryAuditResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryAuditService {
    private final InventoryAuditRepository inventoryAuditRepository;
    private final InventoryAuditResultService inventoryAuditResultService;
    private final InventoryAuditResultRepository inventoryAuditResultRepository;

    public List<InventoryAuditDTO> getAllInventoryAudit() {
        return inventoryAuditRepository.findAll().stream()
                .map(this::convertInventoryAudit)
                .collect(Collectors.toList());
    }

    public List<InventoryAuditDTO> getAllInventoryAuditByAuditSystemId(Long id) {
        return inventoryAuditRepository.findAllByInventoryAuditSystemId(id).stream()
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
        return getAllInventoryAuditByStatus(startDate, endDate, Status.IN_PROGRESS.getCode());
    }

    public List<InventoryAuditDTO> getAllInCompletedInventoryAudit(LocalDate startDate, LocalDate endDate) {
        return getAllInventoryAuditByStatus(startDate, endDate, Status.COMPLETED.getCode());
    }

    public InventoryAuditDTO getById(Long inventoryId) {
        return convertInventoryAudit(inventoryAuditRepository.findById(inventoryId).orElseThrow(() -> new RuntimeException("Инвентаризация не найдена")));
    }

    protected InventoryAuditDTO convertInventoryAudit(InventoryAudit inventoryAudit) {
        List<InventoryAuditResultDTO> result = inventoryAuditResultRepository.findByAuditId(inventoryAudit.getId()).stream().map(inventoryAuditResultService::convertToDTO).toList();
        return new InventoryAuditDTO(inventoryAudit.getId(), inventoryAudit.getWarehouse().getId() ,inventoryAudit.getWarehouse().getName(), inventoryAudit.getAuditDate(), inventoryAudit.getStatus(), inventoryAudit.getCreatedBy().getUserName(), inventoryAudit.getCreatedAt(), result);
    }
}
