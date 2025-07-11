package kz.qasqir.qasqirinventory.api.service.document;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.DocumentException;
import kz.qasqir.qasqirinventory.api.model.dto.DocumentDTO;
import kz.qasqir.qasqirinventory.api.model.dto.DocumentWithTransactionsDTO;
import kz.qasqir.qasqirinventory.api.model.dto.TransactionDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Customer;
import kz.qasqir.qasqirinventory.api.model.entity.Document;
import kz.qasqir.qasqirinventory.api.model.entity.Supplier;
import kz.qasqir.qasqirinventory.api.model.entity.Transaction;
import kz.qasqir.qasqirinventory.api.model.enums.OperationType;
import kz.qasqir.qasqirinventory.api.model.request.DocumentRequest;
import kz.qasqir.qasqirinventory.api.model.request.TransferRequest;
import kz.qasqir.qasqirinventory.api.repository.DocumentRepository;
import kz.qasqir.qasqirinventory.api.repository.TransactionRepository;
import kz.qasqir.qasqirinventory.api.service.partner.CustomerService;
import kz.qasqir.qasqirinventory.api.service.partner.SupplierService;
import kz.qasqir.qasqirinventory.api.service.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final SupplierService supplierService;
    private final CustomerService customerService;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    @Transactional(rollbackOn = Exception.class)
    public Document createDocument(String documentType, String documentNumber, Long supplierId, Long customerId, Long createdBy) {

        if (documentRepository.existsByDocumentNumber(documentNumber)) {
            throw new DocumentException("Документ с номером " + documentNumber + " уже существует.");
        }
            if (OperationType.TRANSFER.name().equals(documentType)
                    || OperationType.WRITE_OFF.name().equals(documentType)
                    || OperationType.PRODUCTION.name().equals(documentType)
                    || OperationType.RETURN.name().equals(documentType)
                    || OperationType.ONE_C_SALES.name().equals(documentType)
            ) {
            Document transferDocument = new Document();
            transferDocument.setDocumentType(documentType);
            transferDocument.setDocumentNumber(documentNumber);
            transferDocument.setDocumentDate(LocalDate.now());
            transferDocument.setSupplier(null);
            transferDocument.setCustomer(null);
            transferDocument.setCreatedBy(createdBy);
            transferDocument.setCreatedAt(LocalDateTime.now());
            transferDocument.setUpdatedAt(LocalDateTime.now());
            return documentRepository.save(transferDocument);
        }

        if (supplierId == null && customerId == null) {
            throw new DocumentException("Необходимо указать либо поставщика, либо клиента.");
        }

        Supplier supplier = (supplierId != null) ? supplierService.getById(supplierId) : null;
        Customer customer = (customerId != null) ? customerService.getById(customerId) : null;

        if (supplier != null && customer != null) {
            throw new DocumentException("Нельзя указать одновременно и поставщика, и клиента.");
        }

        if (supplierId != null && supplier == null) {
            throw new DocumentException("Поставщик с ID " + supplierId + " не найден.");
        }
        if (customerId != null && customer == null) {
            throw new DocumentException("Клиент с ID " + customerId + " не найден.");
        }

        Document document = new Document();
        document.setDocumentType(documentType);
        document.setDocumentNumber(documentNumber);
        document.setDocumentDate(LocalDate.now());
        document.setSupplier(supplier);
        document.setCustomer(customer);
        document.setCreatedBy(createdBy);
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());

        return documentRepository.save(document);
    }


    // Метод для добавления документа на основе TransferRequest
    @Transactional(rollbackOn = Exception.class)
    public Document addTransferDocument(TransferRequest transferRequest) {
        try {
            return
                    createDocument(
                            transferRequest.getDocumentType(),
                            transferRequest.getDocumentNumber(),
                            null,
                            null,
                            transferRequest.getCreatedBy()
            );
        } catch (DocumentException e) {
            throw new DocumentException("Ошибка при создании документа: " + e.getMessage());
        }
    }

    // Метод для добавления документа на основе DocumentRequest
    public Document addDocument(DocumentRequest documentRequest) {
        try {
            return createDocument(
                            documentRequest.getDocumentType(),
                            documentRequest.getDocumentNumber(),
                            documentRequest.getSupplierId(),
                            documentRequest.getCustomerId(),
                            documentRequest.getCreatedBy()
            );
        } catch (DocumentException e) {
            throw new DocumentException("Ошибка при создании документа: " + e.getMessage());
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public DocumentDTO updateDocument(DocumentRequest documentRequest, Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentException("Документ не найден"));

        try {
            document.setDocumentType(documentRequest.getDocumentType());
            document.setDocumentNumber(documentRequest.getDocumentNumber());
            document.setDocumentDate(LocalDate.now());
            document.setSupplier(supplierService.getById(documentRequest.getSupplierId()));
            document.setCustomer(customerService.getById(documentRequest.getCustomerId()));
            document.setUpdatedAt(LocalDateTime.now());

            documentRepository.save(document);
            return convertToDto(document);
        } catch (DocumentException e) {
            throw new DocumentException("Ошибка при изменении документа: " + e.getMessage());
        }
    }

    // Метод для получения всех документов
    public List<DocumentDTO> getAllDocument() {
        return documentRepository.findAll()
                .stream()
                .map(this::convertToDto)
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

    public List<DocumentWithTransactionsDTO> getDocumentsWithTransactions(LocalDate startDate, LocalDate endDate) {
        // 1. Загружаем документы с поставщиками за 1 запрос
        List<Document> documents = documentRepository.findByDocumentDateBetweenWithJoins(startDate, endDate);
        List<Long> documentIds = documents.stream().map(Document::getId).toList();

        // 2. Загружаем транзакции с номенклатурой и пользователями за 1 запрос
        List<Transaction> transactions = transactionRepository.findByDocumentIdInWithJoins(documentIds);

        // 3. Группируем транзакции по ID документа (а не по ID транзакции!)
        Map<Long, List<TransactionDTO>> transactionsMap = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getDocument().getId(),
                        Collectors.mapping(transactionService::convertToDto, Collectors.toList())
                ));

        // 4. Собираем итоговый DTO
        return documents.stream()
                .map(document -> new DocumentWithTransactionsDTO(
                        convertToDto(document),
                        transactionsMap.getOrDefault(document.getId(), new ArrayList<>())
                ))
                .collect(Collectors.toList());
    }

    public DocumentDTO convertToDto(Document document) {
        String supplierName = (document.getSupplier() != null) ? document.getSupplier().getName() : "Не имеется";
        String customerName = (document.getCustomer() != null) ? document.getCustomer().getName() : "Не имеется";

        return new DocumentDTO(
                document.getId(),
                document.getDocumentType(),
                document.getDocumentNumber(),
                document.getDocumentDate(),
                supplierName,
                customerName,
                document.getCreatedAt(),
                document.getCreatedBy()
        );
    }


}