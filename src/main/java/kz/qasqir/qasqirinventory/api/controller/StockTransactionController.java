package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.dto.DocumentDTO;
import kz.qasqir.qasqirinventory.api.model.dto.InventoryDTO;
import kz.qasqir.qasqirinventory.api.model.request.InventoryRequest;
import kz.qasqir.qasqirinventory.api.model.request.ReturnRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.InventoryService;
import kz.qasqir.qasqirinventory.api.service.StockTransactionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouse-manager")
public class StockTransactionController {
    private final InventoryService inventoryService;
    private final StockTransactionService stockTransactionService;

    public StockTransactionController(InventoryService inventoryService,
                                      StockTransactionService stockTransactionService
    ) {
        this.inventoryService = inventoryService;
        this.stockTransactionService = stockTransactionService;
    }

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
        return MessageResponse.empty("Inventory deleted successfully");
    }

    // New Endpoints

    @PostMapping("/transactions/incoming")
    public MessageResponse<String> processIncomingGoods(@RequestBody DocumentDTO documentDTO) {
        stockTransactionService.processIncomingGoods(documentDTO);
        return MessageResponse.empty("Incoming goods processed successfully");
    }

    @PostMapping("/transactions/import")
    public MessageResponse<String> processImport(@RequestBody DocumentDTO documentDTO) {
        stockTransactionService.processImport(documentDTO);
        return MessageResponse.empty("Import processed successfully");
    }

    @PostMapping("/transactions/return")
    public MessageResponse<String> processReturn(@RequestBody ReturnRequest returnRequest) {
        stockTransactionService.processReturn(returnRequest);
        return MessageResponse.empty("Return processed successfully");
    }
}
