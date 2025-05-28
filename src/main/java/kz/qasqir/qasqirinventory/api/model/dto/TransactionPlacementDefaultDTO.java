package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionPlacementDefaultDTO {
    private WarehouseZoneDTO warehouseZoneDTO;
    private WarehouseContainerDTO warehouseContainerDTO;
    private BigDecimal quantity;
}
