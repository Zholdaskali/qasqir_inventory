package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.DocumentException;
import kz.qasqir.qasqirinventory.api.exception.NomenclatureException;
import kz.qasqir.qasqirinventory.api.model.entity.Document;
import kz.qasqir.qasqirinventory.api.model.entity.Inventory;
import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import kz.qasqir.qasqirinventory.api.model.entity.Return;
import kz.qasqir.qasqirinventory.api.model.request.ReturnRequest;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
import kz.qasqir.qasqirinventory.api.repository.ReturnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProcessReturnService {

    private final NomenclatureService nomenclatureService;
    private final DocumentService documentService;
    private final ReturnRepository returnRepository;
    private final InventoryRepository inventoryRepository;
    private final TransactionService transactionService;
    private final UserService userService;

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

        transactionService.addTransaction("RETURN", document, nomenclature, returnRequest.getQuantity(), document.getDocumentDate(), userService.getByUserId(document.getCreatedBy()));
    }
}
