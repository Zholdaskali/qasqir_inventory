package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.WarehouseZoneException;
import kz.qasqir.qasqirinventory.api.mapper.WarehouseZoneMapper;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseZoneDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Warehouse;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import kz.qasqir.qasqirinventory.api.model.request.WarehouseZoneRequest;
import kz.qasqir.qasqirinventory.api.repository.WarehouseRepository;
import kz.qasqir.qasqirinventory.api.repository.WarehouseZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseZoneService {

    private final WarehouseZoneRepository warehouseZoneRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseZoneMapper warehouseZoneMapper;
    private final CapacityControlService capacityControlService;

    public String deleteWarehouseZone(Long warehouseZoneId) {
        try {
            WarehouseZone warehouseZone = warehouseZoneRepository.findById(warehouseZoneId)
                    .orElseThrow(() -> new WarehouseZoneException("Зона склада с ID " + warehouseZoneId + " не найдена"));

            boolean hasChildren = warehouseZoneRepository.existsByParentId(warehouseZoneId);
            if (hasChildren) {
                throw new WarehouseZoneException("Невозможно удалить зону, так как она содержит дочерние зоны");
            }

            if (warehouseZone.getParent() != null) {
                WarehouseZone parentZone = warehouseZone.getParent();
                BigDecimal zoneVolume = warehouseZone.getCapacity();
                capacityControlService.freeZoneCapacity(parentZone, zoneVolume); // Освобождаем объем в родительской зоне
            }

            warehouseZoneRepository.deleteById(warehouseZoneId);
            return "Зона склада с ID " + warehouseZoneId + " успешно удалена";
        } catch (DataAccessException e) {
            throw new WarehouseZoneException("Ошибка при удалении зоны склада: " + e.getMessage());
        } catch (Exception e) {
            throw new WarehouseZoneException("Неизвестная ошибка при удалении зоны склада: " + e.getMessage());
        }
    }

    public List<WarehouseZoneDTO> getAllWarehouseZoneByWarehouseId(Long warehouseId) {
        return warehouseZoneRepository.findAllByWarehouseId(warehouseId)
                .stream()
                .map(warehouseZoneMapper::toDto)
                .collect(Collectors.toList());
    }

    public String addWarehouseZone(Long warehouseId, WarehouseZoneRequest warehouseZoneRequest, Long userId) {
        try {
            Warehouse warehouse = getWarehouseById(warehouseId);
            WarehouseZone parentZone = Optional.ofNullable(warehouseZoneRequest.getParentId())
                    .map(this::getById)
                    .orElse(null);

            BigDecimal newZoneVolume = BigDecimal.valueOf(
                    warehouseZoneRequest.getHeight() * warehouseZoneRequest.getWidth() * warehouseZoneRequest.getLength()
            );

            if (parentZone != null) {
                if (warehouseZoneRequest.getWidth() > parentZone.getWidth() ||
                        warehouseZoneRequest.getLength() > parentZone.getLength() ||
                        warehouseZoneRequest.getHeight() > parentZone.getHeight()) {
                    throw new RuntimeException("Размеры дочерней зоны превышают размеры родительской зоны");
                }
                capacityControlService.reserveZoneCapacity(parentZone, newZoneVolume); // Резервируем объем в родительской зоне
            }

            WarehouseZone warehouseZone = new WarehouseZone();
            warehouseZone.setName(warehouseZoneRequest.getName());
            warehouseZone.setParent(parentZone);
            warehouseZone.setWarehouse(warehouse);
            warehouseZone.setWidth(warehouseZoneRequest.getWidth());
            warehouseZone.setHeight(warehouseZoneRequest.getHeight());
            warehouseZone.setLength(warehouseZoneRequest.getLength());
            warehouseZone.setCreatedBy(userId);
            warehouseZone.setUpdatedBy(userId);
            warehouseZone.setCreatedAt(Timestamp.from(Instant.now()).toLocalDateTime());
            warehouseZone.setUpdatedAt(Timestamp.from(Instant.now()).toLocalDateTime());
            warehouseZone.setCapacity(newZoneVolume); // Устанавливаем емкость новой зоны

            warehouseZoneRepository.save(warehouseZone);
            updateCanStoreItems(warehouseZone);

            return "Зона на складе успешно инициализирована";
        } catch (Exception e) {
            throw new WarehouseZoneException("Ошибка при создании зоны: " + e.getMessage());
        }
    }

    public void save(WarehouseZone warehouseZone) {
        warehouseZoneRepository.save(warehouseZone);
    }

    private void updateCanStoreItems(WarehouseZone warehouseZone) {
        boolean hasChildren = warehouseZoneRepository.existsByParentId(warehouseZone.getId());
        warehouseZone.setCanStoreItems(!hasChildren);
        warehouseZoneRepository.save(warehouseZone);

        if (warehouseZone.getParent() != null) {
            WarehouseZone parentZone = warehouseZone.getParent();
            parentZone.setCanStoreItems(false);
            warehouseZoneRepository.save(parentZone);
        }
    }

    public WarehouseZoneDTO updateWarehouseZone(Long warehouseId, WarehouseZoneRequest warehouseZoneRequest, Long userId) {
        try {
            WarehouseZone warehouseZone = warehouseZoneRepository.findById(warehouseZoneRequest.getId())
                    .orElseThrow(() -> new WarehouseZoneException("Зона склада не найдена с id: " + warehouseZoneRequest.getId()));

            // Вычисляем старую и новую емкость для обновления родительской зоны
            BigDecimal oldVolume = BigDecimal.valueOf(
                    warehouseZone.getHeight() * warehouseZone.getWidth() * warehouseZone.getLength()
            );
            BigDecimal newVolume = BigDecimal.valueOf(
                    warehouseZoneRequest.getHeight() * warehouseZoneRequest.getWidth() * warehouseZoneRequest.getLength()
            );

            WarehouseZone parentZone = warehouseZone.getParent();
            if (parentZone != null) {
                // Освобождаем старый объем и резервируем новый
                capacityControlService.freeZoneCapacity(parentZone, oldVolume);
                if (warehouseZoneRequest.getWidth() > parentZone.getWidth() ||
                        warehouseZoneRequest.getLength() > parentZone.getLength() ||
                        warehouseZoneRequest.getHeight() > parentZone.getHeight()) {
                    throw new RuntimeException("Размеры дочерней зоны превышают размеры родительской зоны");
                }
                capacityControlService.reserveZoneCapacity(parentZone, newVolume);
            }

            updateBasicInfo(warehouseZone, warehouseZoneRequest, userId);
            updateRelations(warehouseZone, warehouseZoneRequest, warehouseId);

            warehouseZone.setCapacity(newVolume); // Обновляем емкость зоны
            warehouseZoneRepository.save(warehouseZone);

            return warehouseZoneMapper.toDto(warehouseZone);
        } catch (Exception e) {
            throw new WarehouseZoneException("Ошибка при изменении зоны склада: " + e.getMessage());
        }
    }

    private void updateBasicInfo(WarehouseZone zone, WarehouseZoneRequest request, Long userId) {
        zone.setName(request.getName());
        zone.setUpdatedBy(userId);
        zone.setLength(request.getLength());
        zone.setWidth(request.getWidth());
        zone.setHeight(request.getHeight());
        zone.setUpdatedAt(Timestamp.from(Instant.now()).toLocalDateTime());
    }

    private void updateRelations(WarehouseZone zone, WarehouseZoneRequest request, Long warehouseId) {
        Warehouse warehouse = getWarehouseById(warehouseId);
        WarehouseZone parentZone = Optional.ofNullable(request.getParentId())
                .map(this::getById)
                .orElse(null);

        zone.setWarehouse(warehouse);
        zone.setParent(parentZone);
    }

    private Warehouse getWarehouseById(Long warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new WarehouseZoneException("Склад не найден с id: " + warehouseId));
    }

    public WarehouseZone getById(Long warehouseZoneId) {
        return warehouseZoneRepository.findById(warehouseZoneId)
                .orElseThrow(() -> new WarehouseZoneException("Зона не найдена с id: " + warehouseZoneId));
    }
}