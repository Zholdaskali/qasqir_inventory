package kz.qasqir.qasqirinventory.api.model.request;

import kz.qasqir.qasqirinventory.api.repository.WarehouseZoneRepository;

public class WarehouseZoneRequest {
    private Long id;
    private String name;
    private Long parentId;

    public WarehouseZoneRequest(String name, Long parentId) {
        this.name = name;
        this.parentId = parentId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
