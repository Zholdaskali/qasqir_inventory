package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.DocumentException;
import kz.qasqir.qasqirinventory.api.model.entity.*;
import kz.qasqir.qasqirinventory.api.model.request.DocumentRequest;
import kz.qasqir.qasqirinventory.api.model.request.ItemRequest;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
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

    // Поступление товара на склад
    @Transactional(rollbackOn = Exception.class)
    public void processIncomingGoods(DocumentRequest documentDTO) {
        if (documentDTO == null || documentDTO.getItems() == null || documentDTO.getItems().isEmpty()) {
            throw new DocumentException("Документ или позиции документа не могут быть пустыми");
        }

        Document document = documentService.addDocument(documentDTO);
        List<ItemRequest> documentItems = documentDTO.getItems();

        for (ItemRequest item : documentItems) {
            if (item.getNomenclatureId() == null || item.getWarehouseZoneId() == null || item.getQuantity() == null) {
                throw new DocumentException("Некорректные данные позиции товаров");
            }

            Nomenclature nomenclature = nomenclatureService.getById(item.getNomenclatureId());
            WarehouseZone warehouseZone = warehouseZoneService.getById(item.getWarehouseZoneId());

            if (item.getContainerId() != null) {
                WarehouseContainer container = warehouseContainerService.getById(item.getContainerId());
                validateItemSizeOrVolume(nomenclature, container, item.getQuantity());

                if (container.getCapacity().compareTo(BigDecimal.valueOf(0)) > 0) {
                    containerCapacityControl(nomenclature, container, item.getQuantity().doubleValue());
                } else {
                    throw new RuntimeException("В контейнере нет места");
                }
            } else {
                zoneCapacityControl(nomenclature, warehouseZone, item.getQuantity().doubleValue());
            }

            Inventory inventory = inventoryRepository.findByNomenclatureIdAndWarehouseZoneId(
                            nomenclature.getId(), warehouseZone.getId())
                    .orElseGet(() -> {
                        Inventory newInventory = new Inventory();
                        newInventory.setNomenclature(nomenclature);
                        newInventory.setWarehouseZone(warehouseZone);
                        newInventory.setQuantity(BigDecimal.ZERO);
                        return newInventory;
                    });

            inventory.setQuantity(inventory.getQuantity().add(item.getQuantity()));
            inventoryRepository.save(inventory);

            transactionService.addTransaction("INCOMING", document, nomenclature, item.getQuantity(), document.getDocumentDate(), userService.getByUserId(document.getCreatedBy()));
        }
    }


    @Transactional(rollbackOn = Exception.class)
    public void processImport(DocumentRequest documentDTO) {
        if (documentDTO == null || documentDTO.getTnvedCode() == null || documentDTO.getDocumentType() == null) {
            throw new DocumentException("Некорректные данные документа импорта");
        }

        if (!"IMPORT".equals(documentDTO.getDocumentType())) {
            throw new DocumentException("Тип документа должен быть IMPORT для обработки импорта");
        }

        Document document = documentService.addDocument(documentDTO);
        processIncomingGoods(documentDTO);
    }


    private void validateItemSizeOrVolume(Nomenclature nomenclature, WarehouseContainer container, BigDecimal quantity) {
        boolean hasDimensions = nomenclature.getHeight() != null
                && nomenclature.getWidth() != null
                && nomenclature.getLength() != null;
        boolean hasVolume = nomenclature.getVolume() != null;

        if (hasDimensions && hasVolume) {
            throw new DocumentException("Товар не может одновременно иметь габариты и объем.");
        }

        if (!hasDimensions && !hasVolume) {
            throw new DocumentException("Товар должен иметь либо габариты, либо объем.");
        }

        double quantityDouble = quantity.doubleValue();

        if (hasDimensions) {
            double itemVolume = (nomenclature.getHeight() * nomenclature.getWidth() * nomenclature.getLength()) * quantityDouble;
            double containerVolume = container.getHeight() * container.getWidth() * container.getLength();

            if (nomenclature.getHeight() > container.getHeight() ||
                    nomenclature.getWidth() > container.getWidth() ||
                    nomenclature.getLength() > container.getLength()) {
                throw new DocumentException("Размеры товара превышают размеры контейнера.");
            }

            if (itemVolume > containerVolume) {
                throw new DocumentException("Объем товара превышает объем контейнера.");
            }
        }

        if (hasVolume) {
            double containerVolume = container.getHeight() * container.getWidth() * container.getLength();
            if (BigDecimal.valueOf(nomenclature.getVolume()).doubleValue() > containerVolume) {
                throw new DocumentException("Объем товара превышает объем контейнера.");
            }
        }
    }



    private void containerCapacityControl(Nomenclature nomenclature, WarehouseContainer container, double quantity) {
        if (nomenclature.getVolume() == null) {
            double itemVolume = (nomenclature.getHeight() * nomenclature.getWidth() * nomenclature.getLength()) * quantity;
            container.setCapacity(container.getCapacity().subtract(BigDecimal.valueOf(itemVolume)));
        } else {
            container.setCapacity(container.getCapacity().subtract(BigDecimal.valueOf(nomenclature.getVolume() * quantity)));
        }
    }

    private void zoneCapacityControl(Nomenclature nomenclature, WarehouseZone zone, double quantity) {
        if (nomenclature.getVolume() == null) {
            double itemVolume = (nomenclature.getHeight() * nomenclature.getWidth() * nomenclature.getLength()) * quantity;
            zone.setCapacity(zone.getCapacity().subtract(BigDecimal.valueOf(itemVolume)));
        } else {
            zone.setCapacity(zone.getCapacity().subtract(BigDecimal.valueOf(nomenclature.getVolume() * quantity)));
        }
    }
}
