package kz.qasqir.qasqirinventory.api.service.mainprocessservice;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.InsufficientStockException;
import kz.qasqir.qasqirinventory.api.model.dto.StockLocationDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Category;
import kz.qasqir.qasqirinventory.api.model.entity.Document;
import kz.qasqir.qasqirinventory.api.model.entity.Inventory;
import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import kz.qasqir.qasqirinventory.api.model.request.OneC.OneCWriteOffItemRequest;
import kz.qasqir.qasqirinventory.api.model.request.OneC.OneCWriteOffRequest;
import kz.qasqir.qasqirinventory.api.model.request.OneC.SyncingWith1CItemRequest;
import kz.qasqir.qasqirinventory.api.model.request.OneC.SyncingWith1CRequest;
import kz.qasqir.qasqirinventory.api.repository.NomenclatureRepository;
import kz.qasqir.qasqirinventory.api.service.defaultservice.CategoryService;
import kz.qasqir.qasqirinventory.api.service.defaultservice.DocumentService;
import kz.qasqir.qasqirinventory.api.service.defaultservice.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessOneCService {

    private static final Long SYSTEM_USER_ID = 6L;

    private final InventoryService inventoryService;
    private final TicketService ticketService;
    private final DocumentService documentService;
    private final CategoryService categoryService;
    private final NomenclatureRepository nomenclatureRepository;

    @Transactional
    public String createIssueRequest(OneCWriteOffRequest request) {
        validateWriteOffRequest(request);

        Document document = documentService.createDocument(
                request.getType(),
                request.getDocumentNumber(),
                null,
                null,
                SYSTEM_USER_ID
        );

        processWriteOffItems(request.getItems(), document, request.getType(), request.getComment());
        return "Issue request successfully submitted!";
    }

    private void validateWriteOffRequest(OneCWriteOffRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Items list cannot be empty");
        }
    }

    private void processWriteOffItems(List<OneCWriteOffItemRequest> items, Document document, String documentType, String comment) {
        for (OneCWriteOffItemRequest item : items) {
            processSingleWriteOffItem(item, document, documentType, comment);
        }
    }

    private void processSingleWriteOffItem(OneCWriteOffItemRequest item, Document document, String documentType, String comment) {
        List<Inventory> inventories = inventoryService.getInventoryByNomenclatureCodeAndQuantity(item.getNomenclatureCode());
        validateAvailableQuantity(item.getNomenclatureCode(), item.getQuantity(), inventories);

        List<StockLocationDTO> locations = new ArrayList<>();
        BigDecimal remainingQuantity = item.getQuantity();

        for (Inventory inventory : inventories) {
            if (remainingQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal quantityToTake = inventory.getQuantity().min(remainingQuantity);
            addStockLocation(locations, inventory, quantityToTake);
            createTicketForInventory(document, documentType, comment, inventory, quantityToTake);

            remainingQuantity = remainingQuantity.subtract(quantityToTake);
        }
    }

    private void validateAvailableQuantity(String nomenclatureCode, BigDecimal requestedQuantity, List<Inventory> inventories) {
        BigDecimal totalAvailable = inventories.stream()
                .map(Inventory::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAvailable.compareTo(requestedQuantity) < 0) {
            throw new InsufficientStockException(
                    String.format("Insufficient stock for %s: requested %s, available %s",
                            nomenclatureCode, requestedQuantity, totalAvailable));
        }
    }

    private void addStockLocation(List<StockLocationDTO> locations, Inventory inventory, BigDecimal quantity) {
        Long warehouseId = inventory.getWarehouseZone() != null ? inventory.getWarehouseZone().getId() : null;
        Long containerId = inventory.getWarehouseContainer() != null ? inventory.getWarehouseContainer().getId() : null;

        locations.add(new StockLocationDTO(warehouseId, containerId, quantity));
    }

    private void createTicketForInventory(Document document, String documentType, String comment,
                                          Inventory inventory, BigDecimal quantity) {
        ticketService.createTicket(
                comment,
                quantity,
                inventory.getId(),
                SYSTEM_USER_ID,
                documentType,
                document.getId()
        );
    }

    @Transactional
    public String syncingWith1C(SyncingWith1CRequest request) {
        validateSyncRequest(request);

        LocalDateTime now = LocalDateTime.now();
        processSyncItems(request.getItems(), now);

        return "Synchronization completed successfully";
    }

    private void validateSyncRequest(SyncingWith1CRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Nomenclature list cannot be empty");
        }
    }

    private void processSyncItems(List<SyncingWith1CItemRequest> items, LocalDateTime syncTime) {
        for (SyncingWith1CItemRequest item : items) {
            Category category = categoryService.syncingWith1CByCategoryName(item.getCategoryName());
            syncNomenclature(item, category, syncTime);
        }
    }

    private void syncNomenclature(SyncingWith1CItemRequest item, Category category, LocalDateTime syncTime) {
        Nomenclature existingNomenclature = nomenclatureRepository.findByCode(item.getCode());

        if (existingNomenclature == null) {
            createNewNomenclature(item, category, syncTime);
        } else {
            updateExistingNomenclature(existingNomenclature, item, category, syncTime);
        }
    }

    private void createNewNomenclature(SyncingWith1CItemRequest item, Category category, LocalDateTime syncTime) {
        Nomenclature nomenclature = new Nomenclature();
        nomenclature.setName(item.getName());
        nomenclature.setArticle(item.getArticle());
        nomenclature.setCode(item.getCode());
        nomenclature.setType(item.getType());
        nomenclature.setCategory(category);
        nomenclature.setMeasurementUnit(item.getMeasurementUnit());
        nomenclature.setTnvedCode(item.getTnvedCode());
        nomenclature.setCreatedBy(SYSTEM_USER_ID);
        nomenclature.setUpdatedBy(SYSTEM_USER_ID);
        nomenclature.setCreatedAt(syncTime);
        nomenclature.setUpdatedAt(syncTime);
        nomenclature.setSyncDate(syncTime);
        nomenclature.setHeight(0.0);
        nomenclature.setLength(0.0);
        nomenclature.setWidth(0.0);
        nomenclature.setVolume(0.0);

        nomenclatureRepository.save(nomenclature);
    }

    private void updateExistingNomenclature(Nomenclature nomenclature, SyncingWith1CItemRequest item,
                                            Category category, LocalDateTime syncTime) {
        nomenclature.setName(item.getName());
        nomenclature.setArticle(item.getArticle());
        nomenclature.setType(item.getType());
        nomenclature.setCategory(category);
        nomenclature.setMeasurementUnit(item.getMeasurementUnit());
        nomenclature.setTnvedCode(item.getTnvedCode());
        nomenclature.setUpdatedBy(SYSTEM_USER_ID);
        nomenclature.setUpdatedAt(syncTime);
        nomenclature.setSyncDate(syncTime);

        nomenclatureRepository.save(nomenclature);
    }
}