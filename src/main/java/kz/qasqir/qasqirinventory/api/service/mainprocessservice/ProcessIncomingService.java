package kz.qasqir.qasqirinventory.api.service.mainprocessservice;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.DocumentException;
import kz.qasqir.qasqirinventory.api.model.entity.*;
import kz.qasqir.qasqirinventory.api.model.request.DocumentFileRequest;
import kz.qasqir.qasqirinventory.api.model.request.DocumentRequest;
import kz.qasqir.qasqirinventory.api.model.request.ItemRequest;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
import kz.qasqir.qasqirinventory.api.service.defaultservice.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Base64;
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
    private final TransactionPlacementService transactionPlacementService;
    private final DocumentFileService documentFileService;

    @Transactional(rollbackOn = Exception.class)
    public void processIncomingGoods(DocumentRequest documentRequest) {
        validateDocumentRequest(documentRequest);

        Document document = documentService.addDocument(documentRequest);
        if (documentRequest.getFileData() != null) {
            byte[] fileData = Base64.getDecoder().decode(documentRequest.getFileData());
            addDocumentFile(documentRequest.getFileName(), fileData, document.getId());
        }
        processDocumentItems(document, documentRequest.getItems());
    }

    private void validateDocumentRequest(DocumentRequest documentRequest) {
        if (documentRequest == null || documentRequest.getItems() == null || documentRequest.getItems().isEmpty()) {
            throw new DocumentException("Document or document items cannot be empty");
        }
    }

    private void processDocumentItems(Document document, List<ItemRequest> items) {
        for (ItemRequest item : items) {
            validateItemRequest(item);
            processSingleItem(document, item);
        }
    }

    private void addDocumentFile(String fileName, byte[] fileData, Long documentId) {
        DocumentFileRequest documentFileRequest = new DocumentFileRequest(documentId, fileName, fileData);
        documentFileService.saveDocumentFile(documentFileRequest);
    }

    private void processSingleItem(Document document, ItemRequest item) {
        Nomenclature nomenclature = nomenclatureService.getById(item.getNomenclatureId());
        WarehouseZone zone = warehouseZoneService.getById(item.getWarehouseZoneId());
        WarehouseContainer container = getContainerIfExists(item.getContainerId());

        BigDecimal requiredVolume = capacityControlService.calculateVolume(nomenclature, item.getQuantity());
        reserveStorageCapacity(zone, container, nomenclature, item.getQuantity(), requiredVolume);

        Inventory inventory = getOrCreateInventory(nomenclature, zone, container, item.getContainerId());
        updateInventoryAndRecordTransaction(document, inventory, nomenclature, zone, container, item.getQuantity());
    }

    private WarehouseContainer getContainerIfExists(Long containerId) {
        return containerId != null ? warehouseContainerService.getById(containerId) : null;
    }

    private void reserveStorageCapacity(WarehouseZone zone, WarehouseContainer container,
                                        Nomenclature nomenclature, BigDecimal quantity, BigDecimal requiredVolume) {
        if (container != null) {
            capacityControlService.reserveContainerCapacity(container, nomenclature, quantity);
        } else {
            capacityControlService.reserveZoneCapacity(zone, requiredVolume);
        }
    }

    private Inventory getOrCreateInventory(Nomenclature nomenclature, WarehouseZone zone,
                                           WarehouseContainer container, Long containerId) {
        return inventoryRepository.findByNomenclatureIdAndWarehouseZoneIdAndWarehouseContainerId(
                        nomenclature.getId(), zone.getId(), containerId)
                .orElseGet(() -> new Inventory(nomenclature, BigDecimal.ZERO, zone, container));
    }

    private void updateInventoryAndRecordTransaction(Document document, Inventory inventory,
                                                     Nomenclature nomenclature, WarehouseZone zone,
                                                     WarehouseContainer container, BigDecimal quantity) {
        capacityControlService.updateInventory(inventory, inventory.getQuantity().add(quantity));

        Transaction transaction = transactionService.addTransaction(
                "INCOMING",
                document,
                nomenclature,
                quantity,
                document.getDocumentDate(),
                userService.getByUserId(document.getCreatedBy())
        );

        transactionPlacementService.saveTransactionPlacement(transaction, zone, container, quantity);
    }

    private void validateItemRequest(ItemRequest item) {
        if (item.getNomenclatureId() == null ||
                item.getWarehouseZoneId() == null ||
                item.getQuantity() == null) {
            throw new DocumentException("Invalid item data: all required fields must be provided");
        }
        if (item.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DocumentException("Item quantity must be positive");
        }
    }
}