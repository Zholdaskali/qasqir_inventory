package kz.qasqir.qasqirinventory.api.service.mainprocessservice;

import kz.qasqir.qasqirinventory.api.model.dto.InventoryAuditDTO;
import kz.qasqir.qasqirinventory.api.model.dto.InventoryAuditSystemDTO;
import kz.qasqir.qasqirinventory.api.model.entity.InventoryAuditSystem;
import kz.qasqir.qasqirinventory.api.model.request.InventoryAuditSystemRequest;
import kz.qasqir.qasqirinventory.api.repository.InventoryAuditSystemRepository;
import kz.qasqir.qasqirinventory.api.service.defaultservice.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryAuditSystemService {

    private final InventoryAuditSystemRepository inventoryAuditSystemRepository;
    private final UserService userService;
    private final InventoryAuditService inventoryAuditService;

    @Transactional
    public InventoryAuditSystemDTO startInventoryAuditSystem(InventoryAuditSystemRequest inventoryAuditSystemRequest) {
        InventoryAuditSystem inventoryAuditSystem = new InventoryAuditSystem(userService.getByUserId(inventoryAuditSystemRequest.getCreatedById()));
        return convertToDTO(inventoryAuditSystemRepository.save(inventoryAuditSystem));
    }

    public List<InventoryAuditSystemDTO> getInventoryResultsCompleted(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay(); // Начало дня
        LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1).minusSeconds(1);
        return inventoryAuditSystemRepository.findAllByCreatedAtBetweenAndStatus(startDateTime, endDateTime, "COMPLETED").stream().map(this::convertToDTO).toList();
    }

    public List<InventoryAuditSystemDTO> getInventoryResultsInProgress(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay(); // Начало дня
        LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1).minusSeconds(1);
        return inventoryAuditSystemRepository.findAllByCreatedAtBetweenAndStatus(startDateTime, endDateTime, "IN_PROGRESS").stream().map(this::convertToDTO).toList();
    }

    private InventoryAuditSystemDTO convertToDTO(InventoryAuditSystem inventoryAuditSystem) {

        List<InventoryAuditDTO> inventoryAuditDTOs = inventoryAuditService.getAllInventoryAuditByAuditSystemId(inventoryAuditSystem.getId());
        return new InventoryAuditSystemDTO(
                inventoryAuditSystem.getId(),
                inventoryAuditSystem.getAuditDate(),
                inventoryAuditSystem.getStatus(),
                inventoryAuditSystem.getCreatedBy().getId(),
                inventoryAuditSystem.getCreatedAt(),
                inventoryAuditSystem.getUpdatedAt(),
                inventoryAuditDTOs);
    }

    public InventoryAuditSystem getById(Long inventoryAuditSystemId) {
        return inventoryAuditSystemRepository.findById(inventoryAuditSystemId).orElseThrow(()-> new RuntimeException("Системная инвентаризация не найдена"));
    }

    public InventoryAuditSystemDTO getInventoryAuditSystemDTO(Long inventoryId) {
        return convertToDTO(getById(inventoryId));
    }

    @Transactional
    public String deleteById(Long id) {
        inventoryAuditSystemRepository.deleteById(id);
        return "Успешно удалено";
    }

//    private Long id;
//    private LocalDate auditDate;
//    private String status;
//    private Long createdById;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//    private List<InventoryAuditDTO> results;
}
