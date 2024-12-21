package kz.qasqir.qasqirinventory.api.model.dto;

import java.util.List;

public class WarehouseZoneItemDTO {
    private String zoneName; // Название зоны
    private List<InventoryItemDTO> items; // Список товаров в данной зоне

    public WarehouseZoneItemDTO(String zoneName, List<InventoryItemDTO> items) {
        this.zoneName = zoneName;
        this.items = items;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public List<InventoryItemDTO> getItems() {
        return items;
    }

    public void setItems(List<InventoryItemDTO> items) {
        this.items = items;
    }

}

