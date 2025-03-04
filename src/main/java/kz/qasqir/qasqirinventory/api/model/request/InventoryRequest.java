package kz.qasqir.qasqirinventory.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class InventoryRequest {
    private Long nomenclatureId; // ID номенклатуры
    private Long warehouseZoneId; // ID зоны склада
    private BigDecimal quantity; // Количество
    private Long containerId; // Серийный номер контейнера
}

