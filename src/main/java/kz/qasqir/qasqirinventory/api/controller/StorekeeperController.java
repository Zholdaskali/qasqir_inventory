package kz.qasqir.qasqirinventory.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.model.dto.*;
import kz.qasqir.qasqirinventory.api.model.entity.Document;
import kz.qasqir.qasqirinventory.api.model.entity.DocumentFile;
import kz.qasqir.qasqirinventory.api.model.request.*;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/storekeeper")
public class StorekeeperController {

    private final DocumentService documentService;
    private final ProcessSalesAndWriteOffAndProductionService processSalesAndTransferService;
    private final DocumentFileService documentFileService;
    private final InventoryAuditService inventoryAuditService;
    private final ProcessIncomingService processIncomingService;
    private final ProcessInventoryCheckService processInventoryCheck;
    private final ProcessTransferService processTransferService;
    private final ProcessReturnService processReturnService;
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
    public MessageResponse<InventoryAuditDTO> startInventoryCheck(@RequestParam Long warehouseId, @RequestParam Long createdBy) {
        return MessageResponse.of(processInventoryCheck.startInventoryCheck(warehouseId, createdBy));
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

    @GetMapping("/document")
    public MessageResponse<List<DocumentDTO>> getAllDocument() {
        return MessageResponse.of(documentService.getAllDocument());
    }


    @GetMapping("/inventory-check/in-progress")
    public MessageResponse<List<InventoryAuditDTO>> getInventoryCheckInProgress(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)  {
        return MessageResponse.of(inventoryAuditService.getAllInProgressInventoryAudit(startDate, endDate));
    }

    @GetMapping("/inventory-check/completed")
    public MessageResponse<List<InventoryAuditDTO>> getInventoryCheckCompleted(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)  {
        return MessageResponse.of(inventoryAuditService.getAllInCompletedInventoryAudit(startDate, endDate));
    }

    @GetMapping("/inventory-check")
    public MessageResponse<List<InventoryAuditDTO>> getAllInventoryCheck() {
        return MessageResponse.of(inventoryAuditService.getAllInventoryAudit());
    }

    @GetMapping("/inventory-check/{inventoryId}")
    public MessageResponse<InventoryAuditDTO> getByInventoryAuditId(@PathVariable Long inventoryId) {
        return MessageResponse.of(inventoryAuditService.getById(inventoryId));
    }

    @GetMapping("/file/download/{id}")
    public void downloadDocumentFile(@PathVariable Long id, HttpServletResponse response) throws IOException {
        DocumentFile documentFile = documentFileService.getDocumentFileById(id);

        // Устанавливаем заголовки для скачивания файла
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documentFile.getFileName() + "\"");

        // Записываем файл в выходной поток
        response.getOutputStream().write(documentFile.getFileData());
        response.getOutputStream().flush();
    }

    @GetMapping("/inventory-check/result/{auditId}")
    public MessageResponse<List<InventoryAuditResultDTO>> getInventoryAudit(@PathVariable Long auditId) {
        return MessageResponse.of(inventoryAuditResultService.getAllByAuditId(auditId));
    }



}

