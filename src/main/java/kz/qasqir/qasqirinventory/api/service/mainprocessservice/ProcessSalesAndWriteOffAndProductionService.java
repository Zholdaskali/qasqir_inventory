package kz.qasqir.qasqirinventory.api.service.mainprocessservice;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.NomenclatureException;
import kz.qasqir.qasqirinventory.api.model.entity.Inventory;
import kz.qasqir.qasqirinventory.api.model.entity.Ticket;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
import kz.qasqir.qasqirinventory.api.service.CapacityControlService;
import kz.qasqir.qasqirinventory.api.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProcessSalesAndWriteOffAndProductionService {

    private final InventoryRepository inventoryRepository;
    private final TransactionService transactionService;
    private final CapacityControlService capacityControlService;

    @Transactional(rollbackOn = Exception.class)
    public void processTicket(Ticket ticket) {
        Inventory inventory = inventoryRepository.findById(ticket.getInventoryId())
                .orElseThrow(() -> new RuntimeException("Инвентарь не найден"));

        BigDecimal updatedQuantity = inventory.getQuantity().subtract(ticket.getQuantity());
        if (updatedQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new NomenclatureException("Недостаточно запаса для списания");
        }

        WarehouseZone warehouseZone = inventory.getWarehouseZone();
        if (warehouseZone == null) {
            throw new RuntimeException("Зона склада не указана в инвентаре");
        }

        BigDecimal freedVolume = capacityControlService.calculateVolume(inventory.getNomenclature(), ticket.getQuantity());
        capacityControlService.freeZoneCapacity(warehouseZone, freedVolume);

        capacityControlService.updateInventory(inventory, updatedQuantity);

        transactionService.addTransaction(
                ticket.getType(),
                ticket.getDocument(),
                inventory.getNomenclature(),
                ticket.getQuantity(),
                ticket.getDocument().getDocumentDate(),
                ticket.getCreatedBy()
        );
    }
}