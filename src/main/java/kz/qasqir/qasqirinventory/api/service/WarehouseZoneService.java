package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.WarehouseZoneException;
import kz.qasqir.qasqirinventory.api.mapper.WarehouseZoneMapper;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseZoneDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Warehouse;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import kz.qasqir.qasqirinventory.api.model.request.WarehouseZoneRequest;
import kz.qasqir.qasqirinventory.api.repository.WarehouseRepository;
import kz.qasqir.qasqirinventory.api.repository.WarehouseZoneRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WarehouseZoneService {
    private final WarehouseZoneRepository warehouseZoneRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseZoneMapper warehouseZoneMapper; // Маппер

    public WarehouseZoneService(WarehouseZoneRepository warehouseZoneRepository, WarehouseRepository warehouseRepository, WarehouseZoneMapper warehouseZoneMapper) {
        this.warehouseZoneRepository = warehouseZoneRepository;
        this.warehouseRepository = warehouseRepository;
        this.warehouseZoneMapper = warehouseZoneMapper;
    }


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
                parentZone.setCapacity(parentZone.getCapacity().add(zoneVolume)); // Восстанавливаем объем
                warehouseZoneRepository.save(parentZone); // Сохраняем обновленную родительскую зону
            }
            warehouseZoneRepository.deleteById(warehouseZoneId);

            return "Зона склада с ID " + warehouseZoneId + " успешно удалена";
        } catch (DataAccessException e) {
            String errorMessage = "Ошибка при удалении зоны склада: " + e.getMessage();
            throw new WarehouseZoneException(errorMessage);
        } catch (Exception e) {
            String errorMessage = "Неизвестная ошибка при удалении зоны склада: " + e.getMessage();
            throw new WarehouseZoneException(errorMessage);
        }
    }


    public List<WarehouseZoneDTO> getAllWarehouseZoneByWarehouseId(Long warehouseId) {
        return warehouseZoneRepository.findAllByWarehouseId(warehouseId)
                .stream()
                .map(warehouseZoneMapper::toDto) // Используем MapStruct для преобразования
                .collect(Collectors.toList());
    }

    public String addWarehouseZone(Long warehouseId, WarehouseZoneRequest warehouseZoneRequest, Long userId) {
        try {
            Warehouse warehouse = getWarehouseById(warehouseId);
            WarehouseZone parentZone = Optional.ofNullable(warehouseZoneRequest.getParentId())
                    .map(this::getById)
                    .orElse(null);
            if (warehouseZoneRequest.getParentId() != null) {
                if (parentZone == null) {
                    throw new RuntimeException("Родительская зона не найдена");
                }

                if (warehouseZoneRequest.getWidth() > parentZone.getWidth() ||
                        warehouseZoneRequest.getLength() > parentZone.getLength() ||
                        warehouseZoneRequest.getHeight() > parentZone.getHeight()) {
                    throw new RuntimeException("Размеры дочерней зоны превышают размеры родительской зоны");
                }

                double newZoneVolume = warehouseZoneRequest.getHeight() * warehouseZoneRequest.getWidth() * warehouseZoneRequest.getLength();

                if (parentZone.getCapacity().compareTo(BigDecimal.valueOf(newZoneVolume)) < 0) {
                    throw new RuntimeException("Зона переполнена: недостаточно места для новой зоны");
                }

                parentZone.setCapacity(parentZone.getCapacity().subtract(BigDecimal.valueOf(newZoneVolume)));
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
            warehouseZone.setCapacity(BigDecimal.valueOf(warehouseZoneRequest.getWidth() * warehouseZoneRequest.getHeight() * warehouseZoneRequest.getLength()));
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

    public WarehouseZoneDTO updateWarehouseZone(Long warehouseId ,WarehouseZoneRequest warehouseZoneRequest, Long userId) {
        try {
            WarehouseZone warehouseZone = warehouseZoneRepository.findById(warehouseZoneRequest.getId())
                    .orElseThrow(() -> new WarehouseZoneException("Зона склада не найдена с id: " + warehouseZoneRequest.getId()));

            updateBasicInfo(warehouseZone, warehouseZoneRequest, userId);
            updateRelations(warehouseZone, warehouseZoneRequest, warehouseId);

            warehouseZoneRepository.save(warehouseZone);
            return warehouseZoneMapper.toDto(warehouseZone); // Используем MapStruct для преобразования
        } catch (Exception e) {
            throw new WarehouseZoneException("Ошибка при изменении зоны склада: " + e.getMessage() + e);
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
                .orElseThrow(() -> new WarehouseZoneException("Родительская зона не найдена с id: " + warehouseZoneId));
    }
}
