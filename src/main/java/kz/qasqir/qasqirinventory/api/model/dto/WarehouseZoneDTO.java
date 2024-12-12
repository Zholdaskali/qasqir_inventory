package kz.qasqir.qasqirinventory.api.model.dto;

import java.time.LocalDateTime;

public class WarehouseZoneDTO {
    private Long id;
    private Long warehouseId;
    private String name;
    private Long parentId;
    private Long createBy;
    private Long updateBy;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public WarehouseZoneDTO(Long id, Long warehouseId, String name, Long parentId, Long createBy, Long updateBy, LocalDateTime createAt, LocalDateTime updateAt) {
        this.id = id;
        this.warehouseId = warehouseId;
        this.name = name;
        this.parentId = parentId;
        this.createBy = createBy;
        this.updateBy = updateBy;
        this.createAt = createAt;
        this.updateAt = updateAt;
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

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
}
