package kz.qasqir.qasqirinventory.api.service.warehouse;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.WarehouseException;
import kz.qasqir.qasqirinventory.api.model.dto.*;
import kz.qasqir.qasqirinventory.api.model.entity.Warehouse;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseContainer;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import kz.qasqir.qasqirinventory.api.model.request.WareHouseSaveRequest;
import kz.qasqir.qasqirinventory.api.model.request.WarehouseUpdateRequest;
import kz.qasqir.qasqirinventory.api.repository.WarehouseContainerRepository;
import kz.qasqir.qasqirinventory.api.repository.WarehouseRepository;
import kz.qasqir.qasqirinventory.api.repository.WarehouseZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseZoneService warehouseZoneService;
    private final WarehouseZoneRepository warehouseZoneRepository;
    private final WarehouseContainerRepository warehouseContainerRepository;
    private final WarehouseContainerService warehouseContainerService;


    @Transactional
    public String saveWarehouse(WareHouseSaveRequest request) {
        try {
            Warehouse warehouse = new Warehouse();
            warehouse.setName(request.getName());
            warehouse.setLocation(request.getLocation());
            warehouse.setLatitude(request.getLatitude());
            warehouse.setLongitude(request.getLongitude());
            warehouseRepository.save(warehouse);
            return "Склад успешно инициализирован";
        } catch (Exception e) {
            throw new WarehouseException("Ошибка при инициализировании склада: " + e.getMessage());
        }
    }

    public List<WarehouseDTO> getAllWarehouses() {
        return warehouseRepository.findAll().stream().map(this::convertToWarehouseDTO).toList();
    }

    @Transactional
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
    public WarehouseDTO convertToWarehouseDTO(Warehouse warehouse) {
        return new WarehouseDTO(warehouse.getId(), warehouse.getName(), warehouse.getLocation(), warehouse.getCreatedAt(), warehouse.getUpdatedAt(), getZonesCount(warehouse.getId()), getTotalCapacity(warehouse.getId()), warehouse.getLatitude(), warehouse.getLongitude());
    }

    // Пиздец
    private int getZonesCount(Long WarehouseId) {
        return warehouseZoneService.getAllWarehouseZoneByWarehouseId(WarehouseId).size();
    }

    public WarehouseStructureDTO getWarehouseDetails(Long warehouseId) {
        // Находим склад по ID
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + warehouseId));

        // Преобразуем сущность Warehouse в DTO
        WarehouseStructureDTO warehouseStructureDTO = new WarehouseStructureDTO();
        warehouseStructureDTO.setId(warehouse.getId());
        warehouseStructureDTO.setName(warehouse.getName());
        warehouseStructureDTO.setLocation(warehouse.getLocation());
        warehouseStructureDTO.setCreatedAt(warehouse.getCreatedAt());
        warehouseStructureDTO.setUpdatedAt(warehouse.getUpdatedAt());

        // Находим корневые зоны склада WarehouseStructureDTO
        List<WarehouseZone> rootZones = warehouseZoneRepository.findRootZonesByWarehouseId(warehouseId);
        List<WarehouseZoneStructureDTO> zoneDTOs = rootZones.stream()
                .map(this::mapZoneToDTO)
                .collect(Collectors.toList());

        warehouseStructureDTO.setZones(zoneDTOs);
        return warehouseStructureDTO;
    }


    private WarehouseZoneStructureDTO mapZoneToDTO(WarehouseZone zone) {
        WarehouseZoneStructureDTO zoneDTO = new WarehouseZoneStructureDTO();
        zoneDTO.setId(zone.getId());
        zoneDTO.setName(zone.getName());
        zoneDTO.setParentId(zone.getParent() != null ? zone.getParent().getId() : null);
        zoneDTO.setCreatedBy(zone.getCreatedBy());
        zoneDTO.setUpdatedBy(zone.getUpdatedBy());
        zoneDTO.setCreatedAt(zone.getCreatedAt());
        zoneDTO.setUpdatedAt(zone.getUpdatedAt());
        zoneDTO.setWidth(zone.getWidth());
        zoneDTO.setHeight(zone.getHeight());
        zoneDTO.setLength(zone.getLength());
        zoneDTO.setCapacity(zone.getCapacity());
        zoneDTO.setCanStoreItems(zone.getCanStoreItems());

        // Находим дочерние зоны
        List<WarehouseZone> childZones = warehouseZoneRepository.findByParentId(zone.getId());
        List<WarehouseZoneStructureDTO> childZoneDTOs = childZones.stream()
                .map(this::mapZoneToDTO)
                .collect(Collectors.toList());
        zoneDTO.setChildZones(childZoneDTOs);

        // Находим контейнеры в этой зоне
        List<WarehouseContainer> containers = warehouseContainerRepository.findByWarehouseZoneId(zone.getId());
        List<WarehouseContainerDTO> containerDTOs = containers.stream()
                .map(this::mapContainerToDTO)
                .collect(Collectors.toList());
        zoneDTO.setContainers(containerDTOs);

        return zoneDTO;
    }

    private WarehouseContainerDTO mapContainerToDTO(WarehouseContainer container) {
        WarehouseContainerDTO containerDTO = new WarehouseContainerDTO();
        containerDTO.setId(container.getId());
        containerDTO.setWarehouseZoneId(container.getWarehouseZone().getId());
        containerDTO.setSerialNumber(container.getSerialNumber());
        containerDTO.setCapacity(container.getCapacity());
        containerDTO.setCreatedAt(container.getCreatedAt());
        containerDTO.setUpdatedAt(container.getUpdatedAt());
        containerDTO.setWidth(container.getWidth());
        containerDTO.setHeight(container.getHeight());
        containerDTO.setLength(container.getLength());
        return containerDTO;
    }

    private Double getTotalCapacity(Long warehouseId) {
        return warehouseRepository.getUsedVolumePercent(warehouseId);
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
//        List<WarehouseZoneDTO> parentZones = zones.stream()
//                .filter(Objects::nonNull)
//                .filter(zone -> {
//                    if (zone.getParentId() == null) {
//                        return true;
//                    }
//                    return zones.stream()
//                            .noneMatch(childZone -> zone.getId().equals(childZone.getParentId()));
//                })
//                .toList();
//
//        double totalVolume = parentZones.stream()
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
//        return 100 - ((freeVolume / totalVolume) * 100);
//    }

    public Warehouse getById(Long warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new WarehouseException("Склад не найден"));
    }

}
