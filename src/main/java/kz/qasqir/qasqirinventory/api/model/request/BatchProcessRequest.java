package kz.qasqir.qasqirinventory.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
public class BatchProcessRequest {
    private String documentType;
    private String documentNumber;
    private LocalDate documentDate;
    private Long customerId;
    private Long createdBy;
    private List<BatchTicketRequest> ticketRequests;
}
