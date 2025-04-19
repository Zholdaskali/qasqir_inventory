package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.dto.CustomerDTO;
import kz.qasqir.qasqirinventory.api.model.dto.SupplierDTO;
import kz.qasqir.qasqirinventory.api.model.request.CustomerRequest;
import kz.qasqir.qasqirinventory.api.model.request.SupplierRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.CustomerService;
import kz.qasqir.qasqirinventory.api.service.SupplierService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouse-manager")
public class PartnerController {
    private final SupplierService supplierService;
    private final CustomerService customerService;

    public PartnerController(SupplierService supplierService, CustomerService customerService) {
        this.supplierService = supplierService;
        this.customerService = customerService;
    }

    @PostMapping("/suppliers")
    public MessageResponse<SupplierDTO> addSupplier(@RequestBody SupplierRequest supplierRequest) {
        return MessageResponse.of(supplierService.addSupplier(supplierRequest));
    }

    @DeleteMapping("/suppliers/{supplierId}")
    public MessageResponse<String> deleteSupplier(@PathVariable Long supplierId) {
        return MessageResponse.empty(supplierService.deleteSupplier(supplierId));
    }

    @PutMapping("/suppliers/{supplierId}")
    public MessageResponse<SupplierDTO> updateSupplier(@RequestBody SupplierRequest supplierRequest, @PathVariable Long supplierId) {
        return MessageResponse.of(supplierService.updateSupplier(supplierRequest, supplierId));
    }

    @PostMapping("/customers")
    public MessageResponse<CustomerDTO> addCustomer(@RequestBody CustomerRequest customerRequest) {
        return MessageResponse.of(customerService.addCustomer(customerRequest));
    }

    @PutMapping("/customers/{customerId}")
    public MessageResponse<CustomerDTO> updateCustomer(@RequestBody CustomerRequest customerRequest, @PathVariable Long customerId) {
        return MessageResponse.of(customerService.updateCustomer(customerRequest, customerId));
    }

    @DeleteMapping("/customers/{customerId}")
    public MessageResponse<String> deleteCustomer(@PathVariable Long customerId) {
        return MessageResponse.of(customerService.deleteCustomer(customerId));
    }
}
