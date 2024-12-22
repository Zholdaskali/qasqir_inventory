package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.Data;
import java.util.List;
import java.time.LocalDate;

@Data
public class DocumentDTO {
    private Long id;
    private String documentType;
    private String documentNumber;
    private LocalDate documentDate;
    private Long supplierId;
    private Long customerId;
    private String tnvedCode;
    private List<DocumentItemDTO> items;
}
