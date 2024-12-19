package kz.qasqir.qasqirinventory.api.model.dto;

import java.time.LocalDateTime;

public class DocumentDTO {
    private String documentType;
    private String documentNumber;
    private LocalDateTime documentDate;
    private Long supplierId;
    private Long customerId;

}
