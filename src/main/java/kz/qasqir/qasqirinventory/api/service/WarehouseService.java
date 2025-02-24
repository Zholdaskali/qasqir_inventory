package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.WarehouseException;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseDTO;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseZoneDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Warehouse;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import kz.qasqir.qasqirinventory.api.model.request.WareHouseSaveRequest;
import kz.qasqir.qasqirinventory.api.model.request.WarehouseUpdateRequest;
import kz.qasqir.qasqirinventory.api.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseZoneService warehouseZoneService;

    public WarehouseService(WarehouseRepository warehouseRepository, WarehouseZoneService warehouseZoneService) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseZoneService = warehouseZoneService;
    }

    public String saveWarehouse(WareHouseSaveRequest request) {
        try {
            Warehouse warehouse = new Warehouse();
            warehouse.setName(request.getName());
            warehouse.setLocation(request.getLocation());
            LocalDateTime now = Timestamp.from(Instant.now()).toLocalDateTime();
            warehouse.setCreatedAt(now);
            warehouse.setUpdatedAt(now);
            warehouseRepository.save(warehouse);
            return "Склад успешно инициализирован";
        } catch (WarehouseException e) {
            throw new WarehouseException("Ошибка при инициализировании склада" + e);
        }

    }

    public List<WarehouseDTO> getAllWarehouses() {
        return warehouseRepository.findAll().stream().map(this::convertToWarehouseDTO).toList();
    }

    public WarehouseDTO updateWarehouse(WarehouseUpdateRequest request, Long warehouseId) {
        try {
            Warehouse warehouse = getById(warehouseId);

            warehouse.setName(request.getName());
            warehouse.setLocation(request.getLocation());
            warehouse.setUpdatedAt(Timestamp.from(Instant.now()).toLocalDateTime());
            warehouseRepository.save(warehouse);
            return convertToWarehouseDTO(warehouse);
        } catch (WarehouseException e) {
            throw new WarehouseException("Ошибка при инициализации склада");
        }

    }

    public String deleteWarehouseById(Long organizationById) {
        try {
            warehouseRepository.deleteById(organizationById);
            return "Успешно удалено!";
        } catch (WarehouseException e) {
            throw new WarehouseException("Ошибка при удалении Склада");
        }

    }

    private WarehouseDTO convertToWarehouseDTO(Warehouse warehouse) {
        return new WarehouseDTO(warehouse.getId(), warehouse.getName(), warehouse.getLocation(), warehouse.getCreatedAt(), warehouse.getUpdatedAt(), getZonesCount(warehouse.getId()), getTotalCapacity(warehouse.getId()));
    }

    private int getZonesCount(Long WarehouseId) {
        return warehouseZoneService.getAllWarehouseZoneByWarehouseId(WarehouseId).size();
    }

    private Double getTotalCapacity(Long warehouseId) {
        List<WarehouseZoneDTO> zones = warehouseZoneService.getAllWarehouseZoneByWarehouseId(warehouseId);

        if (zones == null || zones.isEmpty()) {
            return 0.0;
        }

        Set<Long> zoneIds = zones.stream()
                .filter(Objects::nonNull)
                .map(WarehouseZoneDTO::getId)
                .collect(Collectors.toSet());

        List<WarehouseZoneDTO> filteredZones = zones.stream()
                .filter(Objects::nonNull)
                .filter(zone -> {
                    if (zone.getParentId() != null) {
                        return true;
                    }
                    return zones.stream()
                            .noneMatch(childZone -> zone.getId().equals(childZone.getParentId()));
                })
                .toList();

        double totalVolume = filteredZones.stream()
                .mapToDouble(zone -> zone.getWidth() * zone.getHeight() * zone.getLength())
                .sum();

        double freeVolume = filteredZones.stream()
                .mapToDouble(zone -> zone.getCapacity() != null ? zone.getCapacity() : 0.0)
                .sum();

        if (totalVolume == 0) {
            return 0.0;
        }

        System.out.println(freeVolume + "/" + totalVolume);
        return (freeVolume / totalVolume) * 100;
    }

    public Warehouse getById(Long warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new WarehouseException("Склад не найден"));
    }

}
