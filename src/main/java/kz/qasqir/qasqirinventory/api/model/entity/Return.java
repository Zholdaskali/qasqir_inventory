package kz.qasqir.qasqirinventory.api.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "t_returns")
public class Return {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "return_type", nullable = false)
    private String returnType;

    @ManyToOne
    @JoinColumn(name = "related_document_id")
    private Document relatedDocument;

    @ManyToOne
    @JoinColumn(name = "nomenclature_id", nullable = false)
    private Nomenclature nomenclature;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity;

    private String reason;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

