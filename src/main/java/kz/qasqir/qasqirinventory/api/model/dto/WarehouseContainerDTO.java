package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseContainerDTO {
    private Long id;
    private Long warehouseZoneId;
    private String serialNumber;
    private BigDecimal capacity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double width;
    private Double height;
    private Double length;
}
