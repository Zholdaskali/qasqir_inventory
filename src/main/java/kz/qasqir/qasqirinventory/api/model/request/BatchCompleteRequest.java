package kz.qasqir.qasqirinventory.api.model.request;

import lombok.Data;

import java.util.List;

@Data
public class BatchCompleteRequest {
    private List<Long> ticketIds;
    private Long managedId;
}
