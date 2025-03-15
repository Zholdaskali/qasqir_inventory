package kz.qasqir.qasqirinventory.api.model.request;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class DocumentRequest {
    private String documentType;
    private String documentNumber;
    private LocalDate documentDate;
    private Long supplierId;
    private Long customerId;
    private List<ItemRequest> items;
    private Long createdBy;
}