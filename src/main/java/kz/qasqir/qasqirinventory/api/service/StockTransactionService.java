package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.DocumentException;
import kz.qasqir.qasqirinventory.api.exception.NomenclatureException;
import kz.qasqir.qasqirinventory.api.model.dto.DocumentDTO;
import kz.qasqir.qasqirinventory.api.model.dto.DocumentItemDTO;
import kz.qasqir.qasqirinventory.api.model.entity.*;
import kz.qasqir.qasqirinventory.api.model.request.ReturnRequest;
import kz.qasqir.qasqirinventory.api.repository.DocumentRepository;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
import kz.qasqir.qasqirinventory.api.repository.ReturnRepository;
import kz.qasqir.qasqirinventory.api.repository.TransactionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
        if (documentDTO == null || documentDTO.getItems() == null || documentDTO.getItems().isEmpty()) {
            throw new DocumentException("Document or document items cannot be null/empty");
        }

        try {
            List<DocumentItemDTO> documentItems = documentDTO.getItems();

            for (DocumentItemDTO item : documentItems) {
                // Проверки данных
                if (item.getNomenclatureId() == null || item.getWarehouseZoneId() == null || item.getQuantity() == null) {
                    throw new DocumentException("Invalid document item data");
                }

                Nomenclature nomenclature = nomenclatureService.getById(item.getNomenclatureId());
                WarehouseZone warehouseZone = warehouseZoneService.getById(item.getWarehouseZoneId());

                // Обновляем или создаем инвентаризацию
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

                // Создаем транзакцию
                Document document = documentService.getById(documentDTO.getId());
                Transaction transaction = new Transaction();
                transaction.setTransactionType("INCOMING");
                transaction.setDocument(document);
                transaction.setNomenclature(nomenclature);
                transaction.setQuantity(item.getQuantity());
                transaction.setDate(documentDTO.getDocumentDate());
                transactionRepository.save(transaction);

                logger.info("Processed incoming goods for nomenclature: {}", nomenclature.getName());
            }
        } catch (Exception e) {
            logger.error("Error processing incoming goods: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing incoming goods: " + e.getMessage());
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void processImport(DocumentDTO documentDTO) {
        if (documentDTO == null || documentDTO.getTnvedCode() == null || documentDTO.getDocumentType() == null) {
            throw new DocumentException("Invalid import document data");
        }

        if (!"IMPORT".equals(documentDTO.getDocumentType())) {
            throw new DocumentException("Document type must be IMPORT for import processing");
        }

        try {
            processIncomingGoods(documentDTO);
        } catch (Exception e) {
            logger.error("Error processing import: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing import: " + e.getMessage());
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void processReturn(ReturnRequest returnRequest) {
        if (returnRequest == null || returnRequest.getNomenclatureId() == null || returnRequest.getQuantity() == null) {
            throw new DocumentException("Invalid return request data");
        }

        try {
            Nomenclature nomenclature = nomenclatureService.getById(returnRequest.getNomenclatureId());
            Document document = documentService.getById(returnRequest.getRelatedDocumentId());

            // Создаем запись о возврате
            Return returnItem = new Return();
            returnItem.setReturnType(returnRequest.getReturnType());
            returnItem.setRelatedDocument(document);
            returnItem.setNomenclature(nomenclature);
            returnItem.setQuantity(returnRequest.getQuantity());
            returnItem.setReason(returnRequest.getReason());
            returnRepository.save(returnItem);

            // Обновляем инвентаризацию
            Optional<Inventory> inventoryOpt = inventoryRepository.findByNomenclatureId(nomenclature.getId());
            if (inventoryOpt.isEmpty()) {
                throw new NomenclatureException("Inventory record not found for nomenclature ID: " + nomenclature.getId());
            }

            Inventory inventory = inventoryOpt.get();
            BigDecimal updatedQuantity = inventory.getQuantity().subtract(returnRequest.getQuantity());
            if (updatedQuantity.compareTo(BigDecimal.ZERO) < 0) {
                throw new NomenclatureException("Not enough stock for return");
            }

            inventory.setQuantity(updatedQuantity);
            inventoryRepository.save(inventory);

            logger.info("Processed return for nomenclature: {}", nomenclature.getName());
        } catch (Exception e) {
            logger.error("Error processing return: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing return: " + e.getMessage());
        }
    }
}
