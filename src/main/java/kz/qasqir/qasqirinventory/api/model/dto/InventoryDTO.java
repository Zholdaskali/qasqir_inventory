package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryDTO {
    private Long nomenclatureId;
    private BigDecimal quantity;
    private Long warehouseZoneId;
    private String containerSerial;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InventoryDTO(Long nomenclatureId, BigDecimal quantity, Long warehouseZoneId, String containerSerial, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.nomenclatureId = nomenclatureId;
        this.quantity = quantity;
        this.warehouseZoneId = warehouseZoneId;
        this.containerSerial = containerSerial;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getNomenclatureId() {
        return nomenclatureId;
    }

    public void setNomenclatureId(Long nomenclatureId) {
        this.nomenclatureId = nomenclatureId;
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
