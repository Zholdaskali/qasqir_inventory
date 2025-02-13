package kz.qasqir.qasqirinventory.api.model.request;

import kz.qasqir.qasqirinventory.api.repository.WarehouseZoneRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class WarehouseZoneRequest {
    private Long id;
    private String name;
    private Double height;
    private Double length;
    private Double width;
    private Long parentId;
}
