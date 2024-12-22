package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.DocumentException;
import kz.qasqir.qasqirinventory.api.mapper.DocumentMapper;
import kz.qasqir.qasqirinventory.api.model.dto.DocumentDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Customer;
import kz.qasqir.qasqirinventory.api.model.entity.Document;
import kz.qasqir.qasqirinventory.api.model.entity.Supplier;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.request.DocumentRequest;
import kz.qasqir.qasqirinventory.api.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final SupplierService supplierService;
    private final CustomerService customerService;
    private final UserService userService;
    private final DocumentMapper documentMapper;

    public DocumentService(DocumentRepository documentRepository, SupplierService supplierService, CustomerService customerService, UserService userService, DocumentMapper documentMapper) {
        this.documentRepository = documentRepository;
        this.supplierService = supplierService;
        this.customerService = customerService;
        this.userService = userService;
        this.documentMapper = documentMapper;
    }

    public DocumentDTO addDocument(DocumentRequest documentRequest, Long userId) {
        Supplier supplier = supplierService.getById(documentRequest.getSupplierId());
        Customer customer = customerService.getBuId(documentRequest.getCustomerId());
        User createdBy = userService.getByUserId(documentRequest.getCreatedBy());
        try {
            Document document = new Document();
            document.setDocumentType(documentRequest.getDocumentType());
            document.setDocumentNumber(documentRequest.getDocumentNumber());
            document.setDocumentDate(LocalDate.now());
            document.setSupplier(supplier);
            document.setCustomer(customer);
            document.setCreatedBy(documentRequest.getCreatedBy());
            document.setUpdatedBy(documentRequest.getUpdatedBy());
            document.setCreatedAt(LocalDateTime.now());
            document.setUpdatedAt(LocalDateTime.now());
            documentRepository.save(document);
            return documentMapper.toDto(document);
        } catch (DocumentException e) {
            throw new DocumentException("Ощибка при создании документа");
        }
    }

    public DocumentDTO updateDocument(DocumentRequest documentRequest, Long documentId) {
        Document document = documentRepository.findById(documentId).orElseThrow(() -> new DocumentException("Документ не найден"));
        Supplier supplier = supplierService.getById(documentRequest.getSupplierId());
        Customer customer = customerService.getBuId(documentRequest.getCustomerId());

        try {
            document.setDocumentType(documentRequest.getDocumentType());
            document.setDocumentNumber(documentRequest.getDocumentNumber());
            document.setDocumentDate(LocalDate.now());
            document.setSupplier(supplier);
            document.setCustomer(customer);
            document.setUpdatedBy(documentRequest.getUpdatedBy());
            document.setCreatedAt(LocalDateTime.now());
            document.setUpdatedAt(LocalDateTime.now());
            documentRepository.save(document);
            return documentMapper.toDto(document);
        } catch (DocumentException e) {
            throw new DocumentException("Ощибка при изменении документа");
        }
    }

    public List<DocumentDTO> getAllDocument() {
        return documentRepository.findAll().stream().map(documentMapper::toDto).toList();
    }

    public String deleteById(Long documentId) {
        try {
            documentRepository.deleteById(documentId);
            return "Документ успешно удален";
        } catch (DocumentException e) {
            throw new DocumentException("Ощибка при удалении");
        }
    }

    public Document getById(Long documentId) {
        return documentRepository.findById(documentId).orElseThrow(() -> new DocumentException("Документ не найден"));
    }
}
