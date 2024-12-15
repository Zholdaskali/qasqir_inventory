package kz.qasqir.qasqirinventory.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import kz.qasqir.qasqirinventory.api.model.dto.CategoryDTO;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseDTO;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseZoneDTO;
import kz.qasqir.qasqirinventory.api.model.request.*;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.CategoryService;
import kz.qasqir.qasqirinventory.api.service.WarehouseService;
import kz.qasqir.qasqirinventory.api.service.WarehouseZoneService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/warehouse-manager")
public class WarehouseManagerController {

    private final WarehouseService warehouseService;
    private final WarehouseZoneService warehouseZoneService;
    private final CategoryService categoryService;

    public WarehouseManagerController(WarehouseService warehouseService, WarehouseZoneService warehouseZoneService, CategoryService categoryService) {
        this.warehouseService = warehouseService;
        this.warehouseZoneService = warehouseZoneService;
        this.categoryService = categoryService;
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
    @PutMapping("/warehouses")
    public MessageResponse<WarehouseDTO> updateWarehouse(@RequestBody WarehouseUpdateRequest request) {
        return MessageResponse.of(warehouseService.updateWarehouse(request));
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

    @Operation(
            summary = "Получение списка всех категорий",
            description = "Возвращает список категорий"
    )
    // Получение списка всех категорий
    @GetMapping("/categories")
    public MessageResponse<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategory();
        return MessageResponse.of(categories);
    }

    @Operation(
            summary = "Создание новой категории",
            description = "Возвращает сообщение о создании"
    )
    // Создание новой категории
    @PostMapping("/categories")
    public MessageResponse<String> createCategory(@RequestBody CategorySaveRequest categorySaveRequest, @RequestParam Long userId) {
        String responseMessage = categoryService.saveCategory(categorySaveRequest, userId);
        return MessageResponse.of(responseMessage);
    }

    @Operation(
            summary = "Удаление категории по ID",
            description = "Возвращает сообщение об удалении"
    )
    // Удаление категории по ID
    @DeleteMapping("/categories/{categoryId}")
    public MessageResponse<String> deleteCategory(@PathVariable Long categoryId) {
        String responseMessage = categoryService.deleteCategory(categoryId);
        return MessageResponse.of(responseMessage);
    }

    @Operation(
            summary = "Обновление категории",
            description = "Возвращает обновленный категорий"
    )
    // Обновление категории
    @PutMapping("/categories")
    public MessageResponse<CategoryDTO> updateCategory(@RequestBody CategoryUpdateRequest categoryUpdateRequest) {
        CategoryDTO updatedCategory = categoryService.updateCategory(categoryUpdateRequest);
        return MessageResponse.of(updatedCategory);
    }
}
