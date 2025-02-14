package kz.qasqir.qasqirinventory.api.model.request;

import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseContainersRequest {
    private Long warehouseZoneId;
    private String serialNumber;
    private BigDecimal capacity;
    private Double length;
    private Double height;
    private Double width;
}
