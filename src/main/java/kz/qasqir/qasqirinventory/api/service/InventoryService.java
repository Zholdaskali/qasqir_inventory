package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.mapper.InventoryMapper;
import kz.qasqir.qasqirinventory.api.mapper.WarehouseZoneMapper;
import kz.qasqir.qasqirinventory.api.model.dto.*;
import kz.qasqir.qasqirinventory.api.model.entity.*;
import kz.qasqir.qasqirinventory.api.model.request.InventoryRequest;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final NomenclatureService nomenclatureService;
    private final WarehouseZoneService warehouseZoneService;
    private final InventoryMapper inventoryMapper;
    private final WarehouseContainerService warehouseContainerService;
    private final WarehouseService warehouseService;
    private final WarehouseZoneMapper warehouseZoneMapper;

    // Добавление новой записи в инвентаризацию
    public InventoryDTO addInventory(InventoryRequest inventoryRequest, Long warehouseZoneId) {
        try {
            Inventory inventory = new Inventory();
            Nomenclature nomenclature = nomenclatureService.getById(inventoryRequest.getNomenclatureId());
            WarehouseZone warehouseZone = warehouseZoneService.getById(warehouseZoneId);

            inventory.setNomenclature(nomenclature);
            inventory.setWarehouseZone(warehouseZone);
            inventory.setQuantity(inventoryRequest.getQuantity());
            inventory.setWarehouseContainer(warehouseContainerService.getById(inventoryRequest.getContainerId()));
            inventory.setCreatedAt(LocalDateTime.now());
            inventory.setUpdatedAt(LocalDateTime.now());

            inventoryRepository.save(inventory);
            return inventoryMapper.toDto(inventory);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error adding inventory: " + e.getMessage());
        }
    }

    // Обновление записи инвентаризации
    public InventoryDTO updateInventory(InventoryRequest inventoryRequest, Long inventoryId) {
        try {
            Inventory existingInventory = inventoryRepository.findById(inventoryId)
                    .orElseThrow(() -> new RuntimeException("Inventory not found with ID: " + inventoryId));

            // Обновление данных
            if (inventoryRequest.getNomenclatureId() != null) {
                Nomenclature nomenclature = nomenclatureService.getById(inventoryRequest.getNomenclatureId());
                existingInventory.setNomenclature(nomenclature);
            }

            if (inventoryRequest.getWarehouseZoneId() != null) {
                WarehouseZone warehouseZone = warehouseZoneService.getById(inventoryRequest.getWarehouseZoneId());
                existingInventory.setWarehouseZone(warehouseZone);
            }

            if (inventoryRequest.getQuantity() != null) {
                existingInventory.setQuantity(inventoryRequest.getQuantity());
            }

            if (inventoryRequest.getContainerId() != null) {
                existingInventory.setWarehouseContainer(warehouseContainerService.getById(inventoryRequest.getContainerId()));
            }

            existingInventory.setUpdatedAt(LocalDateTime.now());
            inventoryRepository.save(existingInventory);

            return inventoryMapper.toDto(existingInventory);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error updating inventory: " + e.getMessage());
        }
    }

    // Получение записи инвентаризации по ID
    public InventoryDTO getInventoryById(Long inventoryId) {
        try {
            Inventory inventory = inventoryRepository.findById(inventoryId)
                    .orElseThrow(() -> new RuntimeException("Inventory not found with ID: " + inventoryId));
            return inventoryMapper.toDto(inventory);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error fetching inventory: " + e.getMessage());
        }
    }

    // Удаление записи инвентаризации
    public void deleteInventory(Long inventoryId) {
        try {
            Inventory inventory = inventoryRepository.findById(inventoryId)
                    .orElseThrow(() -> new RuntimeException("Inventory not found with ID: " + inventoryId));
            inventoryRepository.delete(inventory);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error deleting inventory: " + e.getMessage());
        }
    }

    public WarehouseInventoryDTO getAllInventoryItems(Long warehouseId) {
        try {
            // Получаем склад и преобразуем его в DTO
            Warehouse warehouse = warehouseService.getById(warehouseId);
            if (warehouse == null) {
                throw new RuntimeException("Склад с ID " + warehouseId + " не найден");
            }
            WarehouseDTO warehouseDTO = warehouseService.convertToWarehouseDTO(warehouse);

            // Получаем зоны склада
            List<WarehouseZoneDTO> warehouseZoneDTOs = warehouseZoneService.getAllWarehouseZoneByWarehouseId(warehouseId);

            // Используем изменяемый список для хранения всех InventoryItemDTO
            List<InventoryItemDTO> inventoryItemDTOS = new ArrayList<>();

            // Проходим по зонам и собираем элементы инвентаря
            for (WarehouseZoneDTO warehouseZoneDTO : warehouseZoneDTOs) {
                List<InventoryItemDTO> zoneItems = inventoryRepository
                        .findAllByWarehouseZoneId(warehouseZoneDTO.getId())
                        .stream()
                        .map(this::convertToDTO)
                        .toList();
                inventoryItemDTOS.addAll(zoneItems); // Добавляем все элементы зоны в общий список
            }

            // Возвращаем результат
            return new WarehouseInventoryDTO(warehouseDTO, inventoryItemDTOS);
        } catch (RuntimeException e) {
            throw new RuntimeException("Ошибка при выводе всех продуктов: " + e.getMessage(), e);
        }
    }

    public List<InventoryItemDTO> getAllInventoryItemsByWarehouseZoneId(Long warehouseZoneId) {
        try {
            return inventoryRepository.findAllByWarehouseZoneId(warehouseZoneId).stream().map(this :: convertToDTO).toList();
        } catch (RuntimeException e) {
            throw new RuntimeException("Ошибка при выводе всех продуктов");
        }
    }


    public InventoryItemDTO convertToDTO (Inventory inventory) {
        WarehouseZoneDTO warehouseZoneDTO = warehouseZoneMapper.toDto(inventory.getWarehouseZone());
        WarehouseContainerDTO warehouseContainerDTO = warehouseContainerService.convertToDto(inventory.getWarehouseContainer());
        return new InventoryItemDTO(inventory.getId(), inventory.getNomenclature().getId(), inventory.getNomenclature().getName(), inventory.getNomenclature().getMeasurementUnit(),
        inventory.getNomenclature().getCode(), inventory.getQuantity(), warehouseZoneDTO, warehouseContainerDTO);
    }


//    public class InventoryItemDTO {
//        private Long id;
//        private Long nomenclatureId;
//        private String nomenclatureName;
//        private String measurementUnit;
//        private String code;
//        private BigDecimal quantity;
//        private WarehouseZoneDTO warehouseZone;
//        private Long containerId;
//        private String containerName;
//    }
}

