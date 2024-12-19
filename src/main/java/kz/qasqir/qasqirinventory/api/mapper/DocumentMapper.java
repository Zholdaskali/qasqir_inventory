package kz.qasqir.qasqirinventory.api.mapper;

import kz.qasqir.qasqirinventory.api.model.dto.DocumentDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Document;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    DocumentDTO toDto(Document document);

    // Преобразование из DTO в сущность
    Document toEntity(DocumentDTO documentDTO);

}
