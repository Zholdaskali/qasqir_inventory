package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemDTO {
    private Long nomenclatureId;
    private String nomenclatureName;
    private BigDecimal quantity;
    private String measurementUnit;
    private Long warehouseZoneId;
    private String warehouseZoneName;
    private String containerSerial;
    private boolean isReturnable;

    @Override
    public String toString() {
        return "DocumentItemDTO {" +
                "nomenclatureId=" + nomenclatureId +
                ", nomenclatureName='" + nomenclatureName + '\'' +
                ", quantity=" + quantity +
                ", measurementUnit='" + measurementUnit + '\'' +
                ", warehouseZoneId=" + warehouseZoneId +
                ", warehouseZoneName='" + warehouseZoneName + '\'' +
                ", containerSerial='" + containerSerial + '\'' +
                ", isReturnable=" + isReturnable +
                '}';
    }
}
