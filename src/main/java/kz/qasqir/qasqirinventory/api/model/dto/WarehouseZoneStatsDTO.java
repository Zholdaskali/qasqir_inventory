package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WarehouseZoneStatsDTO {
    private final Long zoneId;
    private final String zoneName;
    private final String warehouseName;
    private final double fillPercentage;
}
