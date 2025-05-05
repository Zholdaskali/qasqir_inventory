package kz.qasqir.qasqirinventory.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import kz.qasqir.qasqirinventory.api.model.dto.*;
import kz.qasqir.qasqirinventory.api.model.request.*;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.defaultservice.DocumentService;
import kz.qasqir.qasqirinventory.api.service.defaultservice.WarehouseContainerService;
import kz.qasqir.qasqirinventory.api.service.defaultservice.WarehouseService;
import kz.qasqir.qasqirinventory.api.service.defaultservice.WarehouseZoneService;
import kz.qasqir.qasqirinventory.api.service.mainprocessservice.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/v1/warehouse-manager")
@RequiredArgsConstructor
public class WarehouseStructureController {

    private final WarehouseService warehouseService;
    private final WarehouseZoneService warehouseZoneService;
    private final WarehouseContainerService warehouseContainersService;
    private final TicketService ticketService;
    private final DocumentService documentService;

    @Operation(
            summary = "Удаление склада",
            description = "Возвращает сообщение об удалении"
    )
    // Удаление склада
    @DeleteMapping("/warehouses/{warehouseId}")
    public MessageResponse<String> deleteWarehouse(@PathVariable("warehouseId") Long warehouseId) {
        return MessageResponse.empty(warehouseService.deleteWarehouseById(warehouseId));
    }


    @Operation(
            summary = "Создание нового склада",
            description = "Возвращает сообщение об создании"
    )
    // Создание нового склада
    @PostMapping("/warehouses")
    public MessageResponse<String> createWarehouse(@RequestBody WareHouseSaveRequest request) {
        return MessageResponse.empty(warehouseService.saveWarehouse(request));
    }

    @Operation(
            summary = "Обновление информации о складе",
            description = "Возвращает сообщение об обновлении"
    )
    // Обновление информации о складе
    @PutMapping("/warehouses/{warehouseId}")
    public MessageResponse<WarehouseDTO> updateWarehouse(@RequestBody WarehouseUpdateRequest request, @PathVariable Long warehouseId) {
        return MessageResponse.of(warehouseService.updateWarehouse(request, warehouseId));
    }

    @Operation(
            summary = "Добавление новой зоны склада",
            description = "Возвращает сообщение о добавлении"
    )
    // Добавление новой зоны склада
    @PostMapping("/warehouses/{warehouseId}/zones")
    public MessageResponse<String> addWarehouseZone(@PathVariable Long warehouseId,
                                                    @RequestBody WarehouseZoneRequest warehouseZoneRequest,
                                                    @RequestParam Long userId) {
        String responseMessage = warehouseZoneService.addWarehouseZone(warehouseId, warehouseZoneRequest, userId);
        return MessageResponse.of(responseMessage);
    }


    @Operation(
            summary = "Обновление информации о зоне склада",
            description = "Возвращает сообщение обновлении"
    )
    // Обновление информации о зоне склада
    @PutMapping("/warehouses/{warehouseId}/zones")
    public MessageResponse<WarehouseZoneDTO> updateWarehouseZone(@PathVariable Long warehouseId,
                                                                 @RequestBody WarehouseZoneRequest warehouseZoneRequest,
                                                                 @RequestParam Long userId) {
        WarehouseZoneDTO updatedWarehouseZone = warehouseZoneService.updateWarehouseZone(warehouseId, warehouseZoneRequest, userId);
        return MessageResponse.of(updatedWarehouseZone);
    }


    @Operation(
            summary = "Удаление зоны",
            description = "Возвращает сообщение об удаления"
    )
    // Удаление зоны склада
    @DeleteMapping("/warehouses/{warehouseZoneId}/zones")
    public MessageResponse<String> deleteWarehouseZone(@PathVariable Long warehouseZoneId) {
        String responseMessage = warehouseZoneService.deleteWarehouseZone(warehouseZoneId);
        return MessageResponse.empty(responseMessage);
    }

    @PostMapping("/warehouse/container")
    public MessageResponse<String> saveWarehouseContainer(@RequestBody WarehouseContainersRequest warehouseContainersRequest) {
        return MessageResponse.of(warehouseContainersService.addWarehouseContainer(warehouseContainersRequest));
    }

    @DeleteMapping("/warehouse/container/{warehouseContainerId}")
    public MessageResponse<String> deleteWarehouseContainer(@PathVariable Long warehouseContainerId) {
        return MessageResponse.of(warehouseContainersService.deleteByWarehouseContainerId(warehouseContainerId));
    }

    @PutMapping("/ticket/allowed")
    public MessageResponse<String> allowedTicket(@RequestBody TicketCompleteRequest ticketCompleteRequest) {
        return MessageResponse.of(ticketService.allowedTicket(ticketCompleteRequest.getTicketId(), ticketCompleteRequest.getManaged_id()));
    }

    @PutMapping("/ticket/write-off/allowed/batch")
    public MessageResponse<String> allowedBatchTickets(@RequestBody BatchCompleteRequest batchCompleteRequest) {
        return MessageResponse.of(ticketService.allowedBatchTickets(batchCompleteRequest.getTicketIds(), batchCompleteRequest.getManagedId()));
    }

    @GetMapping("/document/transaction")
    public MessageResponse<List<DocumentWithTransactionsDTO>> getAllDocumentWithTransactions(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return MessageResponse.of(documentService.getDocumentsWithTransactions(startDate, endDate));
    }

    @DeleteMapping("/ticket/{ticketId}")
    public MessageResponse<String> deleteWriteOffTicked(@PathVariable Long ticketId) {
        return MessageResponse.of(ticketService.delete(ticketId));
    }
}
