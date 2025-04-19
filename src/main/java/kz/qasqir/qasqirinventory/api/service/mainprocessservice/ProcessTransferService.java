package kz.qasqir.qasqirinventory.api.service.mainprocessservice;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.InsufficientStockException;
import kz.qasqir.qasqirinventory.api.model.entity.Document;
import kz.qasqir.qasqirinventory.api.model.entity.Inventory;
import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import kz.qasqir.qasqirinventory.api.model.request.TransferItemsRequest;
import kz.qasqir.qasqirinventory.api.model.request.TransferRequest;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
import kz.qasqir.qasqirinventory.api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessTransferService {
    private final DocumentService documentService;
    private final NomenclatureService nomenclatureService;
    private final WarehouseZoneService warehouseZoneService;
    private final InventoryRepository inventoryRepository;
    private final TransactionService transactionService;
    private final UserService userService;
    private final CapacityControlService capacityControlService;

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
                        return inventoryRepository.saveAndFlush(newInventory);
                    });

            BigDecimal transferVolume = capacityControlService.calculateVolume(nomenclature, item.getQuantity());
            capacityControlService.freeZoneCapacity(fromZone, transferVolume);
            capacityControlService.reserveZoneCapacity(toZone, transferVolume);

            capacityControlService.updateInventory(fromInventory, fromInventory.getQuantity().subtract(item.getQuantity()));
            capacityControlService.updateInventory(toInventory, toInventory.getQuantity().add(item.getQuantity()));

            transactionService.addTransaction("TRANSFER", document, nomenclature, item.getQuantity(),
                    documentDTO.getDocumentDate(), userService.getByUserId(document.getCreatedBy()));
        }

        document.setStatus("COMPLETED");
        documentService.saveDocument(document);
    }
}