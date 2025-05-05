package kz.qasqir.qasqirinventory.api.service.mainprocessservice;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.DocumentException;
import kz.qasqir.qasqirinventory.api.model.entity.*;
import kz.qasqir.qasqirinventory.api.model.request.DocumentRequest;
import kz.qasqir.qasqirinventory.api.model.request.ItemRequest;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
import kz.qasqir.qasqirinventory.api.service.defaultservice.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessIncomingService {

    private final DocumentService documentService;
    private final NomenclatureService nomenclatureService;
    private final WarehouseZoneService warehouseZoneService;
    private final WarehouseContainerService warehouseContainerService;
    private final InventoryRepository inventoryRepository;
    private final UserService userService;
    private final TransactionService transactionService;
    private final CapacityControlService capacityControlService;

    @Transactional(rollbackOn = Exception.class)
    public void processIncomingGoods(DocumentRequest documentDTO) {
        if (documentDTO == null || documentDTO.getItems() == null || documentDTO.getItems().isEmpty()) {
            throw new DocumentException("Документ или позиции документа не могут быть пустыми");
        }

        Document document = documentService.addDocument(documentDTO);
        List<ItemRequest> documentItems = documentDTO.getItems();

        for (ItemRequest item : documentItems) {
            validateItemRequest(item);

            Nomenclature nomenclature = nomenclatureService.getById(item.getNomenclatureId());
            WarehouseZone warehouseZone = warehouseZoneService.getById(item.getWarehouseZoneId());
            WarehouseContainer container = item.getContainerId() != null ? warehouseContainerService.getById(item.getContainerId()) : null;

            BigDecimal requiredVolume = capacityControlService.calculateVolume(nomenclature, item.getQuantity());

            if (container != null) {
                // Проверяем и резервируем емкость контейнера с учетом габаритов и объема
                capacityControlService.reserveContainerCapacity(container, nomenclature, item.getQuantity());
            } else {
                // Резервируем емкость зоны, если контейнер не указан
                capacityControlService.reserveZoneCapacity(warehouseZone, requiredVolume);
            }

            // Поиск или создание записи инвентаря
            Inventory inventory = inventoryRepository.findByNomenclatureIdAndWarehouseZoneIdAndWarehouseContainerId(
                            nomenclature.getId(), warehouseZone.getId(), item.getContainerId())
                    .orElseGet(() -> {
                        Inventory newInventory = new Inventory();
                        newInventory.setNomenclature(nomenclature);
                        newInventory.setWarehouseZone(warehouseZone);
                        newInventory.setWarehouseContainer(container);
                        newInventory.setQuantity(BigDecimal.ZERO);
                        return newInventory;
                    });

            // Обновляем количество в инвентаре
            capacityControlService.updateInventory(inventory, inventory.getQuantity().add(item.getQuantity()));

            // Добавляем транзакцию
            transactionService.addTransaction("INCOMING", document, nomenclature, item.getQuantity(), document.getDocumentDate(), userService.getByUserId(document.getCreatedBy()));
        }
    }

    private void validateItemRequest(ItemRequest item) {
        if (item.getNomenclatureId() == null || item.getWarehouseZoneId() == null || item.getQuantity() == null) {
            throw new DocumentException("Некорректные данные позиции товаров");
        }

        if (item.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DocumentException("Количество товара должно быть положительным числом");
        }
    }
}