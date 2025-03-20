package kz.qasqir.qasqirinventory.api.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class BatchTicketRequest {
    private String comment;
    private Long inventoryId;
    private BigDecimal quantity;
}

