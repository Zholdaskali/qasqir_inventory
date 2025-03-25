package kz.qasqir.qasqirinventory.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import kz.qasqir.qasqirinventory.api.model.dto.InventoryDTO;
import kz.qasqir.qasqirinventory.api.model.dto.TicketDTO;
import kz.qasqir.qasqirinventory.api.model.request.BatchCompleteRequest;
import kz.qasqir.qasqirinventory.api.model.request.BatchProcessRequest;
import kz.qasqir.qasqirinventory.api.model.request.InventoryRequest;
import kz.qasqir.qasqirinventory.api.model.request.TicketRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.InventoryService;
import kz.qasqir.qasqirinventory.api.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouse-manager")
@RequiredArgsConstructor
public class StockTransactionController {

    private final TicketService ticketService;
    private final InventoryService inventoryService;

    // Инвентаризация
    @PostMapping("/inventory")
    public MessageResponse<InventoryDTO> addInventory(@RequestBody InventoryRequest request, @RequestParam Long warehouseZoneId) {
        InventoryDTO inventoryDTO = inventoryService.addInventory(request, warehouseZoneId);
        return MessageResponse.of(inventoryDTO);
    }

    @PutMapping("/inventory/{inventoryId}")
    public MessageResponse<InventoryDTO> updateInventory(@PathVariable Long inventoryId, @RequestBody InventoryRequest request) {
        InventoryDTO inventoryDTO = inventoryService.updateInventory(request, inventoryId);
        return MessageResponse.of(inventoryDTO);
    }

    @GetMapping("/inventory/{inventoryId}")
    public MessageResponse<InventoryDTO> getInventoryById(@PathVariable Long inventoryId) {
        InventoryDTO inventoryDTO = inventoryService.getInventoryById(inventoryId);
        return MessageResponse.of(inventoryDTO);
    }

    @DeleteMapping("/inventory/{inventoryId}")
    public MessageResponse<String> deleteInventory(@PathVariable Long inventoryId) {
        inventoryService.deleteInventory(inventoryId);
        return MessageResponse.empty("Инвентарь успешно удален");
    }


// Энд-поинты для списания
    @Operation(
            summary = "Создание групповой заявки ",
            description = "Возвращает сообщение о создании"
    )
    @PostMapping("/ticket/batch")
    public MessageResponse<String> addBatchWriteOffTickets(@RequestBody BatchProcessRequest batchProcessRequest) {
        return MessageResponse.of(ticketService.addBatchWriteOffTickets(batchProcessRequest));
    }

    @PutMapping("/ticket/completed/batch")
    public MessageResponse<String> completedBatchTickets(@RequestBody BatchCompleteRequest batchCompleteRequest) {
        return MessageResponse.of(ticketService.completedBatchTickets(batchCompleteRequest.getTicketIds()));
    }

    @PutMapping("/ticket/{ticketId}")
    public MessageResponse<String> completedWriteOffTicked(@PathVariable Long ticketId) {
        return MessageResponse.of(ticketService.completedTicket(ticketId));
    }

    @DeleteMapping("/ticket/{ticketId}")
    public MessageResponse<String> deleteWriteOffTicked(@PathVariable Long ticketId) {
        return MessageResponse.of(ticketService.delete(ticketId));
    }

    @GetMapping("ticket/{type}")
    public MessageResponse<List<TicketDTO>> getWriteOffTickets(@PathVariable String type,
                                                               @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                               @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return MessageResponse.of(ticketService.getAllTicked(type, startDate, endDate));
    }
}
