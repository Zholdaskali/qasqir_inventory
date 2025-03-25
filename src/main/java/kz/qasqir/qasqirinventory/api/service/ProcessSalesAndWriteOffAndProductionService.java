package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.NomenclatureException;
import kz.qasqir.qasqirinventory.api.model.entity.Inventory;
import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import kz.qasqir.qasqirinventory.api.model.entity.Ticket;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProcessSalesAndWriteOffAndProductionService {

    private final InventoryRepository inventoryRepository;
    private final TransactionService transactionService;
    private final WarehouseZoneService warehouseZoneService; // Добавляем сервис для работы с зонами

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

        BigDecimal freedVolume = calculateFreedVolume(inventory.getNomenclature(), ticket.getQuantity());

        inventory.setQuantity(updatedQuantity);

        if (Objects.equals(inventory.getQuantity(), BigDecimal.ZERO)) {
            inventoryRepository.delete(inventory);
        } else {
            inventoryRepository.save(inventory);
        }

        updateZoneCapacity(warehouseZone, freedVolume);

        transactionService.addTransaction(
                ticket.getType(),
                ticket.getDocument(),
                inventory.getNomenclature(),
                ticket.getQuantity(),
                ticket.getDocument().getDocumentDate(),
                ticket.getCreatedBy()
        );
    }

    private BigDecimal calculateFreedVolume(Nomenclature nomenclature, BigDecimal quantity) {
        if (nomenclature.getVolume() != null) {
            return BigDecimal.valueOf(nomenclature.getVolume()).multiply(quantity);
        } else if (nomenclature.getHeight() != null && nomenclature.getWidth() != null && nomenclature.getLength() != null) {
            double itemVolume = nomenclature.getHeight() * nomenclature.getWidth() * nomenclature.getLength();
            return BigDecimal.valueOf(itemVolume).multiply(quantity);
        } else {
            throw new NomenclatureException("У номенклатуры отсутствуют данные об объеме или габаритах");
        }
    }

    private void updateZoneCapacity(WarehouseZone warehouseZone, BigDecimal freedVolume) {
        BigDecimal newCapacity = warehouseZone.getCapacity().add(freedVolume);
        warehouseZone.setCapacity(newCapacity);
        warehouseZoneService.save(warehouseZone);
    }
}