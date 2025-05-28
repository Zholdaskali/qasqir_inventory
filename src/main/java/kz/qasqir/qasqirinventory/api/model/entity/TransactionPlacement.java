package kz.qasqir.qasqirinventory.api.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_transaction_placements")
@Getter
@Setter
@NoArgsConstructor
public class TransactionPlacement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_zone_id")
    private WarehouseZone warehouseZone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_container_id")
    private WarehouseContainer warehouseContainer;

    @Column(name = "quantity", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public TransactionPlacement(Transaction transaction, WarehouseZone warehouseZone, WarehouseContainer warehouseContainer, BigDecimal quantity) {
        this.transaction = transaction;
        this.warehouseZone = warehouseZone;
        this.warehouseContainer = warehouseContainer;
        this.quantity = quantity;
    }
}
