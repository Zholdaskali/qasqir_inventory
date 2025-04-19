package kz.qasqir.qasqirinventory.api.service.mainprocessservice;

import kz.qasqir.qasqirinventory.api.exception.InsufficientStockException;
import kz.qasqir.qasqirinventory.api.model.dto.StockLocationDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Document;
import kz.qasqir.qasqirinventory.api.model.entity.Inventory;
import kz.qasqir.qasqirinventory.api.model.request.OneC.OneCWriteOffItemRequest;
import kz.qasqir.qasqirinventory.api.model.request.OneC.OneCWriteOffRequest;
import kz.qasqir.qasqirinventory.api.service.DocumentService;
import kz.qasqir.qasqirinventory.api.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessOneCService {

    private final InventoryService inventoryService;
    private final TicketService ticketService;
    private final DocumentService documentService;

    public String createIssueRequest(OneCWriteOffRequest oneCWriteOffRequest) {
        List<OneCWriteOffItemRequest> items = oneCWriteOffRequest.getItems();
        if (items == null) {
            throw new RuntimeException("items пусто");
        }
        Document document = documentService.createDocument(
                oneCWriteOffRequest.getType(),
                oneCWriteOffRequest.getDocumentNumber(),
                null,
                null,
                oneCWriteOffRequest.getCreateBy());
        for (OneCWriteOffItemRequest item : items) {
            List<Inventory> inventories = inventoryService.getInventoryByNomenclatureCodeAndQuantity(item.getNomenclatureCode());
            // Суммируем доступное количество
            BigDecimal totalAvailable = inventories.stream()
                    .map(Inventory::getQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal requestedQuantity = item.getQuantity();

            // Проверяем, хватает ли количества
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
                        oneCWriteOffRequest.getCreateBy(), oneCWriteOffRequest.getType(), document.getId());
                remainingQuantity = remainingQuantity.subtract(quantityToTake);
            }
        }
        return "Заявка успешно отправлена!!!";
    }
}
