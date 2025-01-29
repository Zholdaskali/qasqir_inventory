package kz.qasqir.qasqirinventory.api.model.dto;

import java.math.BigDecimal;

public class DocumentItemDTO {
    private Long nomenclatureId; // ID товара
    private String nomenclatureName; // Наименование товара
    private BigDecimal quantity; // Количество товара
    private String measurementUnit; // Единица измерения товара
    private Long warehouseZoneId; // ID зоны склада, куда поступает товар
    private String warehouseZoneName; // Название зоны склада
    private String containerSerial; // Серийный номер контейнера (если применимо)
    private boolean isReturnable; // Флаг возвратной тары (если применимо)

    // Getters and Setters
    public Long getNomenclatureId() {
        return nomenclatureId;
    }

    public void setNomenclatureId(Long nomenclatureId) {
        this.nomenclatureId = nomenclatureId;
    }

    public String getNomenclatureName() {
        return nomenclatureName;
    }

    public void setNomenclatureName(String nomenclatureName) {
        this.nomenclatureName = nomenclatureName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public Long getWarehouseZoneId() {
        return warehouseZoneId;
    }

    public void setWarehouseZoneId(Long warehouseZoneId) {
        this.warehouseZoneId = warehouseZoneId;
    }

    public String getWarehouseZoneName() {
        return warehouseZoneName;
    }

    public void setWarehouseZoneName(String warehouseZoneName) {
        this.warehouseZoneName = warehouseZoneName;
    }

    public String getContainerSerial() {
        return containerSerial;
    }

    public void setContainerSerial(String containerSerial) {
        this.containerSerial = containerSerial;
    }

    public boolean isReturnable() {
        return isReturnable;
    }

    public void setReturnable(boolean returnable) {
        isReturnable = returnable;
    }

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
