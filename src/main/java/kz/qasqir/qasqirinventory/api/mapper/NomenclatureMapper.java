package kz.qasqir.qasqirinventory.api.mapper;

import kz.qasqir.qasqirinventory.api.model.dto.NomenclatureDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface NomenclatureMapper {

    // Преобразование из сущности в DTO
    NomenclatureDTO toDto(Nomenclature nomenclature);

    // Преобразование из DTO в сущность
    Nomenclature toEntity(NomenclatureDTO nomenclatureDTO);
}
