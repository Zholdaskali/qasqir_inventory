package kz.qasqir.qasqirinventory.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseDTO;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseZoneDTO;
import kz.qasqir.qasqirinventory.api.model.request.*;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.WarehouseService;
import kz.qasqir.qasqirinventory.api.service.WarehouseZoneService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/warehouse-manager")
public class WarehouseStructureController {

    private final WarehouseService warehouseService;
    private final WarehouseZoneService warehouseZoneService;

    public WarehouseStructureController(WarehouseService warehouseService, WarehouseZoneService warehouseZoneService) {
        this.warehouseService = warehouseService;
        this.warehouseZoneService = warehouseZoneService;
    }

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

}
