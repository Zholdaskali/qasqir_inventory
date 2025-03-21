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

@Service
@RequiredArgsConstructor
public class ProcessWriteOffService {
    private final NomenclatureService nomenclatureService;
    private final DocumentService documentService;
    private final ReturnRepository returnRepository;
    private final InventoryRepository inventoryRepository;
    private final TransactionService transactionService;
    private final UserService userService;

    // Списывание
    @Transactional(rollbackOn = Exception.class)
    public void processWriteOff(ReturnRequest writeOffRequest) {
        if (writeOffRequest == null || writeOffRequest.getNomenclatureId() == null || writeOffRequest.getQuantity() == null) {
            throw new DocumentException("Некорректные данные запроса на списание");
        }

        Nomenclature nomenclature = nomenclatureService.getById(writeOffRequest.getNomenclatureId());
        Document document = documentService.getById(writeOffRequest.getRelatedDocumentId());

        Return returnItem = new Return();
        returnItem.setReturnType(writeOffRequest.getReturnType());
        returnItem.setRelatedDocument(document);
        returnItem.setNomenclature(nomenclature);
        returnItem.setQuantity(writeOffRequest.getQuantity());
        returnItem.setReason(writeOffRequest.getReason());

        returnRepository.save(returnItem);

        Inventory inventory = inventoryRepository.findByNomenclatureId(nomenclature.getId())
                .orElseThrow(() -> new NomenclatureException("Номенклатура не найдена: " + nomenclature.getId()));

        BigDecimal updatedQuantity = inventory.getQuantity().subtract(writeOffRequest.getQuantity());
        if (updatedQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new NomenclatureException("Недостаточно запаса для списания");
        }
        inventory.setQuantity(updatedQuantity);
        inventoryRepository.save(inventory);

        transactionService.addTransaction("WRITE-OFF", document, nomenclature, writeOffRequest.getQuantity(), document.getDocumentDate(), userService.getByUserId(document.getCreatedBy()));
    }


    @Transactional (rollbackOn = Exception.class)
    public void processTicketWriteOff(Ticket ticket) {

        Inventory inventory = inventoryRepository.findById(ticket.getInventoryId()).orElseThrow(() -> new RuntimeException("Инвентарь не найден"));
        Return returnItem = new Return();
        returnItem.setReturnType(ticket.getType());
        returnItem.setRelatedDocument(ticket.getDocument());
        returnItem.setNomenclature(inventory.getNomenclature());
        returnItem.setQuantity(ticket.getQuantity());
        returnItem.setReason(ticket.getComment());

        returnRepository.save(returnItem);

        BigDecimal updatedQuantity = inventory.getQuantity().subtract(ticket.getQuantity());
        if (updatedQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new NomenclatureException("Недостаточно запаса для списания");
        }
        inventory.setQuantity(updatedQuantity);
        inventoryRepository.save(inventory);

        transactionService.addTransaction("WRITE-OFF", ticket.getDocument(), ticket.getInventory().getNomenclature(), ticket.getQuantity(), ticket.getDocument().getDocumentDate(), ticket.getCreatedBy());
    }

}
