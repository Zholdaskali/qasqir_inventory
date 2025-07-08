package kz.qasqir.qasqirinventory.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import kz.qasqir.qasqirinventory.api.model.dto.InventoryDTO;
import kz.qasqir.qasqirinventory.api.model.request.BatchCompleteRequest;
import kz.qasqir.qasqirinventory.api.model.request.BatchProcessRequest;
import kz.qasqir.qasqirinventory.api.model.request.InventoryRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.inventory.InventoryService;
import kz.qasqir.qasqirinventory.api.service.process.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/storekeeper")
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
}
