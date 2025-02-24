package kz.qasqir.qasqirinventory.api.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "t_warehouse_containers")
public class WarehouseContainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "warehouse_zone_id", nullable = false)
    private WarehouseZone warehouseZone;

    @Column(name = "serial_number", nullable = false)
    private String serialNumber;

    @Column(precision = 15, scale = 3)
    private BigDecimal capacity;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "width")
    private Double width;

    @Column(name = "height")
    private Double height;

    @Column(name = "length")
    private Double length;
}

