package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.SupplierException;
import kz.qasqir.qasqirinventory.api.mapper.SupplierMapper;
import kz.qasqir.qasqirinventory.api.model.dto.SupplierDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Supplier;
import kz.qasqir.qasqirinventory.api.model.request.SupplierRequest;
import kz.qasqir.qasqirinventory.api.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public SupplierService(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }

    public SupplierDTO addSupplier(SupplierRequest supplierRequest) {
        try {
            Supplier supplier = new Supplier();
            supplier.setName(supplierRequest.getName());
            supplier.setContactInfo(supplierRequest.getContactInfo());
            supplier.setCreatedAt(Timestamp.from(Instant.now()).toLocalDateTime());
            supplier.setUpdatedAt(Timestamp.from(Instant.now()).toLocalDateTime());
            supplierRepository.save(supplier);
            return supplierMapper.toDto(supplier);
        } catch (SupplierException e) {
            throw new SupplierException("Ошибка при создании поставщика");
        }
    }

    public List<SupplierDTO> getAllSuppliers() {
        try {
            return supplierRepository.findAll().stream().map(supplierMapper :: toDto).toList();
        } catch (Exception e) {
            throw new SupplierException("Ошибка при выводе поставщиков");
        }
    }

    public String deleteSupplier(Long supplierId) {
        try {
            supplierRepository.deleteById(supplierId);
            return "Поставщик успешно удалено";
        } catch (SupplierException e) {
            throw new SupplierException("Ошибка при удалении поставщиков");
        }
    }

    public SupplierDTO updateSupplier(SupplierRequest supplierRequest, Long supplierId) {
        try {
            Supplier supplier = supplierRepository.findById(supplierId).orElseThrow(() -> new SupplierException("Поставщик не найден"));
            supplier.setName(supplierRequest.getName());
            supplier.setUpdatedAt(Timestamp.from(Instant.now()).toLocalDateTime());
            supplier.setContactInfo(supplierRequest.getContactInfo());
            supplierRepository.save(supplier);
            return supplierMapper.toDto(supplier);
        } catch (RuntimeException e) {
            throw new SupplierException("Ошибка при обновлении данных поставшика");
        }
    }

    public Supplier getById(Long supplierId) {
        return supplierRepository.findById(supplierId).orElseThrow(() -> new SupplierException("Поставшик не найден"));
    }
}
