package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.DocumentException;
import kz.qasqir.qasqirinventory.api.exception.InsufficientStockException;
import kz.qasqir.qasqirinventory.api.exception.NomenclatureException;
import kz.qasqir.qasqirinventory.api.model.dto.DocumentDTO;
import kz.qasqir.qasqirinventory.api.model.dto.DocumentItemDTO;
import kz.qasqir.qasqirinventory.api.model.entity.*;
import kz.qasqir.qasqirinventory.api.model.request.ReturnRequest;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
import kz.qasqir.qasqirinventory.api.repository.ReturnRepository;
import kz.qasqir.qasqirinventory.api.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StockTransactionService {

    private final InventoryRepository inventoryRepository;
    private final NomenclatureService nomenclatureService;
    private final WarehouseZoneService warehouseZoneService;
    private final TransactionRepository transactionRepository;
    private final DocumentService documentService;
    private final ReturnRepository returnRepository;

    public StockTransactionService(InventoryRepository inventoryRepository,
                                   NomenclatureService nomenclatureService,
                                   WarehouseZoneService warehouseZoneService,
                                   TransactionRepository transactionRepository,
                                   DocumentService documentService,
                                   ReturnRepository returnRepository) {
        this.inventoryRepository = inventoryRepository;
        this.nomenclatureService = nomenclatureService;
        this.warehouseZoneService = warehouseZoneService;
        this.transactionRepository = transactionRepository;
        this.documentService = documentService;
        this.returnRepository = returnRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public void processIncomingGoods(DocumentDTO documentDTO) {
        System.out.println(documentDTO.getItems());
        if (documentDTO == null || documentDTO.getItems() == null || documentDTO.getItems().isEmpty()) {
            throw new DocumentException("Документ или позиции документа не могут быть пустыми");
        }

        try {
            List<DocumentItemDTO> documentItems = documentDTO.getItems();

            for (DocumentItemDTO item : documentItems) {
                if (item.getNomenclatureId() == null || item.getWarehouseZoneId() == null || item.getQuantity() == null) {
                    throw new DocumentException("Некорректные данные позиции документа");
                }

                Nomenclature nomenclature = nomenclatureService.getById(item.getNomenclatureId());
                WarehouseZone warehouseZone = warehouseZoneService.getById(item.getWarehouseZoneId());

                Inventory inventory = inventoryRepository.findByNomenclatureIdAndWarehouseZoneId(nomenclature.getId(), warehouseZone.getId());
                if (inventory == null) {
                    inventory = new Inventory();
                    inventory.setNomenclature(nomenclature);
                    inventory.setWarehouseZone(warehouseZone);
                    inventory.setQuantity(item.getQuantity());
                } else {
                    BigDecimal currentQuantity = inventory.getQuantity() != null ? inventory.getQuantity() : BigDecimal.ZERO;
                    BigDecimal itemQuantity = item.getQuantity();
                    inventory.setQuantity(currentQuantity.add(itemQuantity));
                }

                inventoryRepository.save(inventory);

                Document document = documentService.getById(documentDTO.getId());
                Transaction transaction = new Transaction();
                transaction.setTransactionType("INCOMING");
                transaction.setDocument(document);
                transaction.setNomenclature(nomenclature);
                transaction.setQuantity(item.getQuantity());
                transaction.setDate(documentDTO.getDocumentDate());
                transactionRepository.save(transaction);

            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обработке поступающих товаров: " + e.getMessage());
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void processImport(DocumentDTO documentDTO) {
        if (documentDTO == null || documentDTO.getTnvedCode() == null || documentDTO.getDocumentType() == null) {
            throw new DocumentException("Некорректные данные документа импорта");
        }

        if (!"IMPORT".equals(documentDTO.getDocumentType())) {
            throw new DocumentException("Тип документа должен быть IMPORT для обработки импорта");
        }

        try {
            processIncomingGoods(documentDTO);

            Transaction importTransaction = new Transaction();
            importTransaction.setTransactionType("IMPORT");
            importTransaction.setDocument(documentService.getById(documentDTO.getId()));
            importTransaction.setDate(documentDTO.getDocumentDate());
            importTransaction.setQuantity(BigDecimal.ZERO);
            transactionRepository.save(importTransaction);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обработке импорта: " + e.getMessage());
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void processReturn(ReturnRequest returnRequest) {
        if (returnRequest == null || returnRequest.getNomenclatureId() == null || returnRequest.getQuantity() == null) {
            throw new DocumentException("Некорректные данные запроса на возврат");
        }

        try {
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
            transaction.setQuantity(returnRequest.getQuantity().negate()); // Отрицательное значение для возврата
            transaction.setDate(document.getDocumentDate());
            transactionRepository.save(transaction);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обработке возврата: " + e.getMessage());
        }
    }


    private void processTransaction(DocumentDTO documentDTO, String transactionType) {
        try {
            List<DocumentItemDTO> items = documentDTO.getItems();
            Document document = documentService.getById(documentDTO.getId());

            for (DocumentItemDTO item : items) {
                Nomenclature nomenclature = nomenclatureService.getById(item.getNomenclatureId());
                WarehouseZone warehouseZone = warehouseZoneService.getById(item.getWarehouseZoneId());

                Inventory inventory = inventoryRepository.findByNomenclatureIdAndWarehouseZoneId(
                        nomenclature.getId(), warehouseZone.getId());
                if (inventory == null || inventory.getQuantity().compareTo(item.getQuantity()) < 0) {
                    throw new InsufficientStockException("Недостаточно запаса для позиции: " + nomenclature.getName());
                }
                inventory.setQuantity(inventory.getQuantity().subtract(item.getQuantity()));
                inventoryRepository.save(inventory);

                Transaction transaction = new Transaction();
                transaction.setTransactionType(transactionType);
                transaction.setDocument(document);
                transaction.setNomenclature(nomenclature);
                transaction.setQuantity(item.getQuantity());
                transaction.setDate(documentDTO.getDocumentDate());
                transactionRepository.save(transaction);
            }

            document.setStatus("COMPLETED");
            documentService.saveDocument(document);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обработке " + transactionType.toLowerCase() + ": " + e.getMessage());
        }
    }

    public void processSales(DocumentDTO salesDocument) {
        processTransaction(salesDocument, "OUTGOING");
    }

    public void processProductionTransfer(DocumentDTO productionDocument) {
        processTransaction(productionDocument, "PRODUCTION");
    }
}
