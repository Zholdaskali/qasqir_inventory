package kz.qasqir.qasqirinventory.api.model.request.OneC;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class OneCWriteOffItemRequest {
    private String nomenclatureCode;
    private BigDecimal quantity;
}
