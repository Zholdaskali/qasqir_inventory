package kz.qasqir.qasqirinventory.api.mapper;

import kz.qasqir.qasqirinventory.api.model.dto.InventoryDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(source = "warehouseZone.id", target = "warehouseZoneId")
    @Mapping(source = "nomenclature.id", target = "nomenclatureId")
    @Mapping(source = "nomenclature.name", target = "nomenclatureName")
    InventoryDTO toDto(Inventory inventory);
}
