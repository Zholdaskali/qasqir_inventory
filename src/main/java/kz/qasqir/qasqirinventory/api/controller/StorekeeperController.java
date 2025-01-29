package kz.qasqir.qasqirinventory.api.controller;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.model.dto.DocumentDTO;
import kz.qasqir.qasqirinventory.api.model.request.DocumentRequest;
import kz.qasqir.qasqirinventory.api.model.request.ReturnRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.DocumentFileService;
import kz.qasqir.qasqirinventory.api.service.DocumentService;
import kz.qasqir.qasqirinventory.api.service.StockTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import kz.qasqir.qasqirinventory.api.model.request.DocumentFileRequest;
import kz.qasqir.qasqirinventory.api.model.entity.DocumentFile;

import java.util.Arrays;


@RestController
@RequestMapping("/api/v1/storekeeper")
public class StorekeeperController {

    private final DocumentService documentService;
    private final StockTransactionService stockTransactionService;
    private final DocumentFileService documentFileService;

    public StorekeeperController(DocumentService documentService, StockTransactionService stockTransactionService, DocumentFileService documentFileService) {
        this.documentService = documentService;
        this.stockTransactionService = stockTransactionService;
        this.documentFileService = documentFileService;
    }

    @Transactional
    @PostMapping("/document/add")
    public MessageResponse<DocumentDTO> saveDocument(@RequestBody DocumentRequest documentRequest) {
        DocumentDTO documentDTO = documentService.addDocument(documentRequest);
        return MessageResponse.of(documentDTO);
    }

    // ПОСТУПЛЕНИЕ
    @PostMapping("/transactions/incoming")
    public MessageResponse<String> processIncomingGoods(@RequestBody DocumentDTO documentDTO) {

        stockTransactionService.processIncomingGoods(documentDTO);
        return MessageResponse.empty("Поступление товаров успешно обработано");
    }

    // ИМПОРТИРОВАНИЕ
    @PostMapping("/transactions/import")
    public MessageResponse<String> processImport(@RequestBody DocumentDTO documentDTO) {
        stockTransactionService.processImport(documentDTO);
        return MessageResponse.empty("Импорт успешно обработан");
    }

    // ВОЗВРАТ
    @PostMapping("/transactions/return")
    public MessageResponse<String> processReturn(@RequestBody ReturnRequest returnRequest) {
        stockTransactionService.processReturn(returnRequest);
        return MessageResponse.empty("Возврат успешно обработан");
    }

    // ПРОДАЖА
    @PostMapping("/transactions/sales")
    public MessageResponse<String> processSales(@RequestBody DocumentDTO salesDocument) {
        stockTransactionService.processSales(salesDocument);
        return MessageResponse.empty("Продажи успешно обработаны");
    }

    // ПРОИЗВОДСТВО И ПЕРЕДАЧА
    @PostMapping("/transactions/production-transfer")
    public MessageResponse<String> processProductionTransfer(@RequestBody DocumentDTO productionDocument) {
        stockTransactionService.processProductionTransfer(productionDocument);
        return MessageResponse.empty("Передача в производство успешно обработана");
    }

    @PostMapping("/file/upload")
    public MessageResponse<String> uploadFile(@RequestParam("documentId") Long documentId,
                                        @RequestParam("file") MultipartFile file) {
        try {
            byte[] fileData = file.getBytes();
            DocumentFileRequest request = new DocumentFileRequest();
            request.setDocumentId(documentId);
            request.setFileName(file.getOriginalFilename());
            request.setFileData(fileData);

            return MessageResponse.empty(documentFileService.saveDocumentFile(request));
        } catch (Exception e) {
            return MessageResponse.empty("Ошибка при сохранении файла: " + e.getMessage());
        }
    }
}
