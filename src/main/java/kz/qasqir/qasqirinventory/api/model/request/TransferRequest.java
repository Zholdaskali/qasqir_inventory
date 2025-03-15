package kz.qasqir.qasqirinventory.api.model.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    private String documentType; // Тип документа
    private String documentNumber; // Номер
    private LocalDate documentDate; // Дата создания
    private List<TransferItemsRequest> items; // Товары
    private Long createdBy;
}
