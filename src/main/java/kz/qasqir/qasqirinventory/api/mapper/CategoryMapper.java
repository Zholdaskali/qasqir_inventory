package kz.qasqir.qasqirinventory.api.mapper;
import kz.qasqir.qasqirinventory.api.model.dto.CategoryDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);
 // Привязка ID
    @Mapping(target = "name", source = "name")
    @Mapping(target = "createdBy", source = "createdBy") // Это поле должно соответствовать полю в DTO
    @Mapping(target = "updatedBy", source = "updatedBy") // Это поле должно соответствовать полю в DTO
    @Mapping(target = "createdAt", source = "createdAt") // Это поле должно соответствовать полю в DTO
    @Mapping(target = "updatedAt", source = "updatedAt") // Это поле должно соответствовать полю в DTO
    CategoryDTO toDto(Category category);
// Привязка ID
    @Mapping(target = "name", source = "name")
    @Mapping(target = "createdBy", source = "createdBy") // Здесь используется createBy из DTO
    @Mapping(target = "updatedBy", source = "updatedBy") // Здесь используется updateBy из DTO
    @Mapping(target = "createdAt", source = "createdAt") // Здесь используется createAt из DTO
    @Mapping(target = "updatedAt", source = "updatedAt") // Здесь используется updateAt из DTO
    Category toEntity(CategoryDTO categoryDTO);
}




