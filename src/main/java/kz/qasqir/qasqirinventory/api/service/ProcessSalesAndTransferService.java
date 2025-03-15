package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.InsufficientStockException;
import kz.qasqir.qasqirinventory.api.model.request.*;
import kz.qasqir.qasqirinventory.api.model.entity.*;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessSalesAndTransferService {

    private final InventoryRepository inventoryRepository;
    private final NomenclatureService nomenclatureService;
    private final WarehouseZoneService warehouseZoneService;
    private final DocumentService documentService;
    private final UserService userService;
    private final TransactionService transactionService;


    @Transactional(rollbackOn = Exception.class)
    public void processSales(DocumentRequest salesDocument) {
        processTransaction(salesDocument, "SALES");
    }

    @Transactional(rollbackOn = Exception.class)
    public void processProductionTransfer(DocumentRequest productionDocument) {
        processTransaction(productionDocument, "PRODUCTION");
    }

    @Transactional(rollbackOn = Exception.class)
    protected void processTransaction(DocumentRequest documentDTO, String transactionType) {
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

            transactionService.addTransaction(transactionType, document, nomenclature, item.getQuantity(), documentDTO.getDocumentDate(), userService.getByUserId(document.getCreatedBy()));
        }

        document.setStatus("COMPLETED");
        documentService.saveDocument(document);
    }

}