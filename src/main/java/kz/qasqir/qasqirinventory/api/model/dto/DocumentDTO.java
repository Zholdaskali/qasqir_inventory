package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.Data;
import java.util.List;
import java.time.LocalDate;

@Data
public class DocumentDTO {
    private String documentType; // Тип документа
    private String documentNumber; // Номер
    private LocalDate documentDate; // Дата создания
    private Long supplierId; // поставщика
    private Long customerId; // клиента
    private String tnvedCode; // Код
    private List<ItemRequest> items; // Товары
}
