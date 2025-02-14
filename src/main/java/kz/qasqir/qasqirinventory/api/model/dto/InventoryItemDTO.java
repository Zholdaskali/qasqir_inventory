package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItemDTO {
    private String itemName;
    private String article;
    private String code;
    private String unit;
    private BigDecimal quantity;
    private String containerSerial;
    private String warehouseZoneName;
    private String warehouseLocation;
    private String warehouseName;
}

