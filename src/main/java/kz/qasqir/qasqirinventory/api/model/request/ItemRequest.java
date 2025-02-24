package kz.qasqir.qasqirinventory.api.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemRequest {
    private Long nomenclatureId;
    private BigDecimal quantity;
    private Long warehouseZoneId;
    private Long containerId;
    private boolean isReturnable;
}