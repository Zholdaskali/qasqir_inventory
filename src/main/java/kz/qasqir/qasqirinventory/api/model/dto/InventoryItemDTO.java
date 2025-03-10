package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItemDTO {
    private Long id;
    private Long nomenclatureId;
    private String nomenclatureName;
    private String measurementUnit;
    private String code;
    private BigDecimal quantity;
    private Long warehouseId;
    private String warehouseName;
    private Long containerId;
    private String containerName;
}

