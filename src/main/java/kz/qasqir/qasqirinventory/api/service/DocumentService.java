package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.DocumentException;
import kz.qasqir.qasqirinventory.api.mapper.DocumentMapper;
import kz.qasqir.qasqirinventory.api.model.dto.DocumentDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Customer;
import kz.qasqir.qasqirinventory.api.model.entity.Document;
import kz.qasqir.qasqirinventory.api.model.entity.Supplier;
import kz.qasqir.qasqirinventory.api.model.request.DocumentRequest;
import kz.qasqir.qasqirinventory.api.model.request.TransferRequest;
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
    private final DocumentMapper documentMapper;

    public DocumentService(DocumentRepository documentRepository, SupplierService supplierService, CustomerService customerService, DocumentMapper documentMapper) {
        this.documentRepository = documentRepository;
        this.supplierService = supplierService;
        this.customerService = customerService;
        this.documentMapper = documentMapper;
    }

    // Общий метод для создания документа
    private Document createDocument(String documentType, String documentNumber, Long supplierId, Long customerId, String createdBy) {
        if (supplierId == null && customerId == null) {
            throw new DocumentException("Необходимо указать либо поставщика, либо клиента.");
        }

        Supplier supplier = null;
        Customer customer = null;

        if (supplierId != null) {
            supplier = supplierService.getById(supplierId);
        }

        if (customerId != null) {
            customer = customerService.getBuId(customerId);
        }

        if (supplier != null && customer != null) {
            throw new DocumentException("Нельзя указать одновременно и поставщика, и клиента.");
        }

        Document document = new Document();
        document.setDocumentType(documentType);
        document.setDocumentNumber(documentNumber);
        document.setDocumentDate(LocalDate.now());
        document.setSupplier(supplier);
        document.setCustomer(customer);
        document.setCreatedBy(Long.valueOf(createdBy));
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());

        return document;
    }

    // Метод для добавления документа на основе TransferRequest
    public Document addTransferDocument(TransferRequest transferRequest) {
        try {
            return documentRepository.save(
                    createDocument(
                            transferRequest.getDocumentType(),
                            transferRequest.getDocumentNumber(),
                            transferRequest.getSupplierId(),
                            transferRequest.getCustomerId(),
                            transferRequest.getCreatedBy().toString()
                    )
            );
        } catch (DocumentException e) {
            throw new DocumentException("Ошибка при создании документа: " + e.getMessage());
        }
    }

    // Метод для добавления документа на основе DocumentRequest
    public Document addDocument(DocumentRequest documentRequest) {
        try {
            return documentRepository.save(
                    createDocument(
                            documentRequest.getDocumentType(),
                            documentRequest.getDocumentNumber(),
                            documentRequest.getSupplierId(),
                            documentRequest.getCustomerId(),
                            documentRequest.getCreatedBy().toString()
                    )
            );
        } catch (DocumentException e) {
            throw new DocumentException("Ошибка при создании документа: " + e.getMessage());
        }
    }

    // Метод для обновления документа
    public DocumentDTO updateDocument(DocumentRequest documentRequest, Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentException("Документ не найден"));

        try {
            document.setDocumentType(documentRequest.getDocumentType());
            document.setDocumentNumber(documentRequest.getDocumentNumber());
            document.setDocumentDate(LocalDate.now());
            document.setSupplier(supplierService.getById(documentRequest.getSupplierId()));
            document.setCustomer(customerService.getBuId(documentRequest.getCustomerId()));
            document.setUpdatedAt(LocalDateTime.now());

            documentRepository.save(document);
            return documentMapper.toDto(document);
        } catch (DocumentException e) {
            throw new DocumentException("Ошибка при изменении документа: " + e.getMessage());
        }
    }

    // Метод для получения всех документов
    public List<DocumentDTO> getAllDocument() {
        return documentRepository.findAll()
                .stream()
                .map(documentMapper::toDto)
                .toList();
    }

    // Метод для удаления документа по ID
    public String deleteById(Long documentId) {
        try {
            documentRepository.deleteById(documentId);
            return "Документ успешно удален";
        } catch (Exception e) {
            throw new DocumentException("Ошибка при удалении документа: " + e.getMessage());
        }
    }

    // Метод для поиска документа по номеру
    public Document getByDocumentNumber(String documentNumber) {
        return documentRepository.findByDocumentNumber(documentNumber)
                .orElseThrow(() -> new DocumentException("Документ с таким номером не найден"));
    }

    // Метод для поиска документа по ID
    public Document getById(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentException("Документ не найден"));
    }

    // Метод для сохранения документа
    public void saveDocument(Document document) {
        documentRepository.save(document);
    }
}