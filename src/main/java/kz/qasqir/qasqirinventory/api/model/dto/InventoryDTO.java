package kz.qasqir.qasqirinventory.api.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryDTO {
    private Long id;
    private BigDecimal quantity;
    private Long warehouseZoneId;
    private String containerSerial;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InventoryDTO(Long nomenclatureId, BigDecimal quantity, Long warehouseZoneId, String containerSerial, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = nomenclatureId;
        this.quantity = quantity;
        this.warehouseZoneId = warehouseZoneId;
        this.containerSerial = containerSerial;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Long getWarehouseZoneId() {
        return warehouseZoneId;
    }

    public void setWarehouseZoneId(Long warehouseZoneId) {
        this.warehouseZoneId = warehouseZoneId;
    }

    public String getContainerSerial() {
        return containerSerial;
    }

    public void setContainerSerial(String containerSerial) {
        this.containerSerial = containerSerial;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
