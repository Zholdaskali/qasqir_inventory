package kz.qasqir.qasqirinventory.api.mapper;

import kz.qasqir.qasqirinventory.api.model.dto.InventoryDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Inventory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    InventoryDTO toDto(Inventory inventory);

    // Преобразование из DTO в сущность
    Inventory toEntity(InventoryDTO inventoryDTO);
}
