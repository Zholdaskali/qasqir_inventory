package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransactionPlacementDTO {
    private TransactionDTO transactionDTO;
    private WarehouseZoneDTO warehouseZoneDTO;
    private WarehouseContainerDTO warehouseContainerDTO;
    private BigDecimal quantity;
}
//(Transaction transaction,
//WarehouseZone warehouseZone,
//WarehouseContainer warehouseContainer,
//BigDecimal quantity)