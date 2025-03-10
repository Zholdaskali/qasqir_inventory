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

    @Mapping(source = "id", target = "id")
    @Mapping(source = "nomenclature.id", target = "nomenclatureId")
    @Mapping(source = "nomenclature.name", target = "nomenclatureName")
    @Mapping(source = "nomenclature.measurementUnit", target = "measurementUnit")

    @Mapping(source = "nomenclature.code", target = "code")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "warehouseZone.id", target = "warehouseId")
    @Mapping(source = "warehouseZone.name", target = "warehouseName")
    @Mapping(source = "warehouseContainer.id", target = "containerId")
    @Mapping(source = "warehouseContainer.serialNumber", target = "containerName")
    InventoryItemDTO toDto(Inventory inventory);

//    private Long inventoryId;
//    private Long nomenclatureId;
//    private String nomenclatureName;
//    private String measurementUnit;
//    private String code;
//    private BigDecimal quantity;
//    private Long warehouseId;
//    private String warehouseName;
//    private Long containerId;
//    private String containerName;


//    @Mapping(source = "itemName", target = "nomenclature.name")
//    @Mapping(source = "article", target = "nomenclature.article")
//    @Mapping(source = "code", target = "nomenclature.code")
//    @Mapping(source = "unit", target = "nomenclature.measurementUnit")
//
//    @Mapping(source = "quantity", target = "quantity")
//    @Mapping(source = "warehouseZoneName", target = "warehouseZone.name")
//    @Mapping(source = "warehouseLocation", target = "warehouseZone.warehouse.location")
//    @Mapping(source = "warehouseName", target = "warehouseZone.warehouse.name")
    Inventory toEntity(InventoryItemDTO inventoryItemDTO);

}
