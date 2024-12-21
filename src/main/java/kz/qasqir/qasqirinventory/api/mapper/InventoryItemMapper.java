package kz.qasqir.qasqirinventory.api.mapper;

import kz.qasqir.qasqirinventory.api.model.dto.DocumentDTO;
import kz.qasqir.qasqirinventory.api.model.dto.InventoryItemDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Document;
import kz.qasqir.qasqirinventory.api.model.entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface InventoryItemMapper {

    @Mapping(source = "nomenclature.name", target = "itemName")
    @Mapping(source = "nomenclature.article", target = "article")
    @Mapping(source = "nomenclature.code", target = "code")
    @Mapping(source = "inventory.nomenclature.measurementUnit", target = "unit")

    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "warehouseZone.name", target = "warehouseZoneName")
    @Mapping(source = "warehouseZone.warehouse.location", target = "warehouseLocation")
    @Mapping(source = "warehouseZone.warehouse.name", target = "warehouseName")
    @Mapping(source = "containerSerial", target = "containerSerial")
    InventoryItemDTO toDto(Inventory inventory);

    @Mapping(source = "itemName", target = "nomenclature.name")
    @Mapping(source = "article", target = "nomenclature.article")
    @Mapping(source = "code", target = "nomenclature.code")
    @Mapping(source = "unit", target = "nomenclature.measurementUnit")

    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "warehouseZoneName", target = "warehouseZone.name")
    @Mapping(source = "warehouseLocation", target = "warehouseZone.warehouse.location")
    @Mapping(source = "warehouseName", target = "warehouseZone.warehouse.name")
    @Mapping(source = "containerSerial", target = "containerSerial")
    Inventory toEntity(InventoryItemDTO inventoryItemDTO);


//
//    private String itemName; // Название товара
//    private String article; // Артикул товара
//    private String code; // Код товара
//    private String unit; // Единица измерения
//    private BigDecimal quantity; // Количество товара
//    private String containerSerial; // Серийный номер контейнера (если применимо)
//    private String warehouseZoneName; // Название зоны на складе
//    private String warehouseLocation;
}
