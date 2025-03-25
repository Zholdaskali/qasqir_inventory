package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.DocumentException;
import kz.qasqir.qasqirinventory.api.model.entity.Inventory;
import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseContainer;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
import kz.qasqir.qasqirinventory.api.repository.WarehouseContainerRepository;
import kz.qasqir.qasqirinventory.api.repository.WarehouseZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CapacityControlService {

    private final WarehouseZoneRepository warehouseZoneRepository;
    private final WarehouseContainerRepository warehouseContainerRepository; // Заменяем сервис на репозиторий
    private final InventoryRepository inventoryRepository;

    // Рассчитать объем для номенклатуры
    public BigDecimal calculateVolume(Nomenclature nomenclature, BigDecimal quantity) {
        if (nomenclature.getVolume() != null) {
            return BigDecimal.valueOf(nomenclature.getVolume()).multiply(quantity);
        } else if (nomenclature.getHeight() != null && nomenclature.getWidth() != null && nomenclature.getLength() != null) {
            double itemVolume = nomenclature.getHeight() * nomenclature.getWidth() * nomenclature.getLength();
            return BigDecimal.valueOf(itemVolume).multiply(quantity);
        } else {
            throw new DocumentException("У номенклатуры отсутствуют данные об объеме или габаритах");
        }
    }

    // Проверка и обновление емкости зоны (уменьшение при поступлении)
    public void reserveZoneCapacity(WarehouseZone zone, BigDecimal requiredVolume) {
        BigDecimal remainingCapacity = zone.getCapacity();
        if (remainingCapacity.compareTo(requiredVolume) < 0) {
            throw new DocumentException("Недостаточно места в зоне");
        }
        zone.setCapacity(remainingCapacity.subtract(requiredVolume));
        warehouseZoneRepository.save(zone);
    }

    // Освобождение емкости зоны (увеличение при списании или возврате)
    public void freeZoneCapacity(WarehouseZone zone, BigDecimal freedVolume) {
        BigDecimal newCapacity = zone.getCapacity().add(freedVolume);
        zone.setCapacity(newCapacity);
        warehouseZoneRepository.save(zone);
    }

    // Проверка и обновление емкости контейнера (уменьшение при поступлении)
    public void reserveContainerCapacity(WarehouseContainer container, Nomenclature nomenclature, BigDecimal quantity) {
        BigDecimal remainingCapacity = container.getCapacity();
        BigDecimal requiredVolume = calculateVolume(nomenclature, quantity);

        if (remainingCapacity.compareTo(requiredVolume) < 0) {
            throw new DocumentException("Недостаточно места в контейнере");
        }

        if (nomenclature.getHeight() != null && nomenclature.getWidth() != null && nomenclature.getLength() != null) {
            if (nomenclature.getHeight() > container.getHeight() ||
                    nomenclature.getWidth() > container.getWidth() ||
                    nomenclature.getLength() > container.getLength()) {
                throw new DocumentException("Размеры товара превышают размеры контейнера");
            }
        }

        container.setCapacity(remainingCapacity.subtract(requiredVolume));
        warehouseContainerRepository.save(container); // Сохраняем напрямую через репозиторий
    }

    // Освобождение емкости контейнера (увеличение при списании)
    public void freeContainerCapacity(WarehouseContainer container, BigDecimal freedVolume) {
        BigDecimal newCapacity = container.getCapacity().add(freedVolume);
        container.setCapacity(newCapacity);
        warehouseContainerRepository.save(container); // Сохраняем напрямую через репозиторий
    }

    // Обновление инвентаря с учетом количества (удаление при 0)
    public void updateInventory(Inventory inventory, BigDecimal newQuantity) {
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new DocumentException("Количество инвентаря не может быть отрицательным");
        }
        inventory.setQuantity(newQuantity);
        if (newQuantity.compareTo(BigDecimal.ZERO) == 0) {
            inventoryRepository.delete(inventory);
        } else {
            inventoryRepository.save(inventory);
        }
    }
}