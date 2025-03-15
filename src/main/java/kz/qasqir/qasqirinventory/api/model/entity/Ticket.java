package kz.qasqir.qasqirinventory.api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "t_ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false, length = 10)
    private String type;

    @Column(name = "status", nullable = false, length = 10)
    private String status;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "create_by", nullable = false)
    private Long createBy;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "managed_at")
    private LocalDateTime managedAt;

    @Column(name = "comment")
    private String comment;

    @Column(name = "inventory_id")
    private Long inventoryId;

    @Column(name = "quantity", precision = 15, scale = 3, nullable = false)
    private BigDecimal quantity = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", insertable = false, updatable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", insertable = false, updatable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", insertable = false, updatable = false)
    private User manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", insertable = false, updatable = false)
    private Inventory inventory;
}