package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.WarehouseException;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Warehouse;
import kz.qasqir.qasqirinventory.api.model.request.WareHouseSaveRequest;
import kz.qasqir.qasqirinventory.api.model.request.WarehouseUpdateRequest;
import kz.qasqir.qasqirinventory.api.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

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
        return new WarehouseDTO(warehouse.getId(), warehouse.getName(), warehouse.getLocation(), warehouse.getCreatedAt(), warehouse.getUpdatedAt(), getZonesCount(warehouse.getId()));
    }

    private int getZonesCount(Long WarehouseId) {
        return warehouseZoneService.getAllWarehouseZoneByWarehouseId(WarehouseId).size();
    }

    public Warehouse getById(Long warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new WarehouseException("Склад не найден"));
    }

}
