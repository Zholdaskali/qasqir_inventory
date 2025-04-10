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
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryException("Категория с id " + categoryId + " не найдена"));

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
            nomenclature.setHeight(nomenclatureRequest.getHeight());
            nomenclature.setLength(nomenclatureRequest.getLength());
            nomenclature.setWidth(nomenclatureRequest.getWidth());
            nomenclature.setVolume(nomenclatureRequest.getVolume());

            nomenclatureRepository.save(nomenclature);

            return "Номенклатура успешно создана";
        } catch (CategoryException e) {
            return "Ошибка: " + e.getMessage();
        } catch (Exception e) {
            return "Произошла непредвиденная ошибка: " + e.getMessage();
        }
    }

    public String deleteNomenclature(Long nomenclatureId) {
        try {
            Nomenclature nomenclature = nomenclatureRepository.findById(nomenclatureId)
                    .orElseThrow(() -> new NomenclatureException("Номенклатура не найдена"));

            nomenclatureRepository.delete(nomenclature);

            return "Номенклатура успешно удалена";
        } catch (NomenclatureException e) {
            return "Ошибка: " + e.getMessage();
        } catch (Exception e) {
            return "Произошла непредвиденная ошибка: " + e.getMessage();
        }
    }

    public String updateNomenclature(Long userId, NomenclatureRequest nomenclatureRequest) {
        try {
            Nomenclature nomenclature = nomenclatureRepository.findById(id)
                    .orElseThrow(() -> new NomenclatureException("Номенклатура не найдена"));

            nomenclature.setName(nomenclatureRequest.getName());
            nomenclature.setArticle(nomenclatureRequest.getArticle());
            nomenclature.setCode(nomenclatureRequest.getCode());
            nomenclature.setType(nomenclatureRequest.getType());
            nomenclature.setMeasurementUnit(nomenclatureRequest.getMeasurement_unit());
            nomenclature.setTnvedCode(nomenclatureRequest.getTnved_code());
            nomenclature.setUpdatedBy(nomenclatureRequest.getUpdated_by());
            nomenclature.setUpdatedAt(LocalDateTime.now());
            nomenclature.setCategory(categoryRepository.findById(nomenclatureRequest.getCategoryId()).orElseThrow(() -> new CategoryException(" При изменении категории в номенклатуре. Категория не найдена")));
            nomenclature.setHeight(nomenclatureRequest.getHeight());
            nomenclature.setLength(nomenclatureRequest.getLength());
            nomenclature.setWidth(nomenclatureRequest.getWidth());
            nomenclature.setVolume(nomenclatureRequest.getVolume());
            nomenclatureRepository.save(nomenclature);

            return "Номенклатура успешно обновлена";
        } catch (NomenclatureException e) {
            return "Ошибка: " + e.getMessage();
        } catch (Exception e) {
            return "Произошла непредвиденная ошибка: " + e.getMessage();
        }
    }

    public List<NomenclatureDTO> getAllNomenclatureByCategoryId(Long categoryId) {
        return nomenclatureRepository.findByCategoryId(categoryId).stream()
                .map(nomenclatureMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<NomenclatureDTO> getAllNomenclature() {
        return nomenclatureRepository.findAll().stream().map(nomenclatureMapper :: toDto).collect(Collectors.toList());
    }


    public Nomenclature getById(Long nomenclatureId) {
        return nomenclatureRepository.findById(nomenclatureId).orElseThrow(() -> new NomenclatureException("Номернклатура не найдена"));
    }


}
