package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WarehouseStructureDTO {
    private Long id;
    private String name;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<WarehouseZoneStructureDTO> zones;

    public int compareTo(WarehouseZoneStructureDTO other) {
        // Определите естественный порядок здесь, например, по имени
        return this.name.compareTo(other.getName());
    }
}
