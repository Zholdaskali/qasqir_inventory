package kz.qasqir.qasqirinventory.api.mapper;

import kz.qasqir.qasqirinventory.api.model.dto.NomenclatureDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface NomenclatureMapper {

    // Преобразование из сущности в DTO
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "tnvedCode", target = "tnved")
    @Mapping(source = "measurementUnit", target = "measurement")
    NomenclatureDTO toDto(Nomenclature nomenclature);

    // Преобразование из DTO в сущность
    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(source = "tnved", target = "tnvedCode")
    @Mapping(source = "measurement", target = "measurementUnit")
    Nomenclature toEntity(NomenclatureDTO nomenclatureDTO);
}
