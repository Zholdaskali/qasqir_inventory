package kz.qasqir.qasqirinventory.api.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemRequest {
    private Long nomenclatureId; // ID товара
    private String nomenclatureName; // Наименование товара
    private BigDecimal quantity; // Количество товара
    private String measurementUnit; // Единица измерения товара
    private Long warehouseZoneId; // ID зоны склада, куда поступает товар
    private String warehouseZoneName; // Название зоны склада
    private String containerSerial; // Серийный номер контейнера (если применимо)
    private boolean isReturnable; // Флаг возвратной тары (если применимо)
}