package kz.qasqir.qasqirinventory.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAuditResultRequest {
    private Long nomenclatureId;
    private Long warehouseZoneId;
    private Long containerId;
    private BigDecimal actualQuantity;
}
