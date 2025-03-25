package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TicketDTO {
    private Long id;
    private String type;
    private String status;
    private DocumentDTO document;
    private Long createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private Long managerId;
    private String managerName;
    private LocalDateTime managedAt;
    private String comment;
    private InventoryDTO inventory;
    private BigDecimal quantity;
}