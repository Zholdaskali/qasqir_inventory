package kz.qasqir.qasqirinventory.api.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_nomenclature")
@Data
@NoArgsConstructor
public class Nomenclature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String article;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String type;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "measurement_unit", nullable = false)
    private String measurementUnit;

    @Column(name = "tnved_code")
    private String tnvedCode;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

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

    @Column(name = "volume")
    private Double volume;

    @Column(name = "sync_date")
    private LocalDateTime syncDate;

    public Nomenclature(String name, String article,
                        String code, String type,
                        Category category, String measurementUnit,
                        String tnvedCode, Long createdBy,
                        Long updatedBy, LocalDateTime createdAt,
                        LocalDateTime updatedAt, Double width,
                        Double height, Double length,
                        Double volume, LocalDateTime syncDate
    ) {
        this.name = name;
        this.article = article;
        this.code = code;
        this.type = type;
        this.category = category;
        this.measurementUnit = measurementUnit;
        this.tnvedCode = tnvedCode;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.width = width;
        this.height = height;
        this.length = length;
        this.volume = volume;
        this.syncDate = syncDate;
    }
}

