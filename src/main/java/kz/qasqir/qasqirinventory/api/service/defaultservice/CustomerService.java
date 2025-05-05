package kz.qasqir.qasqirinventory.api.service.defaultservice;

import kz.qasqir.qasqirinventory.api.exception.CustomerException;
import kz.qasqir.qasqirinventory.api.mapper.CustomerMapper;
import kz.qasqir.qasqirinventory.api.model.dto.CustomerDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Customer;
import kz.qasqir.qasqirinventory.api.model.request.CustomerRequest;
import kz.qasqir.qasqirinventory.api.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository,
                           CustomerMapper customerMapper)
    {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public List<CustomerDTO> getAllCustomer() {
        return customerRepository.findAll().stream().map(customerMapper::toDto).toList();
    }

    public CustomerDTO addCustomer(CustomerRequest customerRequest) {
        try {
            Customer customer = new Customer();
            customer.setName(customerRequest.getName());
            customer.setContactInfo(customerRequest.getContactInfo());
            customer.setCreatedAt(LocalDateTime.now());
            customer.setUpdatedAt(LocalDateTime.now());
            customerRepository.save(customer);
            return customerMapper.toDto(customer);
        } catch (CustomerException e) {
            throw new CustomerException("Ошибка при создании покупателя");
        }
    }

    public CustomerDTO updateCustomer(CustomerRequest customerRequest, Long customerId) {
        try {
            Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerException("Покупатель не найден"));
            customer.setName(customerRequest.getName());
            customer.setContactInfo(customerRequest.getContactInfo());
            customer.setUpdatedAt(LocalDateTime.now());
            customerRepository.save(customer);
            return customerMapper.toDto(customer);
        } catch (CustomerException e) {
            throw new CustomerException("Ошибка при изменении покупателя");
        }
    }

    public String deleteCustomer(Long customerId) {
        try {
            customerRepository.deleteById(customerId);
            return "Заказчик успешно удалено";
        } catch (CustomerException e) {
            throw new CustomerException("Ошибка при удалении покупателя");
        }
    }

    public Customer getById(Long customerId) {
        return customerRepository.findById(customerId).orElseThrow(() -> new CustomerException("Покупатель не найден"));
    }
}
