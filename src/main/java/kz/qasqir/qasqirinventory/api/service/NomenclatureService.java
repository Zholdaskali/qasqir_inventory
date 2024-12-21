package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.CategoryException;
import kz.qasqir.qasqirinventory.api.exception.NomenclatureException;
import kz.qasqir.qasqirinventory.api.mapper.NomenclatureMapper;
import kz.qasqir.qasqirinventory.api.model.dto.NomenclatureDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Category;
import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import kz.qasqir.qasqirinventory.api.model.request.NomenclatureRequest;
import kz.qasqir.qasqirinventory.api.repository.CategoryRepository;
import kz.qasqir.qasqirinventory.api.repository.NomenclatureRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NomenclatureService {
    private final NomenclatureRepository nomenclatureRepository;
    private final CategoryRepository categoryRepository;
    private final NomenclatureMapper nomenclatureMapper;

    public NomenclatureService(NomenclatureRepository nomenclatureRepository, CategoryRepository categoryRepository, NomenclatureMapper nomenclatureMapper) {
        this.nomenclatureRepository = nomenclatureRepository;
        this.categoryRepository = categoryRepository;
        this.nomenclatureMapper = nomenclatureMapper;
    }

    public String saveNomenclature(NomenclatureRequest nomenclatureRequest, Long categoryId) {
        try {
            // Проверяем существование категории
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryException("Категория с id " + categoryId + " не найдена"));

            // Создаем номенклатуру
            Nomenclature nomenclature = new Nomenclature();
            nomenclature.setName(nomenclatureRequest.getName());
            nomenclature.setArticle(nomenclatureRequest.getArticle());
            nomenclature.setCode(nomenclatureRequest.getCode());
            nomenclature.setType(nomenclatureRequest.getType());
            nomenclature.setCategory(category);
            nomenclature.setMeasurementUnit(nomenclatureRequest.getMeasurement_unit());
            nomenclature.setTnvedCode(nomenclatureRequest.getTnved_code());
            nomenclature.setCreatedBy(nomenclatureRequest.getUpdated_by());
            nomenclature.setUpdatedBy(nomenclatureRequest.getUpdated_by());
            nomenclature.setCreatedAt(Timestamp.from(Instant.now()).toLocalDateTime());
            nomenclature.setUpdatedAt(Timestamp.from(Instant.now()).toLocalDateTime());

            // Сохраняем номенклатуру
            nomenclatureRepository.save(nomenclature);

            return "Номенклатура успешно создана";
        } catch (CategoryException e) {
            // Логирование или обработка ошибки категории
            return "Ошибка: " + e.getMessage();
        } catch (Exception e) {
            // Логирование или обработка других ошибок
            return "Произошла непредвиденная ошибка: " + e.getMessage();
        }
    }

    public String deleteNomenclature(Long nomenclatureId) {
        try {
            // Проверяем существование номенклатуры
            Nomenclature nomenclature = nomenclatureRepository.findById(nomenclatureId)
                    .orElseThrow(() -> new NomenclatureException("Номенклатура не найдена"));

            // Удаляем номенклатуру
            nomenclatureRepository.delete(nomenclature);

            return "Номенклатура успешно удалена";
        } catch (NomenclatureException e) {
            return "Ошибка: " + e.getMessage();
        } catch (Exception e) {
            return "Произошла непредвиденная ошибка: " + e.getMessage();
        }
    }

    public String updateNomenclature(Long id, NomenclatureRequest nomenclatureRequest) {
        try {
            // Проверяем, существует ли номенклатура
            Nomenclature nomenclature = nomenclatureRepository.findById(id)
                    .orElseThrow(() -> new NomenclatureException("Номенклатура не найдена"));

            // Обновляем данные номенклатуры
            nomenclature.setName(nomenclatureRequest.getName());
            nomenclature.setArticle(nomenclatureRequest.getArticle());
            nomenclature.setCode(nomenclatureRequest.getCode());
            nomenclature.setType(nomenclatureRequest.getType());
            nomenclature.setMeasurementUnit(nomenclatureRequest.getMeasurement_unit());
            nomenclature.setTnvedCode(nomenclatureRequest.getTnved_code());
            nomenclature.setUpdatedBy(nomenclatureRequest.getUpdated_by());
            nomenclature.setUpdatedAt(LocalDateTime.now());

            // Сохраняем обновленную номенклатуру
            nomenclatureRepository.save(nomenclature);

            return "Номенклатура успешно обновлена";
        } catch (NomenclatureException e) {
            return "Ошибка: " + e.getMessage();
        } catch (Exception e) {
            return "Произошла непредвиденная ошибка: " + e.getMessage();
        }
    }

    public List<NomenclatureDTO> getAllNomenclature(Long categoryId) {
        return nomenclatureRepository.findAllByCategoryId(categoryId).stream()
                .map(nomenclatureMapper::toDto) // Преобразуем каждую сущность Nomenclature в NomenclatureDTO
                .collect(Collectors.toList()); // Собираем в список
    }


    public Nomenclature getById(Long nomenclatureId) {
        return nomenclatureRepository.findById(nomenclatureId).orElseThrow(() -> new NomenclatureException("Номернклатура не найдена"));
    }



}
