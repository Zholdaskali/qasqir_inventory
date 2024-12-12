package kz.qasqir.qasqirinventory.api.controller;

import jakarta.validation.Valid;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseDTO;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseZoneDTO;
import kz.qasqir.qasqirinventory.api.model.request.WareHouseSaveRequest;
import kz.qasqir.qasqirinventory.api.model.request.WarehouseUpdateRequest;
import kz.qasqir.qasqirinventory.api.model.request.WarehouseZoneRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.WarehouseService;
import kz.qasqir.qasqirinventory.api.service.WarehouseZoneService;
import lombok.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouse-manager")
public class WarehouseManagerController {
    private final WarehouseService warehouseService;
    private final WarehouseZoneService warehouseZoneService;

    public WarehouseManagerController(WarehouseService warehouseService, WarehouseZoneService warehouseZoneService) {
        this.warehouseService = warehouseService;
        this.warehouseZoneService = warehouseZoneService;
    }

    @GetMapping("/warehouse")
    public MessageResponse<List<WarehouseDTO>> getWarehouse() {
        return MessageResponse.of(warehouseService.getAllWarehouses());
    }

    @DeleteMapping("/warehouse/{warehouseId}")
    public MessageResponse<String> deleteWarehouse(@PathVariable("warehouseId") Long warehouseId) {
        return MessageResponse.empty(warehouseService.deleteOrganizationById(warehouseId));
    }

    @PostMapping("/warehouse")
    public MessageResponse<String> saveWarehouse(@RequestBody WareHouseSaveRequest request) {
        return MessageResponse.empty(warehouseService.saveWarehouse(request));
    }

    @PutMapping("/warehouse")
    public MessageResponse<WarehouseDTO> editWarehouse(@RequestBody WarehouseUpdateRequest request) {
        return MessageResponse.of(warehouseService.updateWarehouse(request));
    }

    @GetMapping("/warehouse-zone/{warehouseId}")
    public MessageResponse<List<WarehouseZoneDTO>> getAllWarehouseZonesByWarehouseId(@PathVariable Long warehouseId) {
        return MessageResponse.of(warehouseZoneService.getAllWarehouseZoneByWarehouseId(warehouseId));
    }

    @PostMapping("/warehouse-zone")
    public MessageResponse<String> addWarehouseZone(@RequestBody WarehouseZoneRequest warehouseZoneRequest,
                                                   @RequestParam Long userId) {
        String responseMessage = warehouseZoneService.addWarehouseZone(warehouseZoneRequest, userId);
        return MessageResponse.of(responseMessage);
    }

    // Обновить информацию о зоне склада
    @PutMapping("/warehouse-zone")
    public MessageResponse<WarehouseZoneDTO> updateWarehouseZone(@RequestBody WarehouseZoneRequest warehouseZoneRequest,
                                                                @RequestParam Long userId) {
        WarehouseZoneDTO updatedWarehouseZone = warehouseZoneService.updateWarehouseZone(warehouseZoneRequest, userId);
        return MessageResponse.of(updatedWarehouseZone);
    }

    // Удалить зону склада по идентификатору
    @DeleteMapping("/warehouse-zone/{warehouseZoneId}")
    public MessageResponse<String> deleteWarehouseZone(@PathVariable Long warehouseZoneId) {
        String responseMessage = warehouseZoneService.deleteWarehouseZone(warehouseZoneId);
        return MessageResponse.empty(responseMessage);
    }
}
