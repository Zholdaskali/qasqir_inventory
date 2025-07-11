package kz.qasqir.qasqirinventory.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.model.dto.*;
import kz.qasqir.qasqirinventory.api.model.entity.Document;
import kz.qasqir.qasqirinventory.api.model.entity.DocumentFile;
import kz.qasqir.qasqirinventory.api.model.request.*;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.media.S3FileStorageService1;
import kz.qasqir.qasqirinventory.api.service.document.DocumentService;
import kz.qasqir.qasqirinventory.api.service.process.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/storekeeper")
public class StorekeeperController {

    private final DocumentService documentService;
    private final ProcessSalesAndWriteOffAndProductionService processSalesAndTransferService;
    private final S3FileStorageService1 s3FileStorageService1;
    private final InventoryAuditService inventoryAuditService;
    private final ProcessIncomingService processIncomingService;
    private final ProcessInventoryCheckService processInventoryCheck;
    private final ProcessTransferService processTransferService;
    private final ProcessReturnService processReturnService;
    private final InventoryAuditResultService inventoryAuditResultService;
    private final InventoryAuditSystemService inventoryAuditSystemService;

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
    public MessageResponse<InventoryAuditDTO> startInventoryCheck(@RequestParam Long warehouseId, @RequestParam Long createdBy, @RequestParam Long inventoryAuditSystemId) {
        return MessageResponse.of(processInventoryCheck.startInventoryCheck(warehouseId, createdBy, inventoryAuditSystemId));
    }

    @Operation(
            summary = "Создание ИНВЕНТАРИЗАЦИЯ: Начало инвентаризации",
            description = "Возвращает сообщение о создании"
    )
    @PostMapping("/inventory-check-system/start")
    public MessageResponse<InventoryAuditSystemDTO> startInventoryCheck(@RequestBody InventoryAuditSystemRequest inventoryAuditSystemRequest) {
        return MessageResponse.of(inventoryAuditSystemService.startInventoryAuditSystem(inventoryAuditSystemRequest));
    }

    @Operation(
            summary = "Создание ИНВЕНТАРИЗАЦИЯ: Начало инвентаризации",
            description = "Возвращает сообщение о создании"
    )
    @DeleteMapping("/inventory-check-system/{inventoryId}")
    public MessageResponse<String> startInventoryCheck(@PathVariable Long id) {
        return MessageResponse.of(inventoryAuditSystemService.deleteById(id));
    }

    @GetMapping("/inventory-check-system/in-progress")
    public MessageResponse<List<InventoryAuditSystemDTO>> getInventoryCheckSystemInProgress(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return MessageResponse.of(inventoryAuditSystemService.getInventoryResultsInProgress(startDate, endDate));
    }

    @GetMapping("/inventory-check-system/{inventoryId}")
    public MessageResponse<InventoryAuditSystemDTO> getByInventoryAuditSystemId(@PathVariable Long inventoryId) {
        return MessageResponse.of(inventoryAuditSystemService.getInventoryAuditSystemDTO(inventoryId));
    }

    @GetMapping("/inventory-check-system/completed")
    public MessageResponse<List<InventoryAuditSystemDTO>> getInventoryCheckSystemCompleted(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return MessageResponse.of(inventoryAuditSystemService.getInventoryResultsCompleted(startDate, endDate));
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

            return MessageResponse.of(s3FileStorageService1.saveDocumentFile(request));
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

    @GetMapping("/file/download/by-document/{documentId}")
    public void downloadByDocumentId(@PathVariable Long documentId, HttpServletResponse response) throws IOException {
        DocumentFile documentFile = s3FileStorageService1.getDocumentFileByDocumentId(documentId);

        // Путь к файлу на диске
        Path filePath = Paths.get(documentFile.getFilePath());

        if (!Files.exists(filePath)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + URLEncoder.encode(documentFile.getFileName(), StandardCharsets.UTF_8) + "\"");
        response.setContentLengthLong(Files.size(filePath));

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            Files.copy(filePath, outputStream);
            outputStream.flush();
        }
    }



    @GetMapping("/inventory-check/result/{auditId}")
    public MessageResponse<List<InventoryAuditResultDTO>> getInventoryAudit(@PathVariable Long auditId) {
        return MessageResponse.of(inventoryAuditResultService.getAllByAuditId(auditId));
    }

    @GetMapping("/inventory-check/result-date/{nomenclatureCode}")
    public MessageResponse<List<LocalDateTime>> getInventoryAudit(@PathVariable String nomenclatureCode) {
        return MessageResponse.of(inventoryAuditResultService.getLast10AuditDatesByNomenclatureCode(nomenclatureCode));
    }
}

