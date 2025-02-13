package kz.qasqir.qasqirinventory.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferItemsRequest {
    private Long nomenclatureId;
    private BigDecimal quantity;
    private String measurementUnit;
    private Long toWarehouseZoneId;
    private Long fromWarehouseZoneId;
    private String containerSerial;
    private boolean isReturnable;
}
