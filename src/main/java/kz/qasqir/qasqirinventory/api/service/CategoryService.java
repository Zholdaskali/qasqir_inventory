package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.CategoryException;
import kz.qasqir.qasqirinventory.api.mapper.CategoryMapper;
import kz.qasqir.qasqirinventory.api.model.dto.CategoryDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Category;
import kz.qasqir.qasqirinventory.api.model.request.CategorySaveRequest;
import kz.qasqir.qasqirinventory.api.model.request.CategoryUpdateRequest;
import kz.qasqir.qasqirinventory.api.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import javax.xml.catalog.CatalogException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public List<CategoryDTO> getAllCategory() {
        try {
            return categoryRepository.findAll().stream().map(categoryMapper::toDto).toList();
        } catch (RuntimeException e) {
            throw new CategoryException("Ошибка при выводе списка всех категорий");
        }
    }

    public String saveCategory(CategorySaveRequest categorySaveRequest, Long userId) {
        try{
            Category category = new Category();
            category.setName(categorySaveRequest.getName());
            category.setCreatedBy(userId);
            category.setUpdatedBy(userId);
            LocalDateTime now = Timestamp.from(Instant.now()).toLocalDateTime();
            category.setCreatedAt(now);
            category.setUpdatedAt(now);
            categoryRepository.save(category);
            return "Категория успешно создано";
        }catch (RuntimeException e){
            throw new CategoryException("Ошибка при создании категории");
        }
    }

    public String deleteCategory(Long categoryId) {
        try {
            categoryRepository.deleteById(categoryId);
            return "Категория успешно удалена";
        } catch (RuntimeException e) {
            throw new CategoryException("Ошибка при удалении");
        }
    }

    public String updateCategory(CategoryUpdateRequest categoryUpdateRequest, Long categoryId) {
        try {
            Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new CategoryException("Категория не найдено"));
            category.setName(categoryUpdateRequest.getName());
            category.setUpdatedBy(categoryUpdateRequest.getUpdateBy());
            category.setUpdatedAt(Timestamp.from(Instant.now()).toLocalDateTime());
            categoryRepository.save(category);
            return "Успешное изменено";
        } catch (RuntimeException e) {
            throw new CatalogException("Ошибка при изменении данных категории");
        }
    }
}
