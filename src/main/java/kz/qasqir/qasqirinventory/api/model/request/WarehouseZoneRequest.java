package kz.qasqir.qasqirinventory.api.model.request;

import kz.qasqir.qasqirinventory.api.repository.WarehouseZoneRepository;

public class WarehouseZoneRequest {
    private Long id;
    private Long warehouseId;
    private String name;
    private Long parentId;

    public WarehouseZoneRequest(Long warehouseId, String name, Long parentId) {
        this.warehouseId = warehouseId;
        this.name = name;
        this.parentId = parentId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
