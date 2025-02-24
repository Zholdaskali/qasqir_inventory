package kz.qasqir.qasqirinventory.api.model.dto;

import kz.qasqir.qasqirinventory.api.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
public class InventoryAuditDTO {
    private Long InventoryId;
    private Long warehouseId;
    private String warehouseName;
    private LocalDate auditDate;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
}
