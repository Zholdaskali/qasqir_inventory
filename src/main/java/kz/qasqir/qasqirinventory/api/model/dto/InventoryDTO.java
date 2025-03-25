package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDTO {
    private Long id;
    private BigDecimal quantity;
    private Long nomenclatureId;
    private String nomenclatureName;
    private Long warehouseZoneId;
    private String containerSerial;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
