package kz.qasqir.qasqirinventory.api.model.request;

import com.auth0.jwt.internal.com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
public class BatchWriteOffRequest {
    private String documentType;
    private String documentNumber;
    private LocalDate documentDate;
    private Long createdBy;
    private List<BatchTicketRequest> ticketRequests;
}
