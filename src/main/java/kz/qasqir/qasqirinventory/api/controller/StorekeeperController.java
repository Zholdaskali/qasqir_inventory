package kz.qasqir.qasqirinventory.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.model.entity.Document;
import kz.qasqir.qasqirinventory.api.model.entity.InventoryAudit;
import kz.qasqir.qasqirinventory.api.model.request.*;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.DocumentFileService;
import kz.qasqir.qasqirinventory.api.service.DocumentService;
import kz.qasqir.qasqirinventory.api.service.StockTransactionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;


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
    public MessageResponse<Document> saveDocument(@RequestBody DocumentRequest documentRequest) {
        return MessageResponse.of(documentService.addDocument(documentRequest));
    }


    @Operation(
            summary = "Создание ПОСТУПЛЕНИЕ",
            description = "Возвращает сообщение о создании"
    )
    // ПОСТУПЛЕНИЕ
    @PostMapping("/incoming")
    public MessageResponse<String> processIncomingGoods(@RequestBody DocumentRequest documentDTO) {
        stockTransactionService.processIncomingGoods(documentDTO);
        return MessageResponse.empty("Поступление товаров успешно обработано");
    }

    // ИМПОРТИРОВАНИЕ
    @Operation(
            summary = "Создание ИМПОРТИРОВАНИЕ",
            description = "Возвращает сообщение о создании"
    )
    @PostMapping("/import")
    public MessageResponse<String> processImport(@RequestBody DocumentRequest documentDTO) {
        stockTransactionService.processImport(documentDTO);
        return MessageResponse.empty("Импорт успешно обработан");
    }

    // ВОЗВРАТ
    @Operation(
            summary = "Создание ВОЗВРАТ",
            description = "Возвращает сообщение о создании"
    )
    @PostMapping("/return")
    public MessageResponse<String> processReturn(@RequestBody ReturnRequest returnRequest) {
        stockTransactionService.processReturn(returnRequest);
        return MessageResponse.empty("Возврат успешно обработан");
    }

    // ПРОДАЖА
    @Operation(
            summary = "Создание ПРОДАЖА",
            description = "Возвращает сообщение о создании"
    )
    @PostMapping("/sales")
    public MessageResponse<String> processSales(@RequestBody DocumentRequest salesDocument) {
        stockTransactionService.processSales(salesDocument);
        return MessageResponse.empty("Продажи успешно обработаны");
    }

    // ПРОИЗВОДСТВО И ПЕРЕДАЧА
    @Operation(
            summary = "Создание ПРОИЗВОДСТВО И ПЕРЕДАЧА",
            description = "Возвращает сообщение о создании"
    )
    @PostMapping("/production-transfer")
    public MessageResponse<String> processProductionTransfer(@RequestBody DocumentRequest productionDocument) {
        stockTransactionService.processProductionTransfer(productionDocument);
        return MessageResponse.empty("Передача в производство успешно обработана");
    }

    // СПИСАНИЕ
    @Operation(
            summary = "Создание ПРОИЗВОДСТВО И СПИСАНИЕ",
            description = "Возвращает сообщение о создании"
    )
    @PostMapping("/write-off")
    public MessageResponse<String> processWriteOff(@RequestBody DocumentRequest documentDTO) {
        stockTransactionService.processWriteOff(documentDTO);
        return MessageResponse.empty("Списание успешно обработано");
    }

    // ПЕРЕМЕЩЕНИЕ
    @Operation(
            summary = "Создание ПЕРЕМЕЩЕНИЕ",
            description = "Возвращает сообщение о создании"
    )
    @PostMapping("/transfer")
    public MessageResponse<String> processTransfer(@RequestBody TransferRequest transferRequest) {
        stockTransactionService.processTransfer(transferRequest);
        return MessageResponse.empty("Перемещение успешно обработано");
    }

    // ИНВЕНТАРИЗАЦИЯ: Начало инвентаризации
    @Operation(
            summary = "Создание ИНВЕНТАРИЗАЦИЯ: Начало инвентаризации",
            description = "Возвращает сообщение о создании"
    )
    @PostMapping("/inventory-check/start")
    public MessageResponse<String> startInventoryCheck(@RequestParam Long warehouseId, @RequestParam Long createdBy) {
        InventoryAudit audit = stockTransactionService.startInventoryCheck(warehouseId, createdBy);
        return MessageResponse.of(audit.getId() + " Инвентаризация успешно начата");
    }

    // ИНВЕНТАРИЗАЦИЯ: Завершение инвентаризации
    @Operation(
            summary = "Создание ИНВЕНТАРИЗАЦИЯ: Завершение инвентаризации",
            description = "Возвращает сообщение о создании"
    )
    @PostMapping("/inventory-check/process")
    public MessageResponse<String> processInventoryCheck(@RequestParam Long auditId, @RequestBody List<InventoryAuditResultRequest> results) {
        stockTransactionService.processInventoryCheck(auditId, results);
        return MessageResponse.empty("Инвентаризация успешно завершена");
    }

    @Operation(
            summary = "Загрузка файла",
            description = "Возвращает сообщение о создании"
    )
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
