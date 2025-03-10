package kz.qasqir.qasqirinventory.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.model.dto.*;
import kz.qasqir.qasqirinventory.api.model.entity.Document;
import kz.qasqir.qasqirinventory.api.model.entity.DocumentFile;
import kz.qasqir.qasqirinventory.api.model.request.*;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/storekeeper")
public class StorekeeperController {

    private final DocumentService documentService;
    private final ProcessSalesAndTransferService processSalesAndTransferService;
    private final DocumentFileService documentFileService;
    private final InventoryAuditService inventoryAuditService;
    private final TransactionService transactionService;
    private final ProcessIncomingService processIncomingService;
    private final ProcessInventoryCheckService processInventoryCheck;
    private final ProcessTransferService processTransferService;
    private final ProcessReturnService processReturnService;
    private final ProcessWriteOffService writeOffService;
    private final InventoryAuditResultService inventoryAuditResultService;

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
        processIncomingService.processIncomingGoods(documentDTO);
        return MessageResponse.empty("Поступление товаров успешно обработано");
    }

    // ИМПОРТИРОВАНИЕ
    @Operation(
            summary = "Создание ИМПОРТИРОВАНИЕ",
            description = "Возвращает сообщение о создании"
    )
    @PostMapping("/import")
    public MessageResponse<String> processImport(@RequestBody DocumentRequest documentDTO) {
        processIncomingService.processImport(documentDTO);
        return MessageResponse.empty("Импорт успешно обработан");
    }

    // ВОЗВРАТ
    @Operation(
            summary = "Создание ВОЗВРАТ",
            description = "Возвращает сообщение о создании"
    )
    @PostMapping("/return")
    public MessageResponse<String> processReturn(@RequestBody ReturnRequest returnRequest) {
        processReturnService.processReturn(returnRequest);
        return MessageResponse.empty("Возврат успешно обработан");
    }

    // ПРОДАЖА
    @Operation(
            summary = "Создание ПРОДАЖА",
            description = "Возвращает сообщение о создании"
    )
    @PostMapping("/sales")
    public MessageResponse<String> processSales(@RequestBody DocumentRequest salesDocument) {
        processSalesAndTransferService.processSales(salesDocument);
        return MessageResponse.empty("Продажи успешно обработаны");
    }

    // ПРОИЗВОДСТВО И ПЕРЕДАЧА
    @Operation(
            summary = "Создание ПРОИЗВОДСТВО И ПЕРЕДАЧА",
            description = "Возвращает сообщение о создании"
    )
    @PostMapping("/production-transfer")
    public MessageResponse<String> processProductionTransfer(@RequestBody DocumentRequest productionDocument) {
        processSalesAndTransferService.processProductionTransfer(productionDocument);
        return MessageResponse.empty("Передача в производство успешно обработана");
    }

    // СПИСАНИЕ
    @Operation(
            summary = "Создание ПРОИЗВОДСТВО И СПИСАНИЕ",
            description = "Возвращает сообщение о создании"
    )
    @PostMapping("/write-off")
    public MessageResponse<String> processWriteOff(@RequestBody ReturnRequest writeOffRequest) {
        writeOffService.processWriteOff(writeOffRequest);
        return MessageResponse.empty("Списание успешно обработано");
    }

    // ПЕРЕМЕЩЕНИЕ
    @Operation(
            summary = "Создание ПЕРЕМЕЩЕНИЕ",
            description = "Возвращает сообщение о создании"
    )
    @PostMapping("/transfer")
    public MessageResponse<String> processTransfer(@RequestBody TransferRequest transferRequest) {
        processTransferService.processTransfer(transferRequest);
        return MessageResponse.empty("Перемещение успешно обработано");
    }

    // ИНВЕНТАРИЗАЦИЯ: Начало инвентаризации
    @Operation(
            summary = "Создание ИНВЕНТАРИЗАЦИЯ: Начало инвентаризации",
            description = "Возвращает сообщение о создании"
    )
    @PostMapping("/inventory-check/start")
    public MessageResponse<String> startInventoryCheck(@RequestParam Long warehouseId, @RequestParam Long createdBy) {
        return MessageResponse.empty(processInventoryCheck.startInventoryCheck(warehouseId, createdBy));
    }

    // ИНВЕНТАРИЗАЦИЯ: Завершение инвентаризации
    @Operation(
            summary = "Создание ИНВЕНТАРИЗАЦИЯ: Завершение инвентаризации",
            description = "Возвращает сообщение о создании"
    )
    @PostMapping("/inventory-check/process/{inventoryId}")
    public MessageResponse<String> processInventoryCheck(@PathVariable Long inventoryId, @RequestBody List<InventoryAuditResultRequest> results) {
        return MessageResponse.empty(processInventoryCheck.processInventoryCheck(inventoryId, results));
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

            return MessageResponse.of(documentFileService.saveDocumentFile(request));
        } catch (Exception e) {
            return MessageResponse.empty("Ошибка при сохранении файла: " + e.getMessage());
        }
    }

    @GetMapping("/inventory-check/in-progress")
    public MessageResponse<List<InventoryAuditDTO>> getInventoryCheckInProgress() {
        return MessageResponse.of(inventoryAuditService.getAllInProgressInventoryAudit());
    }

    @GetMapping("/inventory-check/completed")
    public MessageResponse<List<InventoryAuditDTO>> getInventoryCheckCompleted() {
        return MessageResponse.of(inventoryAuditService.getAllInCompletedInventoryAudit());
    }

    @GetMapping("/inventory-check")
    public MessageResponse<List<InventoryAuditDTO>> getAllInventoryCheck() {
        return MessageResponse.of(inventoryAuditService.getAllInventoryAudit());
    }

    @GetMapping("/inventory-check/{inventoryId}")
    public MessageResponse<InventoryAuditDTO> getByInventoryAuditId(@PathVariable Long inventoryId) {
        return MessageResponse.of(inventoryAuditService.getById(inventoryId));
    }

    @GetMapping("/transaction")
    public MessageResponse<List<TransactionDTO>> getAllTransaction() {
        return MessageResponse.of(transactionService.getAllTransaction());
    }

    @GetMapping("/file")
    public MessageResponse<List<DocumentFileDTO>> getDocumentFile() {
        return MessageResponse.of(documentFileService.getDocumentFile());
    }

    @GetMapping("/inventory-check/result/{auditId}")
    public MessageResponse<List<InventoryAuditResultDTO>> getInventoryAudit(@PathVariable Long auditId) {
        return MessageResponse.of(inventoryAuditResultService.getAllByAuditId(auditId));
    }
}
