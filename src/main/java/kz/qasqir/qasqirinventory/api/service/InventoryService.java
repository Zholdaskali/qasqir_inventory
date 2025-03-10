package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.mapper.InventoryItemMapper;
import kz.qasqir.qasqirinventory.api.mapper.InventoryMapper;
import kz.qasqir.qasqirinventory.api.model.dto.InventoryDTO;
import kz.qasqir.qasqirinventory.api.model.dto.InventoryItemDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Inventory;
import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import kz.qasqir.qasqirinventory.api.model.request.InventoryRequest;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final NomenclatureService nomenclatureService;
    private final WarehouseZoneService warehouseZoneService;
    private final InventoryMapper inventoryMapper;
    private final InventoryItemMapper inventoryItemMapper;
    private final WarehouseContainerService warehouseContainerService;

    public InventoryService(InventoryRepository inventoryRepository, NomenclatureService nomenclatureService, WarehouseZoneService warehouseZoneService, InventoryMapper inventoryMapper, InventoryItemMapper inventoryItemMapper, WarehouseContainerService warehouseContainerService) {
        this.inventoryRepository = inventoryRepository;
        this.nomenclatureService = nomenclatureService;
        this.warehouseZoneService = warehouseZoneService;
        this.inventoryMapper = inventoryMapper;
        this.inventoryItemMapper = inventoryItemMapper;
        this.warehouseContainerService = warehouseContainerService;
    }

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

    public List<InventoryItemDTO> getAllInventoryItems() {
        try {
            return inventoryRepository.findAll().stream().map(inventoryItemMapper :: toDto).toList();
        } catch (RuntimeException e) {
            throw new RuntimeException("Ошибка при выводе всех продуктов");
        }
    }

    public List<InventoryItemDTO> getAllInventoryItemsByWarehouseZoneId(Long warehouseZoneId) {
        try {
            return inventoryRepository.findAllByWarehouseZoneId(warehouseZoneId).stream().map(inventoryItemMapper :: toDto).toList();
        } catch (RuntimeException e) {
            throw new RuntimeException("Ошибка при выводе всех продуктов");
        }
    }
}

