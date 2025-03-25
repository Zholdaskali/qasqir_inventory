package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WarehouseZoneStatsDTO {
    private Long zoneId;
    private String zoneName;
    private double fillPercentage;
    BigDecimal usedCapacity;
    BigDecimal totalVolume;
    private Long warehouseId;
    private String warehouseName;
}
