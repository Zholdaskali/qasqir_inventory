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

    @GetMapping("/suppliers")
    public MessageResponse<List<SupplierDTO>> getAllSupplier() {
        return MessageResponse.of(supplierService.getAllSuppliers());
    }

    @DeleteMapping("/{supplierId}")
    public MessageResponse<String> deleteSupplier(@PathVariable Long supplierId) {
        return MessageResponse.empty(supplierService.deleteSupplier(supplierId));
    }

    @PutMapping("/{supplierId}")
    public MessageResponse<SupplierDTO> updateSupplier(@RequestBody SupplierRequest supplierRequest, @PathVariable Long supplierId) {
        return MessageResponse.of(supplierService.updateSupplier(supplierRequest, supplierId));
    }

    @GetMapping("/customers")
    public MessageResponse<List<CustomerDTO>> getAllCustomer() {
        return MessageResponse.of(customerService.getAllCustomer());
    }

    @PostMapping("/customers")
    public MessageResponse<CustomerDTO> saveCustomer(@RequestBody CustomerRequest customerRequest, Long customerId) {
        return MessageResponse.of(customerService.saveCustomer(customerRequest));
    }

    @PutMapping("/{customerId}")
    public MessageResponse<CustomerDTO> updateCustomer(@RequestBody CustomerRequest customerRequest, Long customerId) {
        return MessageResponse.of(customerService.updateCustomer(customerRequest, customerId));
    }

    @DeleteMapping("/{customerId}")
    public MessageResponse<String> deleteCustomer(@PathVariable Long customerId) {
        return MessageResponse.of(customerService.deleteCustomer(customerId));
    }
}