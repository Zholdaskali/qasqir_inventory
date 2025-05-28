package kz.qasqir.qasqirinventory.api.service.defaultservice;

import kz.qasqir.qasqirinventory.api.exception.DocumentException;
import kz.qasqir.qasqirinventory.api.model.entity.*;
import kz.qasqir.qasqirinventory.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CapacityControlService {

    private final WarehouseZoneRepository warehouseZoneRepository;
    private final WarehouseContainerRepository warehouseContainerRepository; // Заменяем сервис на репозиторий
    private final InventoryRepository inventoryRepository;
    private final TicketRepository ticketRepository;
    private final DocumentRepository documentRepository;

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

    public BigDecimal getContainerOccupiedVolume(WarehouseContainer container) {
        List<Inventory> inventories = inventoryRepository.findByWarehouseContainerId(container.getId());
        return inventories.stream()
                .map(inv -> calculateVolume(inv.getNomenclature(), inv.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void reserveZoneCapacity(WarehouseZone zone, BigDecimal requiredVolume) {
        BigDecimal remainingCapacity = zone.getCapacity();
        if (remainingCapacity.compareTo(requiredVolume) < 0) {
            throw new DocumentException("Недостаточно места в зоне");
        }
        zone.setCapacity(remainingCapacity.subtract(requiredVolume));
        warehouseZoneRepository.save(zone);
    }

    public void freeZoneCapacity(WarehouseZone zone, BigDecimal freedVolume) {
        BigDecimal newCapacity = zone.getCapacity().add(freedVolume);
        zone.setCapacity(newCapacity);
        warehouseZoneRepository.save(zone);
    }

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

    public void freeContainerCapacity(WarehouseContainer container, BigDecimal freedVolume) {
        BigDecimal newCapacity = container.getCapacity().add(freedVolume);
        container.setCapacity(newCapacity);
        warehouseContainerRepository.save(container); // Сохраняем напрямую через репозиторий
    }

    public void updateInventory(Inventory inventory, BigDecimal newQuantity) {
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new DocumentException("Количество инвентаря не может быть отрицательным");
        }
        inventory.setQuantity(newQuantity);
        if (newQuantity.compareTo(BigDecimal.ZERO) == 0) {
            ticketRepository.deleteByInventoryId(inventory.getId());
            inventoryRepository.delete(inventory);
        } else {
            inventoryRepository.save(inventory);
        }
    }

    public void validateNomenclatureSizeChange(Nomenclature nomenclature, Double newVolume, Double newHeight, Double newWidth, Double newLength) {
        List<Inventory> inventories = inventoryRepository.findAllByNomenclatureId(nomenclature.getId());

        for (Inventory inventory : inventories) {
            BigDecimal requiredVolume = calculateVolumeWithNewSizes(nomenclature, inventory.getQuantity(), newVolume, newHeight, newWidth, newLength);

            // Проверка для контейнеров
            if (inventory.getWarehouseContainer().getId() != null) {
                WarehouseContainer container = warehouseContainerRepository.findById(inventory.getWarehouseContainer().getId())
                        .orElseThrow(() -> new DocumentException("Контейнер не найден"));
                if (requiredVolume.compareTo(container.getCapacity()) > 0) {
                    throw new DocumentException("Новый объем превышает вместимость контейнера: " + container.getId());
                }
                if (newHeight != null && newHeight > container.getHeight() ||
                        newWidth != null && newWidth > container.getWidth() ||
                        newLength != null && newLength > container.getLength()) {
                    throw new DocumentException("Новые размеры превышают габариты контейнера: " + container.getId());
                }
            }

            // Проверка для зон
            if (inventory.getWarehouseZone().getId() != null) {
                WarehouseZone zone = warehouseZoneRepository.findById(inventory.getWarehouseZone().getId())
                        .orElseThrow(() -> new DocumentException("Зона не найдена"));
                if (requiredVolume.compareTo(zone.getCapacity()) > 0) {
                    throw new RuntimeException("Новый объем превышает вместимость зоны: " + zone.getId());
                }
            }
        }
    }

    private BigDecimal calculateVolumeWithNewSizes(Nomenclature nomenclature, BigDecimal quantity,
                                                   Double newVolume, Double newHeight, Double newWidth, Double newLength) {
        if (newVolume != null) {
            return BigDecimal.valueOf(newVolume).multiply(quantity);
        } else if (newHeight != null && newWidth != null && newLength != null) {
            return BigDecimal.valueOf(newHeight * newWidth * newLength).multiply(quantity);
        } else {
            return calculateVolume(nomenclature, quantity); // Используем текущие размеры
        }
    }

    public void updateCapacitiesAfterSizeChange(Nomenclature nomenclature) {
        List<Inventory> inventories = inventoryRepository.findAllByNomenclatureId(nomenclature.getId());
        for (Inventory inventory : inventories) {
            BigDecimal oldVolume = calculateVolume(nomenclature, inventory.getQuantity());

            // Освобождаем старый объем
            if (inventory.getWarehouseContainer().getId() != null) {
                WarehouseContainer container = warehouseContainerRepository.findById(inventory.getWarehouseContainer().getId())
                        .orElseThrow(() -> new DocumentException("Контейнер не найден"));
                freeContainerCapacity(container, oldVolume);
            }
            if (inventory.getWarehouseZone().getId() != null) {
                WarehouseZone zone = warehouseZoneRepository.findById(inventory.getWarehouseZone().getId())
                        .orElseThrow(() -> new DocumentException("Зона не найдена"));
                freeZoneCapacity(zone, oldVolume);
            }

            // Резервируем новый объем
            if (inventory.getWarehouseContainer().getId() != null) {
                WarehouseContainer container = warehouseContainerRepository.findById(inventory.getWarehouseContainer().getId())
                        .orElseThrow(() -> new DocumentException("Контейнер не найден"));
                reserveContainerCapacity(container, nomenclature, inventory.getQuantity());
            }
            if (inventory.getWarehouseZone().getId() != null) {
                WarehouseZone zone = warehouseZoneRepository.findById(inventory.getWarehouseZone().getId())
                        .orElseThrow(() -> new DocumentException("Зона не найдена"));
                reserveZoneCapacity(zone, calculateVolume(nomenclature, inventory.getQuantity()));
            }
        }
    }
}