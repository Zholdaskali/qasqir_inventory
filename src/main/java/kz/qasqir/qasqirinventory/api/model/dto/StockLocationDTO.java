package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class StockLocationDTO {
    private Long zoneId;
    private Long containerId;
    private BigDecimal quantity;
}
