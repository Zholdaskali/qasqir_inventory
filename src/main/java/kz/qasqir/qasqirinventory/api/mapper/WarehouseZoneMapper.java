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
    @Mapping(source = "warehouse.name", target = "warehouseName")
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "createdBy", target = "createBy")
    @Mapping(source = "updatedBy", target = "updateBy")
    @Mapping(source = "createdAt", target = "createAt")
    @Mapping(source = "updatedAt", target = "updateAt")
    @Mapping(source = "height", target = "height")
    @Mapping(source = "length", target = "length")
    @Mapping(source = "width", target = "width")
    WarehouseZoneDTO toDto(WarehouseZone warehouseZone);

    @Mapping(source = "warehouseId", target = "warehouse.id")
    @Mapping(source = "warehouseName", target = "warehouse.name")
    @Mapping(source = "parentId", target = "parent.id")
    @Mapping(source = "createBy", target = "createdBy")
    @Mapping(source = "updateBy", target = "updatedBy")
    @Mapping(source = "createAt", target = "createdAt")
    @Mapping(source = "updateAt", target = "updatedAt")
    @Mapping(source = "height", target = "height")
    @Mapping(source = "length", target = "length")
    @Mapping(source = "width", target = "width")
    WarehouseZone toEntity(WarehouseZoneDTO warehouseZoneDTO);
}



