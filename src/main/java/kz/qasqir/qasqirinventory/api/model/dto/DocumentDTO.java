package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DocumentDTO {
    private String documentType;
    private String documentNumber;
    private LocalDate documentDate;
    private Long supplierId;
    private Long customerId;
    private String tnvedCode;
    private List<ItemDTO> items;
}
