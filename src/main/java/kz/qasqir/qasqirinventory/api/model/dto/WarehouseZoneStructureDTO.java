package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseZoneStructureDTO {
    private Long id;
    private String name;
    private Long parentId;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double width;
    private Double height;
    private Double length;
    private BigDecimal capacity;
    private Boolean canStoreItems;
    private List<WarehouseZoneStructureDTO> childZones;
    private List<WarehouseContainerDTO> containers;
}
