package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.WarehouseZoneException;
import kz.qasqir.qasqirinventory.api.mapper.WarehouseZoneMapper;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseZoneDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Warehouse;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import kz.qasqir.qasqirinventory.api.model.request.WarehouseZoneRequest;
import kz.qasqir.qasqirinventory.api.repository.WarehouseRepository;
import kz.qasqir.qasqirinventory.api.repository.WarehouseZoneRepository;
import org.springframework.stereotype.Service;

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
            warehouseZoneRepository.deleteById(warehouseZoneId);
            return "Зона на складе успешно удалена";
        } catch (Exception e) {
            throw new WarehouseZoneException("Ошибка при удалении зоны склада: " + e.getMessage() + e);
        }
    }

    public List<WarehouseZoneDTO> getAllWarehouseZoneByWarehouseId(Long warehouseId) {
        return warehouseZoneRepository.findAllByWarehouseId(warehouseId)
                .stream()
                .map(warehouseZoneMapper::toDto) // Используем MapStruct для преобразования
                .collect(Collectors.toList());
    }

    public String addWarehouseZone(WarehouseZoneRequest warehouseZoneRequest, Long userId) {
        try {
            Warehouse warehouse = getWarehouseById(warehouseZoneRequest.getWarehouseId());
            WarehouseZone parentZone = Optional.ofNullable(warehouseZoneRequest.getParentId())
                    .map(this::getParentZoneById)
                    .orElse(null);

            WarehouseZone warehouseZone = new WarehouseZone();
            warehouseZone.setName(warehouseZoneRequest.getName());
            warehouseZone.setParent(parentZone);
            warehouseZone.setWarehouse(warehouse);
            warehouseZone.setCreatedBy(userId);
            warehouseZone.setUpdatedBy(userId);
            warehouseZone.setCreatedAt(Timestamp.from(Instant.now()).toLocalDateTime());
            warehouseZone.setUpdatedAt(Timestamp.from(Instant.now()).toLocalDateTime());

            warehouseZoneRepository.save(warehouseZone);
            return "Зона на складе успешно инициализирована";
        } catch (Exception e) {
            throw new WarehouseZoneException("Ошибка при создании зоны: " + e.getMessage() + e);
        }
    }

    public WarehouseZoneDTO updateWarehouseZone(WarehouseZoneRequest warehouseZoneRequest, Long userId) {
        try {
            WarehouseZone warehouseZone = warehouseZoneRepository.findById(warehouseZoneRequest.getId())
                    .orElseThrow(() -> new WarehouseZoneException("Зона склада не найдена с id: " + warehouseZoneRequest.getId()));

            updateBasicInfo(warehouseZone, warehouseZoneRequest, userId);
            updateRelations(warehouseZone, warehouseZoneRequest);

            warehouseZoneRepository.save(warehouseZone);
            return warehouseZoneMapper.toDto(warehouseZone); // Используем MapStruct для преобразования
        } catch (Exception e) {
            throw new WarehouseZoneException("Ошибка при изменении зоны склада: " + e.getMessage() + e);
        }
    }

    private void updateBasicInfo(WarehouseZone zone, WarehouseZoneRequest request, Long userId) {
        zone.setName(request.getName());
        zone.setUpdatedBy(userId);
        zone.setUpdatedAt(Timestamp.from(Instant.now()).toLocalDateTime());
    }

    private void updateRelations(WarehouseZone zone, WarehouseZoneRequest request) {
        Warehouse warehouse = getWarehouseById(request.getWarehouseId());
        WarehouseZone parentZone = Optional.ofNullable(request.getParentId())
                .map(this::getParentZoneById)
                .orElse(null);

        zone.setWarehouse(warehouse);
        zone.setParent(parentZone);
    }

    private Warehouse getWarehouseById(Long warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new WarehouseZoneException("Склад не найден с id: " + warehouseId));
    }

    private WarehouseZone getParentZoneById(Long parentId) {
        return warehouseZoneRepository.findById(parentId)
                .orElseThrow(() -> new WarehouseZoneException("Родительская зона не найдена с id: " + parentId));
    }
}
