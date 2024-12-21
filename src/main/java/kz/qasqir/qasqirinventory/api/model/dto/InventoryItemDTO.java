package kz.qasqir.qasqirinventory.api.model.dto;

import java.math.BigDecimal;

public class InventoryItemDTO {
    private String itemName; // Название товара
    private String article; // Артикул товара
    private String code; // Код товара
    private String unit; // Единица измерения
    private BigDecimal quantity; // Количество товара
    private String containerSerial; // Серийный номер контейнера (если применимо)
    private String warehouseZoneName; // Название зоны на складе
    private String warehouseLocation; // Местоположение склада
    private String warehouseName;

    public InventoryItemDTO(String itemName, String article, String code, String unit, BigDecimal quantity, String containerSerial, String warehouseZoneName, String warehouseLocation, String warehouseName) {
        this.itemName = itemName;
        this.article = article;
        this.code = code;
        this.unit = unit;
        this.quantity = quantity;
        this.containerSerial = containerSerial;
        this.warehouseZoneName = warehouseZoneName;
        this.warehouseLocation = warehouseLocation;
        this.warehouseName = warehouseName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getContainerSerial() {
        return containerSerial;
    }

    public void setContainerSerial(String containerSerial) {
        this.containerSerial = containerSerial;
    }

    public String getWarehouseZoneName() {
        return warehouseZoneName;
    }

    public void setWarehouseZoneName(String warehouseZoneName) {
        this.warehouseZoneName = warehouseZoneName;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }
}

