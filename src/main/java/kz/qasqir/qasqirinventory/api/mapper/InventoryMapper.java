package kz.qasqir.qasqirinventory.api.mapper;

import kz.qasqir.qasqirinventory.api.model.dto.InventoryDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(source = "warehouseZone.id", target = "warehouseZoneId")
    InventoryDTO toDto(Inventory inventory);

    // Преобразование из DTO в сущность
    @Mapping(source = "warehouseZoneId", target = "warehouseZone.id")
    Inventory toEntity(InventoryDTO inventoryDTO);
}
