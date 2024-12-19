package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.mapper.InventoryMapper;
import kz.qasqir.qasqirinventory.api.model.dto.InventoryDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Inventory;
import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import kz.qasqir.qasqirinventory.api.model.request.DocumentRequest;
import kz.qasqir.qasqirinventory.api.model.request.InventoryRequest;
import kz.qasqir.qasqirinventory.api.model.request.NomenclatureRequest;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final NomenclatureService nomenclatureService;
    private final WarehouseZoneService warehouseZoneService;
    private final InventoryMapper inventoryMapper;

    public InventoryService(InventoryRepository inventoryRepository, NomenclatureService nomenclatureService, WarehouseZoneService warehouseZoneService, InventoryMapper inventoryMapper) {
        this.inventoryRepository = inventoryRepository;
        this.nomenclatureService = nomenclatureService;
        this.warehouseZoneService = warehouseZoneService;
        this.inventoryMapper = inventoryMapper;
    }

    // Добавление новой записи в инвентаризацию
    public InventoryDTO addInventory(InventoryRequest inventoryRequest, Long warehouseZoneId) {
        try {
            Inventory inventory = new Inventory();
            Nomenclature nomenclature = nomenclatureService.getById(inventoryRequest.getNomenclatureId());
            WarehouseZone warehouseZone = warehouseZoneService.getWarehouseZoneById(warehouseZoneId);

            inventory.setNomenclature(nomenclature);
            inventory.setWarehouseZone(warehouseZone);
            inventory.setQuantity(inventoryRequest.getQuantity());
            inventory.setContainerSerial(inventoryRequest.getContainerSerial());
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
                WarehouseZone warehouseZone = warehouseZoneService.getWarehouseZoneById(inventoryRequest.getWarehouseZoneId());
                existingInventory.setWarehouseZone(warehouseZone);
            }

            if (inventoryRequest.getQuantity() != null) {
                existingInventory.setQuantity(inventoryRequest.getQuantity());
            }

            if (inventoryRequest.getContainerSerial() != null) {
                existingInventory.setContainerSerial(inventoryRequest.getContainerSerial());
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
}

