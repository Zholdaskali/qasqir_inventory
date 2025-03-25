package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.model.dto.DashboardDTO;
import kz.qasqir.qasqirinventory.api.model.dto.TopNomenclatureDTO;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseZoneStatsDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Inventory;
import kz.qasqir.qasqirinventory.api.model.entity.Nomenclature;
import kz.qasqir.qasqirinventory.api.model.entity.Transaction;
import kz.qasqir.qasqirinventory.api.model.entity.WarehouseZone;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
import kz.qasqir.qasqirinventory.api.repository.TransactionRepository;
import kz.qasqir.qasqirinventory.api.repository.WarehouseZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final InventoryRepository inventoryRepository;
    private final WarehouseZoneRepository warehouseZoneRepository;

    // Получение общей аналитической доски
    public DashboardDTO getDashboardStats(LocalDate startDate, LocalDate endDate) {
        DashboardDTO dashboard = new DashboardDTO();

        // Общая статистика
        dashboard.setTotalInventoryQuantity(getTotalInventoryQuantity());
        dashboard.setZoneFillPercentage(getZoneFillPercentage());
        dashboard.setTransactionCount(getTransactionCount(startDate, endDate));

        // Анализ движения товаров
        dashboard.setTopNomenclatures(getTopNomenclatures(startDate, endDate, 5));
        dashboard.setLowStockItems(getLowStockItems());
        dashboard.setDemandForecast(getDemandForecast(startDate, endDate)); // Прогноз спроса
        dashboard.setTrendingItems(getTrendingItems(startDate, endDate));   // Тенденции

        // Эффективность зон
        dashboard.setZoneStats(getZoneStats());

        return dashboard;
    }

    // Общее количество товаров на складе
    private BigDecimal getTotalInventoryQuantity() {
        List<Inventory> inventoryList = inventoryRepository.findAll();
        return inventoryList.stream()
                .map(Inventory::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Процент заполненности зон
    private double getZoneFillPercentage() {
        List<WarehouseZone> zones = warehouseZoneRepository.findAll();

        // Фильтруем только зоны, где можно хранить товары (can_store_items = true)
        List<WarehouseZone> storageZones = zones.stream()
                .filter(WarehouseZone::getCanStoreItems)
                .toList();

        // Рассчитываем общий физический объем только для зон, где можно хранить товары
        BigDecimal totalPhysicalVolume = storageZones.stream()
                .map(z -> BigDecimal.valueOf(z.getLength())
                        .multiply(BigDecimal.valueOf(z.getWidth()))
                        .multiply(BigDecimal.valueOf(z.getHeight())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Рассчитываем занятый объем из инвентаря
        BigDecimal usedCapacity = inventoryRepository.findAll().stream()
                .map(inv -> calculateVolume(inv.getNomenclature(), inv.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Если нет доступного объема, возвращаем 0
        if (totalPhysicalVolume.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }

        // Рассчитываем процент заполненности
        return usedCapacity.multiply(BigDecimal.valueOf(100))
                .divide(totalPhysicalVolume, 2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    // Количество транзакций за период
    private long getTransactionCount(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.countByDateBetween(startDate, endDate);
    }

    // Топ-5 номенклатур по количеству транзакций
    private List<TopNomenclatureDTO> getTopNomenclatures(LocalDate startDate, LocalDate endDate, int limit) {
        List<Transaction> transactions = transactionRepository.findByDateBetween(startDate, endDate);
        Map<Nomenclature, BigDecimal> nomenclatureQuantityMap = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getNomenclature,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getQuantity, BigDecimal::add)
                ));

        return nomenclatureQuantityMap.entrySet().stream()
                .map(entry -> new TopNomenclatureDTO(
                        entry.getKey().getId(),
                        entry.getKey().getName(),
                        entry.getValue()
                ))
                .sorted((a, b) -> b.getTotalQuantity().compareTo(a.getTotalQuantity()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Товары с низким запасом
    private List<TopNomenclatureDTO> getLowStockItems() {
        List<Inventory> inventoryList = inventoryRepository.findAll();
        return inventoryList.stream()
                .filter(inventory -> inventory.getQuantity().compareTo(BigDecimal.valueOf(10)) < 0) // Пример: < 10
                .map(inventory -> new TopNomenclatureDTO(
                        inventory.getNomenclature().getId(),
                        inventory.getNomenclature().getName(),
                        inventory.getQuantity()
                ))
                .collect(Collectors.toList());
    }

    // Статистика по зонам
    private List<WarehouseZoneStatsDTO> getZoneStats() {
        return warehouseZoneRepository.findAll().stream()
                .filter(WarehouseZone::getCanStoreItems) // Фильтруем только зоны, где разрешено хранение
                .map(zone -> {
                    BigDecimal usedCapacity = inventoryRepository.findAllByWarehouseZoneId(zone.getId())
                            .stream()
                            .map(inv -> calculateVolume(inv.getNomenclature(), inv.getQuantity()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalVolume = BigDecimal.valueOf(zone.getLength())
                            .multiply(BigDecimal.valueOf(zone.getWidth()))
                            .multiply(BigDecimal.valueOf(zone.getHeight()));

                    double fillPercentage = totalVolume.compareTo(BigDecimal.ZERO) == 0 ? 0.0 :
                            usedCapacity.multiply(BigDecimal.valueOf(100))
                                    .divide(totalVolume, 2, RoundingMode.HALF_UP)
                                    .doubleValue();

                    return new WarehouseZoneStatsDTO(
                            zone.getId(),
                            zone.getName(),
                            fillPercentage,
                            usedCapacity,
                            totalVolume,
                            zone.getWarehouse().getId(),
                            zone.getWarehouse().getName()
                    );
                })
                .collect(Collectors.toList());
    }

    // Прогноз спроса на следующий месяц
    public List<TopNomenclatureDTO> getDemandForecast(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository.findByDateBetween(startDate, endDate);

        // Фильтруем только продажи и списания
        Map<Nomenclature, BigDecimal> demandMap = transactions.stream()
                .filter(t -> t.getTransactionType().equals("SALES") || t.getTransactionType().equals("WRITE-OFF"))
                .collect(Collectors.groupingBy(
                        Transaction::getNomenclature,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getQuantity, BigDecimal::add)
                ));

        // Рассчитываем средний спрос в день и умножаем на 30 для прогноза на месяц
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (daysBetween <= 0) daysBetween = 1; // Избегаем деления на ноль

        long finalDaysBetween = daysBetween;
        return demandMap.entrySet().stream()
                .map(entry -> {
                    BigDecimal avgDailyDemand = entry.getValue().divide(BigDecimal.valueOf(finalDaysBetween), 2, RoundingMode.HALF_UP);
                    BigDecimal monthlyDemand = avgDailyDemand.multiply(BigDecimal.valueOf(30)); // Прогноз на 30 дней
                    return new TopNomenclatureDTO(
                            entry.getKey().getId(),
                            entry.getKey().getName(),
                            monthlyDemand
                    );
                })
                .sorted((a, b) -> b.getTotalQuantity().compareTo(a.getTotalQuantity()))
                .limit(5) // Топ-5 товаров
                .collect(Collectors.toList());
    }

    // Анализ тенденций движения товаров
    public List<TopNomenclatureDTO> getTrendingItems(LocalDate currentStart, LocalDate currentEnd) {
        LocalDate previousStart = currentStart.minusMonths(1);
        LocalDate previousEnd = currentEnd.minusMonths(1);

        // Транзакции за текущий период
        List<Transaction> currentTransactions = transactionRepository.findByDateBetween(currentStart, currentEnd);
        // Транзакции за предыдущий период
        List<Transaction> previousTransactions = transactionRepository.findByDateBetween(previousStart, previousEnd);

        // Суммируем количество по номенклатуре для текущего периода
        Map<Nomenclature, BigDecimal> currentMap = currentTransactions.stream()
                .filter(t -> t.getTransactionType().equals("SALES") || t.getTransactionType().equals("WRITE-OFF"))
                .collect(Collectors.groupingBy(
                        Transaction::getNomenclature,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getQuantity, BigDecimal::add)
                ));

        // Суммируем количество по номенклатуре для предыдущего периода
        Map<Nomenclature, BigDecimal> previousMap = previousTransactions.stream()
                .filter(t -> t.getTransactionType().equals("SALES") || t.getTransactionType().equals("WRITE-OFF"))
                .collect(Collectors.groupingBy(
                        Transaction::getNomenclature,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getQuantity, BigDecimal::add)
                ));

        // Сравниваем и вычисляем разницу
        return currentMap.entrySet().stream()
                .map(entry -> {
                    Nomenclature nomenclature = entry.getKey();
                    BigDecimal currentQty = entry.getValue();
                    BigDecimal previousQty = previousMap.getOrDefault(nomenclature, BigDecimal.ZERO);
                    BigDecimal trendDifference = currentQty.subtract(previousQty); // Положительное = рост, отрицательное = спад
                    return new TopNomenclatureDTO(
                            nomenclature.getId(),
                            nomenclature.getName(),
                            trendDifference // Разница как индикатор тенденции
                    );
                })
                .filter(dto -> dto.getTotalQuantity().compareTo(BigDecimal.ZERO) != 0) // Исключаем нулевые изменения
                .sorted((a, b) -> b.getTotalQuantity().compareTo(a.getTotalQuantity())) // Сортировка по росту
                .limit(5) // Топ-5 изменений
                .collect(Collectors.toList());
    }

    // Вспомогательный метод для расчета объема
    private BigDecimal calculateVolume(Nomenclature nomenclature, BigDecimal quantity) {
        if (nomenclature.getVolume() != null) {
            return BigDecimal.valueOf(nomenclature.getVolume()).multiply(quantity);
        } else if (nomenclature.getHeight() != null && nomenclature.getWidth() != null && nomenclature.getLength() != null) {
            double itemVolume = nomenclature.getHeight() * nomenclature.getWidth() * nomenclature.getLength();
            return BigDecimal.valueOf(itemVolume).multiply(quantity);
        }
        return BigDecimal.ZERO;
    }
}