package kz.qasqir.qasqirinventory.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequest {
    private String documentType;
    private String documentNumber;
    private LocalDate documentDate;
    private String comment;
    private Long inventoryId;
    private BigDecimal quantity;
    private Long createdBy;
}
