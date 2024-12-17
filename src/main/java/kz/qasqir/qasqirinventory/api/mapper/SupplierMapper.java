package kz.qasqir.qasqirinventory.api.mapper;

import kz.qasqir.qasqirinventory.api.model.dto.NomenclatureDTO;
import kz.qasqir.qasqirinventory.api.model.dto.SupplierDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import kz.qasqir.qasqirinventory.api.model.entity.Supplier;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface SupplierMapper {
    // Преобразование из сущности в DTO
    SupplierDTO toDto(Supplier supplier);

    // Преобразование из DTO в сущность
    Supplier toEntity(SupplierDTO supplierDTO);

}
