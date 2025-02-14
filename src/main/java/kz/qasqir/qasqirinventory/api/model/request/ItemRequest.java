package kz.qasqir.qasqirinventory.api.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemRequest {
    private Long nomenclatureId;
    private String nomenclatureName;
    private BigDecimal quantity;
    private String measurementUnit;
    private Long warehouseZoneId;
    private String warehouseZoneName;
    private Long containerId;
    private boolean isReturnable;
}