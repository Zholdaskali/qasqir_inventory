package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TopNomenclatureDTO {
    private Long nomenclatureId;
    private String nomenclatureName;
    private BigDecimal totalQuantity;
}
