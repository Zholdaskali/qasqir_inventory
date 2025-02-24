package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.DocumentException;
import kz.qasqir.qasqirinventory.api.exception.InsufficientStockException;
import kz.qasqir.qasqirinventory.api.exception.NomenclatureException;
import kz.qasqir.qasqirinventory.api.model.request.*;
import kz.qasqir.qasqirinventory.api.model.entity.*;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
import kz.qasqir.qasqirinventory.api.repository.ReturnRepository;
import kz.qasqir.qasqirinventory.api.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import kz.qasqir.qasqirinventory.api.repository.InventoryAuditRepository;
import kz.qasqir.qasqirinventory.api.repository.InventoryAuditResultRepository;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockTransactionService {

    private final InventoryRepository inventoryRepository;
    private final NomenclatureService nomenclatureService;
    private final WarehouseZoneService warehouseZoneService;
    private final TransactionRepository transactionRepository;
    private final DocumentService documentService;
    private final ReturnRepository returnRepository;
    private final WarehouseService warehouseService;
    private final InventoryAuditRepository inventoryAuditRepository;
    private final InventoryAuditResultRepository inventoryAuditResultRepository;
    private final UserService userService;
    private final WarehouseContainerService warehouseContainerService;


    //Сделано +
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

            Transaction transaction = new Transaction();
            transaction.setTransactionType("INCOMING");
            transaction.setDocument(document);
            transaction.setNomenclature(nomenclature);
            transaction.setQuantity(item.getQuantity());
            transaction.setCreatedBy(userService.getByUserId(document.getCreatedBy()));
            transaction.setDate(documentDTO.getDocumentDate());
            transactionRepository.save(transaction);
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

        Transaction importTransaction = new Transaction();
        importTransaction.setTransactionType("IMPORT");
        importTransaction.setDocument(document);
        importTransaction.setDate(documentDTO.getDocumentDate());
        importTransaction.setQuantity(BigDecimal.ZERO);
        transactionRepository.save(importTransaction);
    }

    @Transactional(rollbackOn = Exception.class)
    public void processReturn(ReturnRequest returnRequest) {
        if (returnRequest == null || returnRequest.getNomenclatureId() == null || returnRequest.getQuantity() == null) {
            throw new DocumentException("Некорректные данные запроса на возврат");
        }

        Nomenclature nomenclature = nomenclatureService.getById(returnRequest.getNomenclatureId());
        Document document = documentService.getById(returnRequest.getRelatedDocumentId());

        Return returnItem = new Return();
        returnItem.setReturnType(returnRequest.getReturnType());
        returnItem.setRelatedDocument(document);
        returnItem.setNomenclature(nomenclature);
        returnItem.setQuantity(returnRequest.getQuantity());
        returnItem.setReason(returnRequest.getReason());
        returnRepository.save(returnItem);

        Inventory inventory = inventoryRepository.findByNomenclatureId(nomenclature.getId())
                .orElseThrow(() -> new NomenclatureException("Запись инвентаризации не найдена для ID номенклатуры: " + nomenclature.getId()));

        BigDecimal updatedQuantity = inventory.getQuantity().subtract(returnRequest.getQuantity());
        if (updatedQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new NomenclatureException("Недостаточно запаса для возврата");
        }
        inventory.setQuantity(updatedQuantity);
        inventoryRepository.save(inventory);

        Transaction transaction = new Transaction();
        transaction.setTransactionType("RETURN");
        transaction.setDocument(document);
        transaction.setNomenclature(nomenclature);
        transaction.setQuantity(returnRequest.getQuantity().negate());
        transaction.setDate(document.getDocumentDate());
        transaction.setCreatedBy(userService.getByUserId(document.getCreatedBy()));
        transactionRepository.save(transaction);
    }

    @Transactional(rollbackOn = Exception.class)
    public void processSales(DocumentRequest salesDocument) {
        processTransaction(salesDocument, "OUTGOING");
    }

    @Transactional(rollbackOn = Exception.class)
    public void processProductionTransfer(DocumentRequest productionDocument) {
        processTransaction(productionDocument, "PRODUCTION");
    }

    @Transactional(rollbackOn = Exception.class)
    private void processTransaction(DocumentRequest documentDTO, String transactionType) {
        Document document = documentService.addDocument(documentDTO);
        List<ItemRequest> items = documentDTO.getItems();

        for (ItemRequest item : items) {
            Nomenclature nomenclature = nomenclatureService.getById(item.getNomenclatureId());
            WarehouseZone warehouseZone = warehouseZoneService.getById(item.getWarehouseZoneId());

            Inventory inventory = inventoryRepository.findByNomenclatureIdAndWarehouseZoneId(nomenclature.getId(), warehouseZone.getId()).orElseThrow(() -> new InsufficientStockException("Недостаточно запаса для позиции: " + nomenclature.getName()));

            BigDecimal updatedQuantity = inventory.getQuantity().subtract(item.getQuantity());
            if (updatedQuantity.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientStockException("Недостаточно запаса для позиции: " + nomenclature.getName());
            }
            inventory.setQuantity(updatedQuantity);
            inventoryRepository.save(inventory);

            Transaction transaction = new Transaction();
            transaction.setTransactionType(transactionType);
            transaction.setDocument(document);
            transaction.setNomenclature(nomenclature);
            transaction.setQuantity(item.getQuantity());
            transaction.setDate(documentDTO.getDocumentDate());
            transaction.setCreatedBy(userService.getByUserId(document.getCreatedBy()));
            transactionRepository.save(transaction);
        }

        document.setStatus("COMPLETED");
        documentService.saveDocument(document);
    }

    // Списывание
    @Transactional(rollbackOn = Exception.class)
    public void processWriteOff(ReturnRequest writeOffRequest) {
        if (writeOffRequest == null || writeOffRequest.getNomenclatureId() == null || writeOffRequest.getQuantity() == null) {
            throw new DocumentException("Некорректные данные запроса на списание");
        }

        Nomenclature nomenclature = nomenclatureService.getById(writeOffRequest.getNomenclatureId());
        Document document = documentService.getById(writeOffRequest.getRelatedDocumentId());

        Return returnItem = new Return();
        returnItem.setReturnType(writeOffRequest.getReturnType());
        returnItem.setRelatedDocument(document);
        returnItem.setNomenclature(nomenclature);
        returnItem.setQuantity(writeOffRequest.getQuantity());
        returnItem.setReason(writeOffRequest.getReason());

        returnRepository.save(returnItem);

        Inventory inventory = inventoryRepository.findByNomenclatureId(nomenclature.getId())
                .orElseThrow(() -> new NomenclatureException("Номенклатура не найдена: " + nomenclature.getId()));

        BigDecimal updatedQuantity = inventory.getQuantity().subtract(writeOffRequest.getQuantity());
        if (updatedQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new NomenclatureException("Недостаточно запаса для списания");
        }
        inventory.setQuantity(updatedQuantity);
        inventoryRepository.save(inventory);

        Transaction transaction = new Transaction();
        transaction.setTransactionType("WRITE-OFF");
        transaction.setDocument(document);
        transaction.setNomenclature(nomenclature);
        transaction.setQuantity(writeOffRequest.getQuantity().negate());
        transaction.setDate(document.getDocumentDate());
        transaction.setCreatedBy(userService.getByUserId(document.getCreatedBy()));

        transactionRepository.save(transaction);
    }

    // Перемещение
    @Transactional(rollbackOn = Exception.class)
    public void processTransfer(TransferRequest documentDTO) {
        Document document = documentService.addTransferDocument(documentDTO);
        List<TransferItemsRequest> items = documentDTO.getItems();

        for (TransferItemsRequest item : items) {
            Nomenclature nomenclature = nomenclatureService.getById(item.getNomenclatureId());
            WarehouseZone fromZone = warehouseZoneService.getById(item.getFromWarehouseZoneId());
            WarehouseZone toZone = warehouseZoneService.getById(item.getToWarehouseZoneId());

            Inventory fromInventory = inventoryRepository.findByNomenclatureIdAndWarehouseZoneId(nomenclature.getId(), fromZone.getId())
                    .orElseThrow(() -> new InsufficientStockException("Недостаточно запаса для перемещения"));

            if (fromInventory.getQuantity().compareTo(item.getQuantity()) < 0) {
                throw new InsufficientStockException("Недостаточно запаса для перемещения");
            }

            Inventory toInventory = inventoryRepository.findByNomenclatureIdAndWarehouseZoneId(nomenclature.getId(), toZone.getId())
                    .orElseGet(() -> {
                        Inventory newInventory = new Inventory();
                        newInventory.setNomenclature(nomenclature);
                        newInventory.setWarehouseZone(toZone);
                        newInventory.setQuantity(BigDecimal.ZERO);
                        return newInventory;
                    });

            fromInventory.setQuantity(fromInventory.getQuantity().subtract(item.getQuantity()));
            toInventory.setQuantity(toInventory.getQuantity().add(item.getQuantity()));

            inventoryRepository.save(fromInventory);
            inventoryRepository.save(toInventory);

            Transaction transaction = new Transaction();
            transaction.setTransactionType("TRANSFER");
            transaction.setDocument(document);
            transaction.setNomenclature(nomenclature);
            transaction.setQuantity(item.getQuantity());
            transaction.setDate(documentDTO.getDocumentDate());
            transaction.setCreatedBy(userService.getByUserId(document.getCreatedBy()));

            transactionRepository.save(transaction);
        }

        document.setStatus("COMPLETED");
        documentService.saveDocument(document);
    }


    // Начало инвентаризации
    @Transactional(rollbackOn = Exception.class)
    public String startInventoryCheck(Long warehouseId, Long createdBy) {
        Warehouse warehouse = warehouseService.getById(warehouseId);
        InventoryAudit audit = new InventoryAudit();
        audit.setWarehouse(warehouse);
        audit.setAuditDate(LocalDate.now());
        audit.setStatus("IN_PROGRESS");
        audit.setCreatedBy(userService.getByUserId(createdBy));
        inventoryAuditRepository.save(audit);
        return "Инвентаризация началось";
    }

    // Процесс инвентаризации
    @Transactional(rollbackOn = Exception.class)
    public void processInventoryCheck(Long auditId, List<InventoryAuditResultRequest> results) {
        InventoryAudit audit = inventoryAuditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Inventory audit not found"));

        for (InventoryAuditResultRequest result : results) {
            Nomenclature nomenclature = nomenclatureService.getById(result.getNomenclatureId());
            WarehouseZone warehouseZone = warehouseZoneService.getById(result.getWarehouseZoneId());

            Inventory inventory = inventoryRepository.findByNomenclatureIdAndWarehouseZoneId(nomenclature.getId(), warehouseZone.getId())
                    .orElse(new Inventory(nomenclature, BigDecimal.ZERO, warehouseZone));

            BigDecimal expectedQuantity = inventory.getQuantity();
            BigDecimal actualQuantity = result.getActualQuantity();
            BigDecimal discrepancy = actualQuantity.subtract(expectedQuantity);

            InventoryAuditResult auditResult = new InventoryAuditResult();
            auditResult.setAudit(audit);
            auditResult.setNomenclature(nomenclature);
            auditResult.setWarehouseZone(warehouseZone);
            auditResult.setExpectedQuantity(expectedQuantity);
            auditResult.setActualQuantity(actualQuantity);
            auditResult.setDiscrepancy(discrepancy);
            inventoryAuditResultRepository.save(auditResult);
        }

        audit.setStatus("COMPLETED");
        inventoryAuditRepository.save(audit);
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