package kz.qasqir.qasqirinventory.api.model.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseZoneDTO {
    private Long id;
    private Long warehouseId;
    private String name;
    private Long parentId;
    private Long createBy;
    private Long updateBy;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Double height;
    private Double length;
    private Double width;
}
