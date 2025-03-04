package kz.qasqir.qasqirinventory.api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_inventory")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nomenclature_id", nullable = false)
    private Nomenclature nomenclature;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_zone_id", nullable = false)
    private WarehouseZone warehouseZone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_container_id")
    private WarehouseContainer warehouseContainer;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Inventory(Nomenclature nomenclature, BigDecimal quantity, WarehouseZone warehouseZone) {
        this.nomenclature = nomenclature;
        this.quantity = quantity;
        this.warehouseZone = warehouseZone;
    }
}

