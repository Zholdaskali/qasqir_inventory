package kz.qasqir.qasqirinventory.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnRequest {
    private String returnType; // Тип возврата (например, "Возврат поставщику", "Возврат от покупателя")
    private String documentNumber; // ID связанного документа (например, накладная или инвойс)
    private Long inventoryId;
    private Long nomenclatureId; // ID товара
    private BigDecimal quantity; // Количество возвращаемого товара
    private String reason; // Причина возврата
    private Long createdBy;
}

