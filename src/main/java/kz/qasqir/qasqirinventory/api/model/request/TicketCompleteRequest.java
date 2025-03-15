package kz.qasqir.qasqirinventory.api.model.request;

import lombok.Data;

@Data
public class TicketCompleteRequest {
    private Long ticketId;
    private Long managed_id;
}
