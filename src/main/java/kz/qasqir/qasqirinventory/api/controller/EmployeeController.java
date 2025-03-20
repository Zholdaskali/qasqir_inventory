package kz.qasqir.qasqirinventory.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import kz.qasqir.qasqirinventory.api.model.dto.*;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.CategoryService;
import kz.qasqir.qasqirinventory.api.service.NomenclatureService;
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
    private final NomenclatureService nomenclatureService;
    private final CategoryService categoryService;

    public EmployeeController(WarehouseService warehouseService, WarehouseZoneService warehouseZoneService, NomenclatureService nomenclatureService, CategoryService categoryService) {
        this.warehouseService = warehouseService;
        this.warehouseZoneService = warehouseZoneService;
        this.nomenclatureService = nomenclatureService;
        this.categoryService = categoryService;
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
}
