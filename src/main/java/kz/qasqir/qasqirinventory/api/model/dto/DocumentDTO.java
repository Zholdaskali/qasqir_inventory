package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentDTO {
    private String documentType;
    private String documentNumber;
    private LocalDateTime documentDate;
    private Long supplierId;
    private Long customerId;
}
