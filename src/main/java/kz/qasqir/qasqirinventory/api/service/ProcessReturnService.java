package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.DocumentException;
import kz.qasqir.qasqirinventory.api.exception.NomenclatureException;
import kz.qasqir.qasqirinventory.api.model.entity.*;
import kz.qasqir.qasqirinventory.api.model.request.ReturnRequest;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
import kz.qasqir.qasqirinventory.api.repository.ReturnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProcessReturnService {

    private final NomenclatureService nomenclatureService;
    private final DocumentService documentService;
    private final ReturnRepository returnRepository;
    private final InventoryRepository inventoryRepository;
    private final TransactionService transactionService;
    private final UserService userService;
    private final WarehouseZoneService warehouseZoneService;
    private final CapacityControlService capacityControlService;

    @Transactional(rollbackOn = Exception.class)
    public void processReturn(ReturnRequest returnRequest) {
        if (returnRequest == null || returnRequest.getNomenclatureId() == null || returnRequest.getQuantity() == null) {
            throw new DocumentException("Некорректные данные запроса на возврат");
        }

        Nomenclature nomenclature = nomenclatureService.getById(returnRequest.getNomenclatureId());
        Document document = documentService.createDocument("RETURN", returnRequest.getDocumentNumber(), null, null, returnRequest.getCreatedBy());

        Return returnItem = new Return();
        returnItem.setReturnType(returnRequest.getReturnType());
        returnItem.setRelatedDocument(document);
        returnItem.setNomenclature(nomenclature);
        returnItem.setQuantity(returnRequest.getQuantity());
        returnItem.setReason(returnRequest.getReason());
        returnRepository.save(returnItem);

        Inventory inventory = inventoryRepository.findById(returnRequest.getInventoryId())
                .orElseThrow(() -> new NomenclatureException("Запись инвентаризации не найдена для ID номенклатуры: " + returnRequest.getInventoryId()));

        BigDecimal updatedQuantity = inventory.getQuantity().subtract(returnItem.getQuantity());
        if (updatedQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new NomenclatureException("Недостаточно запаса для списания");
        }

        WarehouseZone warehouseZone = inventory.getWarehouseZone();
        if (warehouseZone == null) {
            throw new RuntimeException("Зона склада не указана в инвентаре");
        }

        BigDecimal freedVolume = capacityControlService.calculateVolume(nomenclature, returnItem.getQuantity());
        capacityControlService.freeZoneCapacity(warehouseZone, freedVolume);

        capacityControlService.updateInventory(inventory, updatedQuantity);

        transactionService.addTransaction("RETURN", document, nomenclature, returnRequest.getQuantity(), document.getDocumentDate(), userService.getByUserId(document.getCreatedBy()));
    }
}