package kz.qasqir.qasqirinventory.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchProcessRequest {
    private String documentType;
    private String documentNumber;
    private LocalDate documentDate;
    private Long customerId;
    private Long createdBy;
    private String fileName;
    private String fileData;
    private List<BatchTicketRequest> ticketRequests;
}
