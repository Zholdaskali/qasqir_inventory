package kz.qasqir.qasqirinventory.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import kz.qasqir.qasqirinventory.api.model.dto.CategoryDTO;
import kz.qasqir.qasqirinventory.api.model.dto.NomenclatureDTO;
import kz.qasqir.qasqirinventory.api.model.request.CategorySaveRequest;
import kz.qasqir.qasqirinventory.api.model.request.CategoryUpdateRequest;
import kz.qasqir.qasqirinventory.api.model.request.NomenclatureRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.CategoryService;
import kz.qasqir.qasqirinventory.api.service.NomenclatureService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouse-manager")
public class StockController {

    private final CategoryService categoryService;
    private final NomenclatureService nomenclatureService;

    public StockController(CategoryService categoryService, NomenclatureService nomenclatureService) {
        this.categoryService = categoryService;
        this.nomenclatureService = nomenclatureService;
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

    @Operation(
            summary = "Создание номенклатуры",
            description = "Возвращает создание номенклатуры"
    )
    @PostMapping("/{categoryId}/nomenclatures")
    public MessageResponse<String> createNomenclature(@RequestBody NomenclatureRequest nomenclatureRequest, @PathVariable Long categoryId) {
        return MessageResponse.of(nomenclatureService.saveNomenclature(nomenclatureRequest, categoryId));
    }

    @Operation(
            summary = "Списка номенклатуры по категориям",
            description = "Возвращает список номенклатуры"
    )
    @GetMapping("/{categoryId}/nomenclatures")
    public MessageResponse<List<NomenclatureDTO>> getAllByCategoryId(@PathVariable Long categoryId) {
        return  MessageResponse.of(nomenclatureService.getAllNomenclature(categoryId));
    }

    @Operation(
            summary = "Обновление номенклатуры по айди",
            description = "Возвращает список номенклатуры"
    )
    @PutMapping("/{nomenclatureId}/nomenclatures")
    public MessageResponse<String> updateNomenclature(@PathVariable Long nomenclatureId, @RequestBody NomenclatureRequest nomenclatureRequest) {
        return MessageResponse.of(nomenclatureService.updateNomenclature(nomenclatureId, nomenclatureRequest));
    }

    @Operation(
            summary = "Удаление номенклатуры",
            description = "Возвращает сообщение об удалении номенклатуры"
    )
    @DeleteMapping("/{nomenclatureId}/nomenclatures")
    public MessageResponse<String> deleteNomenclature(@PathVariable Long nomenclatureId) {
        return MessageResponse.of(nomenclatureService.deleteNomenclature(nomenclatureId));
    }
}