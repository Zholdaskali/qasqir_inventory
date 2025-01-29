package kz.qasqir.qasqirinventory.api.model.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DocumentRequest {
//    private String documentType;
//    private String documentNumber;
//    private LocalDateTime documentDate;
//    private Long supplierId;
//    private Long customerId;
//    private Long createdBy;
//    private Long updatedBy;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
    private String documentType; // Тип документа
    private String documentNumber; // Номер
    private LocalDate documentDate; // Дата создания
    private Long supplierId; //
    private Long customerId; //
    private String tnvedCode; // Код
    private List<ItemRequest> items; // Товары
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
