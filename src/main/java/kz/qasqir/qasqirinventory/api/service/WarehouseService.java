package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.WarehouseException;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseContainerDTO;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseDTO;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseZoneDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Warehouse;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseContainer;
import kz.qasqir.qasqirinventory.api.model.request.WareHouseSaveRequest;
import kz.qasqir.qasqirinventory.api.model.request.WarehouseUpdateRequest;
import kz.qasqir.qasqirinventory.api.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseZoneService warehouseZoneService;
    private final WarehouseContainerService warehouseContainerService;

    public WarehouseService(WarehouseRepository warehouseRepository, WarehouseZoneService warehouseZoneService, WarehouseContainerService warehouseContainerService) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseZoneService = warehouseZoneService;
        this.warehouseContainerService = warehouseContainerService;
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



    private Double getTotalWarehouseVolume(Long warehouseId) {
        List<WarehouseZoneDTO> zones = warehouseZoneService.getAllWarehouseZoneByWarehouseId(warehouseId);

        if (zones == null || zones.isEmpty()) {
            return 0.0;
        }

        // Фильтруем только корневые зоны (у которых нет родительской зоны)
        List<WarehouseZoneDTO> rootZones = zones.stream()
                .filter(zone -> zone.getParentId() == null)
                .toList();

        // Считаем общий объем корневых зон
        return rootZones.stream()
                .mapToDouble(zone -> zone.getWidth() * zone.getHeight() * zone.getLength())
                .sum();
    }


    private Double getOccupiedVolume(Long warehouseId) {
        List<WarehouseZoneDTO> zones = warehouseZoneService.getAllWarehouseZoneByWarehouseId(warehouseId);

        if (zones == null || zones.isEmpty()) {
            return 0.0;
        }

        // Считаем объем всех дочерних зон и контейнеров
        double occupiedByZonesAndContainers = zones.stream()
                .filter(zone -> zone.getParentId() != null) // Берем только дочерние зоны и контейнеры
                .mapToDouble(zone -> zone.getWidth() * zone.getHeight() * zone.getLength())
                .sum();

        // Считаем объем, занятый товарами
        double occupiedByGoods = zones.stream()
                .mapToDouble(zone -> zone.getCapacity() != null ? zone.getCapacity() : 0.0)
                .sum();

        return occupiedByZonesAndContainers + occupiedByGoods;
    }

    public Double getTotalCapacity(Long warehouseId) {
        double totalVolume = getTotalWarehouseVolume(warehouseId);
        double occupiedVolume = getOccupiedVolume(warehouseId);

        System.out.println("Общий объем склада: " + totalVolume + " м³");
        System.out.println("Занятый объем: " + occupiedVolume + " м³");
        System.out.println("Свободный объем: " + occupiedVolume/totalVolume + " м³");

        return (occupiedVolume/totalVolume) * 100;
    }
    public void printWarehouseStatus(Long warehouseId) {
        double totalVolume = getTotalWarehouseVolume(warehouseId);
        double freeVolume = getTotalCapacity(warehouseId);
        double occupiedVolume = getOccupiedVolume(warehouseId);

        System.out.println("Общий объем склада: " + totalVolume + " м³");
        System.out.println("Занятый объем: " + occupiedVolume + " м³");
        System.out.println("Свободный объем: " + freeVolume + " м³");
    }










//    private Double getTotalCapacity(Long warehouseId) {
//
//        List<WarehouseZoneDTO> zones = warehouseZoneService.getAllWarehouseZoneByWarehouseId(warehouseId);
//
//        List<WarehouseZoneDTO> filteredZones = zones.stream()
//                .filter(Objects::nonNull)
//                .filter(zone -> {
//                    if (zone.getParentId() != null) {
//                        return true;
//                    }
//                    return zones.stream()
//                            .noneMatch(childZone -> zone.getId().equals(childZone.getParentId()));
//                })
//                .toList();
//
//        double containersVolume = filteredZones.stream()
//                .mapToDouble(zone -> {
//                    List<WarehouseContainerDTO> containers = warehouseContainerService.getAllByZoneId(zone.getId());
//                    return containers.stream()
//                            .mapToDouble(container -> container.getWidth() * container.getHeight() * container.getLength())
//                            .sum();
//                })
//                .sum();
//
//        double totalVolume = filteredZones.stream()
//                .mapToDouble(zone -> zone.getWidth() * zone.getHeight() * zone.getLength())
//                .sum();
//
//        double freeVolume = filteredZones.stream()
//                .mapToDouble(zone -> zone.getCapacity() != null ? zone.getCapacity() : 0.0)
//                .sum();
//
//
//        if (totalVolume == 0) {
//            return 0.0;
//        }
//        System.out.println((freeVolume + containersVolume) + "/" + (totalVolume));
//
//        return ((freeVolume + containersVolume) / (totalVolume)) * 100;
//    }

//    private Double getTotalCapacity(Long warehouseId) {
//        List<WarehouseZoneDTO> zones = warehouseZoneService.getAllWarehouseZoneByWarehouseId(warehouseId);
//
//        if (zones == null || zones.isEmpty()) {
//            return 0.0;
//        }
//
//        List<WarehouseZoneDTO> filteredZones = zones.stream()
//                .filter(Objects::nonNull)
//                .filter(zone -> {
//                    if (zone.getParentId() != null) {
//                        return true;
//                    }
//                    return zones.stream()
//                            .noneMatch(childZone -> zone.getId().equals(childZone.getParentId()));
//                })
//                .toList();
//
//        double totalVolume = filteredZones.stream()
//                .mapToDouble(zone -> zone.getWidth() * zone.getHeight() * zone.getLength())
//                .sum();
//
//        double freeVolume = filteredZones.stream()
//                .mapToDouble(zone -> zone.getCapacity() != null ? zone.getCapacity() : 0.0)
//                .sum();
//
//        // Рассчитываем объем контейнеров в каждой зоне
//        double containersVolume = filteredZones.stream()
//                .mapToDouble(zone -> {
//                    List<WarehouseContainerDTO> containers = warehouseContainerService.getAllByZoneId(zone.getId());
//                    return containers.stream()
//                            .mapToDouble(container -> container.getWidth() * container.getHeight() * container.getLength())
//                            .sum();
//                })
//                .sum();
//
//        // Вычитаем объем контейнеров из свободного объема
//        freeVolume += containersVolume;
//
//        if (totalVolume == 0) {
//            return 0.0;
//        }
//
//        System.out.println(freeVolume + "/" + totalVolume);
//        return (freeVolume / totalVolume) * 100;
//    }

    public Warehouse getById(Long warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new WarehouseException("Склад не найден"));
    }

}
