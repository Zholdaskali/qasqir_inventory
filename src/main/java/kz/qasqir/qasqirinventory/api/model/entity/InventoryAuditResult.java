package kz.qasqir.qasqirinventory.api.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_inventory_audit_results")
public class InventoryAuditResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "audit_id", nullable = false)
    private InventoryAudit audit;

    @ManyToOne
    @JoinColumn(name = "nomenclature_id", nullable = false)
    private Nomenclature nomenclature;

    @ManyToOne
    @JoinColumn(name = "warehouse_zone_id", nullable = false)
    private WarehouseZone warehouseZone;

    @Column(name = "expected_quantity", nullable = false)
    private BigDecimal expectedQuantity;

    @Column(name = "actual_quantity", nullable = false)
    private BigDecimal actualQuantity;

    @Column(name = "discrepancy", nullable = false)
    private BigDecimal discrepancy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}