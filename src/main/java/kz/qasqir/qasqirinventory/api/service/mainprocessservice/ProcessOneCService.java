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
import kz.qasqir.qasqirinventory.api.service.defaultservice.NomenclatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessOneCService {

    private final InventoryService inventoryService;
    private final TicketService ticketService;
    private final DocumentService documentService;
    private final CategoryService categoryService;
    private final NomenclatureRepository nomenclatureRepository;

    @Transactional
    public String createIssueRequest(OneCWriteOffRequest oneCWriteOffRequest) {
        List<OneCWriteOffItemRequest> items = oneCWriteOffRequest.getItems();
        if (items == null) {
            throw new RuntimeException("Массив товаров пусто");
        }
        Document document = documentService.createDocument(
                oneCWriteOffRequest.getType(),
                oneCWriteOffRequest.getDocumentNumber(),
                null,
                null,
                6L);
        for (OneCWriteOffItemRequest item : items) {
            List<Inventory> inventories = inventoryService.getInventoryByNomenclatureCodeAndQuantity(item.getNomenclatureCode());
            BigDecimal totalAvailable = inventories.stream()
                    .map(Inventory::getQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal requestedQuantity = item.getQuantity();

            if (totalAvailable.compareTo(requestedQuantity) < 0) {
                throw new InsufficientStockException(
                        String.format("Недостаточно товара %s: требуется %s, доступно %s",
                                item.getNomenclatureCode(), requestedQuantity, totalAvailable));
            }

            List<StockLocationDTO> locations = new ArrayList<>();
            BigDecimal remainingQuantity = requestedQuantity;
            for (Inventory inventory : inventories) {
                if (remainingQuantity.compareTo(BigDecimal.ZERO) <= 0) break;

                BigDecimal quantityToTake = inventory.getQuantity().min(remainingQuantity);

                Long warehouseId = inventory.getWarehouseZone() != null
                        ? inventory.getWarehouseZone().getId()
                        : null;

                Long containerId = inventory.getWarehouseContainer() != null
                        ? inventory.getWarehouseContainer().getId()
                        : null;

                locations.add(new StockLocationDTO(
                        warehouseId,
                        containerId,
                        quantityToTake
                ));

                ticketService.createTicket(oneCWriteOffRequest.getComment(), quantityToTake, inventory.getId(),
                        6L, oneCWriteOffRequest.getType(), document.getId());
                remainingQuantity = remainingQuantity.subtract(quantityToTake);
            }
        }
        return "Заявка успешно отправлена!!!";
    }

    @Transactional
    public String syncingWith1C(SyncingWith1CRequest syncingWith1CRequest) {
        List<SyncingWith1CItemRequest> items = syncingWith1CRequest.getItems();

        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Массив номенклатур пуст");
        }

        LocalDateTime now = LocalDateTime.now();

        for (SyncingWith1CItemRequest item : items) {
            // Проверяем существование записи по уникальному коду 1С
            Nomenclature existingNomenclature = nomenclatureRepository.findByCode(item.getCode());

            Category category = categoryService.syncingWith1CByCategoryName(item.getCategoryName());

            if (existingNomenclature == null) {
                Nomenclature nomenclature = getNomenclature(item, category, now);

                nomenclatureRepository.save(nomenclature);
            } else {
                // Обновляем существующую запись
                existingNomenclature.setName(item.getName());
                existingNomenclature.setArticle(item.getArticle());
                existingNomenclature.setType(item.getType());
                existingNomenclature.setCategory(category);
                existingNomenclature.setMeasurementUnit(item.getMeasurementUnit());
                existingNomenclature.setTnvedCode(item.getTnvedCode());
                existingNomenclature.setUpdatedBy(6L);
                existingNomenclature.setUpdatedAt(now);
                existingNomenclature.setSyncDate(now);

                nomenclatureRepository.save(existingNomenclature);
            }
        }

        return "Синхронизация выполнена успешно";
    }

    private static Nomenclature getNomenclature(SyncingWith1CItemRequest item, Category category, LocalDateTime now) {
        Nomenclature nomenclature = new Nomenclature();
        nomenclature.setName(item.getName());
        nomenclature.setArticle(item.getArticle());
        nomenclature.setCode(item.getCode());
        nomenclature.setType(item.getType());
        nomenclature.setCategory(category);
        nomenclature.setMeasurementUnit(item.getMeasurementUnit());
        nomenclature.setTnvedCode(item.getTnvedCode());
        nomenclature.setCreatedBy(6L);
        nomenclature.setUpdatedBy(6L);
        nomenclature.setCreatedAt(now);
        nomenclature.setUpdatedAt(now);
        nomenclature.setSyncDate(now);
        nomenclature.setHeight(0.0);
        nomenclature.setLength(0.0);
        nomenclature.setWidth(0.0);
        nomenclature.setVolume(0.0);
        return nomenclature;
    }

}
