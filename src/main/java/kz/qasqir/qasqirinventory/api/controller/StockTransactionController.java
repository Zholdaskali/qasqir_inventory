package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.dto.InventoryDTO;
import kz.qasqir.qasqirinventory.api.model.dto.TicketDTO;
import kz.qasqir.qasqirinventory.api.model.request.InventoryRequest;
import kz.qasqir.qasqirinventory.api.model.request.TicketRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.InventoryService;
import kz.qasqir.qasqirinventory.api.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

// Эндпоинты для транзакций
    @PostMapping("/ticket/write-off")
    public MessageResponse<String> addTicked(@RequestBody TicketRequest ticketRequest) {
        return MessageResponse.of(ticketService.addTicket(ticketRequest, "WRITE-OFF"));
    }

    @PutMapping("/ticket/write-off/{ticketId}")
    public MessageResponse<String> addTicked(@PathVariable Long ticketId) {
        return MessageResponse.of(ticketService.completedTicket(ticketId));
    }

    @GetMapping("ticket/write-off")
    public MessageResponse<List<TicketDTO>> getWriteOffTickets() {
        return MessageResponse.of(ticketService.getAllTicked("WRITE-OFF"));
    }
}
