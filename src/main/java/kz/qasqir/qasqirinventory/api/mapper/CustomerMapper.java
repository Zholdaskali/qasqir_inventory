package kz.qasqir.qasqirinventory.api.mapper;

import kz.qasqir.qasqirinventory.api.model.dto.CustomerDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Customer;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDTO toDto(Customer customer);

    // Преобразование из DTO в сущность
    Customer toEntity(CustomerDTO customerDTO);
}
