package kz.qasqir.qasqirinventory.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseDTO;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseZoneDTO;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.WarehouseService;
import kz.qasqir.qasqirinventory.api.service.WarehouseZoneService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final WarehouseService warehouseService;
    private final WarehouseZoneService warehouseZoneService;

    public EmployeeController(WarehouseService warehouseService, WarehouseZoneService warehouseZoneService) {
        this.warehouseService = warehouseService;
        this.warehouseZoneService = warehouseZoneService;
    }

    @Operation(
            summary = "Просмотр всех складов",
            description = "Возвращает список складов"
    )
    // Просмотр всех складов
    @GetMapping("/warehouses")
    public MessageResponse<List<WarehouseDTO>> getAllWarehouses() {
        return MessageResponse.of(warehouseService.getAllWarehouses());
    }


    @Operation(
            summary = "Просмотр зон склада по warehouseId",
            description = "Возвращает зоды определенного склада"
    )
    // Просмотр зон склада по warehouseId
    @GetMapping("/warehouses/{warehouseId}/zones")
    public MessageResponse<List<WarehouseZoneDTO>> getZonesByWarehouseId(@PathVariable Long warehouseId) {
        return MessageResponse.of(warehouseZoneService.getAllWarehouseZoneByWarehouseId(warehouseId));
    }
}
