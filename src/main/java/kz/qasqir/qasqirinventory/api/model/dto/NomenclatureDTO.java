package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NomenclatureDTO {
    private Long id;
    private String name;
    private String article;
    private String code;
    private String type;
    private Long categoryId;
    private String measurement;
    private String tnved;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double width;
    private Double height;
    private Double volume;
    private Double length;
    private LocalDateTime syncDate;
}

