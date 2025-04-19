package kz.qasqir.qasqirinventory.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import kz.qasqir.qasqirinventory.api.model.dto.*;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.*;
import kz.qasqir.qasqirinventory.api.service.mainprocessservice.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final WarehouseService warehouseService;
    private final WarehouseZoneService warehouseZoneService;
    private final NomenclatureService nomenclatureService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;
    private final CustomerService customerService;
    private final WarehouseContainerService warehouseContainerService;
    private final TicketService ticketService;

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
    @GetMapping("/warehouses/{warehouseId}")
    public MessageResponse<WarehouseStructureDTO> getDetailByWarehouseId(@PathVariable Long warehouseId) {
        return MessageResponse.of(warehouseService.getWarehouseDetails(warehouseId));
    }

    @GetMapping("/warehouses/{warehouseId}/zones")
    public MessageResponse<List<WarehouseZoneDTO>> getZonesByWarehouseId(@PathVariable Long warehouseId) {
        return MessageResponse.of(warehouseZoneService.getAllWarehouseZoneByWarehouseId(warehouseId));
    }

    @Operation(
            summary = "Просмотр списка номенклатуры по categoryId",
            description = "Возвращает номенклатуры определенного категория"
    )
    @GetMapping("/categories/{categoryId}/nomenclatures")
    public MessageResponse<List<NomenclatureDTO>> getNomenclatures(@PathVariable Long categoryId) {
        return MessageResponse.of(nomenclatureService.getAllNomenclatureByCategoryId(categoryId));
    }

    @Operation(
            summary = "Просмотр списка категории",
            description = "Возвращает общий список категорий"
    )
    @GetMapping("/categories")
    public MessageResponse<List<CategoryDTO>> getCategories() {
        return MessageResponse.of(categoryService.getAllCategory());
    }

    @GetMapping("/suppliers")
    public MessageResponse<List<SupplierDTO>> getAllSupplier() {
        return MessageResponse.of(supplierService.getAllSuppliers());
    }

    @GetMapping("/customers")
    public MessageResponse<List<CustomerDTO>> getAllCustomer() {
        return MessageResponse.of(customerService.getAllCustomer());
    }

    @Operation(
            summary = "Списка номенклатуры по категориям",
            description = "Возвращает список номенклатуры"
    )
    @GetMapping("/{categoryId}/nomenclatures")
    public MessageResponse<List<NomenclatureDTO>> getAllByCategoryId(@PathVariable Long categoryId) {
        return  MessageResponse.of(nomenclatureService.getAllNomenclatureByCategoryId(categoryId));
    }

    @GetMapping("/nomenclatures")
    public MessageResponse<List<NomenclatureDTO>> getAllNomenclatures() {
        return MessageResponse.of(nomenclatureService.getAllNomenclature());
    }

    @GetMapping("warehouse/container/{zoneId}")
    public MessageResponse<List<WarehouseContainerDTO>> getAllByZoneId(@PathVariable Long zoneId) {
        return MessageResponse.of(warehouseContainerService.getAllByZoneId(zoneId));
    }

    @GetMapping("ticket/{type}")
    public MessageResponse<List<TicketDTO>> getWriteOffTickets(@PathVariable String type,
                                                               @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                               @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return MessageResponse.of(ticketService.getAllTicked(type, startDate, endDate));
    }
}
