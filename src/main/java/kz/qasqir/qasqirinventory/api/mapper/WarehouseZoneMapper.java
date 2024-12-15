package kz.qasqir.qasqirinventory.api.mapper;

import kz.qasqir.qasqirinventory.api.model.dto.WarehouseZoneDTO;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface WarehouseZoneMapper {

    WarehouseZoneMapper INSTANCE = Mappers.getMapper(WarehouseZoneMapper.class);

    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "createdBy", target = "createBy")  // Маппинг для createBy
    @Mapping(source = "updatedBy", target = "updateBy")  // Маппинг для updateBy
    @Mapping(source = "createdAt", target = "createAt")  // Маппинг для createAt
    @Mapping(source = "updatedAt", target = "updateAt")  // Маппинг для updateAt
    WarehouseZoneDTO toDto(WarehouseZone warehouseZone);

    @Mapping(source = "warehouseId", target = "warehouse.id")
    @Mapping(source = "parentId", target = "parent.id")
    @Mapping(source = "createBy", target = "createdBy")  // Обратный маппинг для createBy
    @Mapping(source = "updateBy", target = "updatedBy")  // Обратный маппинг для updateBy
    @Mapping(source = "createAt", target = "createdAt")  // Обратный маппинг для createAt
    @Mapping(source = "updateAt", target = "updatedAt")  // Обратный маппинг для updateAt
    WarehouseZone toEntity(WarehouseZoneDTO warehouseZoneDTO);
}


