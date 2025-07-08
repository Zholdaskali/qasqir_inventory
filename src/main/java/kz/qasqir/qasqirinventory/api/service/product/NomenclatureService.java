package kz.qasqir.qasqirinventory.api.service.product;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.CategoryException;
import kz.qasqir.qasqirinventory.api.exception.NomenclatureException;
import kz.qasqir.qasqirinventory.api.mapper.NomenclatureMapper;
import kz.qasqir.qasqirinventory.api.model.dto.NomenclatureDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Category;
import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import kz.qasqir.qasqirinventory.api.model.request.NomenclatureRequest;
import kz.qasqir.qasqirinventory.api.repository.CategoryRepository;
import kz.qasqir.qasqirinventory.api.repository.NomenclatureRepository;
import kz.qasqir.qasqirinventory.api.service.process.CapacityControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NomenclatureService {
    private final NomenclatureRepository nomenclatureRepository;
    private final CategoryRepository categoryRepository;
    private final NomenclatureMapper nomenclatureMapper;
    private final CapacityControlService capacityControlService;

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
            nomenclature.setMeasurementUnit(nomenclatureRequest.getMeasurement());
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

    @Transactional
    public String updateNomenclature(Long id, NomenclatureRequest nomenclatureRequest) {
        Nomenclature nomenclature = nomenclatureRepository.findById(id)
                .orElseThrow(() -> new NomenclatureException("Номенклатура не найдена"));

        // Сохраняем старые размеры
        Double oldVolume = nomenclature.getVolume();
        Double oldHeight = nomenclature.getHeight();
        Double oldWidth = nomenclature.getWidth();
        Double oldLength = nomenclature.getLength();

        // Новые размеры
        Double newVolume = nomenclatureRequest.getVolume();
        Double newHeight = nomenclatureRequest.getHeight();
        Double newWidth = nomenclatureRequest.getWidth();
        Double newLength = nomenclatureRequest.getLength();

        // Если размеры изменились — делаем валидацию
        boolean sizeChanged = !Objects.equals(oldVolume, newVolume) ||
                !Objects.equals(oldHeight, newHeight) ||
                !Objects.equals(oldWidth, newWidth) ||
                !Objects.equals(oldLength, newLength);

        if (sizeChanged) {
            capacityControlService.validateCapacityForSizeChange(
                    nomenclature, newVolume, newHeight, newWidth, newLength
            );
        }

        // Обновляем поля
        nomenclature.setName(nomenclatureRequest.getName());
        nomenclature.setArticle(nomenclatureRequest.getArticle());
        nomenclature.setCode(nomenclatureRequest.getCode());
        nomenclature.setType(nomenclatureRequest.getType());
        nomenclature.setMeasurementUnit(nomenclatureRequest.getMeasurement());
        nomenclature.setTnvedCode(nomenclatureRequest.getTnved_code());
        nomenclature.setUpdatedBy(nomenclatureRequest.getUpdated_by());
        nomenclature.setUpdatedAt(LocalDateTime.now());
        nomenclature.setCategory(
                categoryRepository.findById(nomenclatureRequest.getCategoryId())
                        .orElseThrow(() -> new CategoryException("Категория не найдена"))
        );
        nomenclature.setHeight(newHeight);
        nomenclature.setLength(newLength);
        nomenclature.setWidth(newWidth);
        nomenclature.setVolume(newVolume);

        nomenclatureRepository.save(nomenclature);

        if (sizeChanged) {
            capacityControlService.updateCapacitiesAfterSizeChange(
                    nomenclature, newVolume, newHeight, newWidth, newLength
            );
        }

        return "Номенклатура успешно обновлена";
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


    public List<Nomenclature> findNomenclaturesSyncedSince(LocalDate startDate, LocalDate endDate) {
        return nomenclatureRepository.findBySyncDateBetween(startDate.atStartOfDay(), endDate.atStartOfDay().plusDays(1).minusSeconds(1));
    }

    public List<Nomenclature> findNomenclaturesNotSyncedSince(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atStartOfDay().plusDays(1).minusSeconds(1);
        return nomenclatureRepository.findBySyncDateIsNullOrSyncDateNotBetween(start, end);
    }
}
