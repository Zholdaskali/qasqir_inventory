package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InventoryAuditResultDTO {
    private Long id;
    private Long nomenclatureId;
    private String nomenclatureName;
    private Long zoneId;
    private String zoneName;
    private BigDecimal expectedQuantity;
    private BigDecimal actualQuantity;
    private BigDecimal discrepancy;
    private LocalDateTime createdAt;
    private String nomenclatureCode;
}
