package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.dto.InventoryDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import kz.qasqir.qasqirinventory.api.model.request.InventoryRequest;
import kz.qasqir.qasqirinventory.api.model.request.NomenclatureRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.InventoryService;
import kz.qasqir.qasqirinventory.api.service.NomenclatureService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouse-manager")
public class StockMovementController {
    private final InventoryService inventoryService;

    public StockMovementController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
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
}
