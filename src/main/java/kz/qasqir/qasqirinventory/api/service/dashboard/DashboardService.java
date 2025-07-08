package kz.qasqir.qasqirinventory.api.service.dashboard;

import kz.qasqir.qasqirinventory.api.mapper.InventoryMapper;
import kz.qasqir.qasqirinventory.api.model.dto.InventoryDTO;
import kz.qasqir.qasqirinventory.api.model.dto.TopNomenclatureDTO;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseZoneStatsDTO;
import kz.qasqir.qasqirinventory.api.repository.InventoryRepository;
import kz.qasqir.qasqirinventory.api.repository.TransactionRepository;
import kz.qasqir.qasqirinventory.api.repository.WarehouseZoneRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);
    private static final BigDecimal LOW_STOCK_THRESHOLD = BigDecimal.valueOf(10);
    private static final int TOP_NOMENCLATURE_LIMIT = 5;

    private final InventoryRepository inventoryRepository;
    private final WarehouseZoneRepository warehouseZoneRepository;
    private final TransactionRepository transactionRepository;
    private final InventoryMapper inventoryMapper;

    @Cacheable(value = "dashboardStats", key = "'totalInventoryQuantity'")
    public BigDecimal getTotalInventoryQuantity() {
        try {
            return inventoryRepository.getTotalQuantity().orElse(BigDecimal.ZERO);
        } catch (Exception e) {
            logger.error("Ошибка при получении общего количества инвентаря: {}", e.getMessage());
            throw new RuntimeException("Не удалось получить общее количество инвентаря", e);
        }
    }

    @Cacheable(value = "dashboardStats", key = "'zoneFillPercentage'")
    public double getZoneFillPercentage() {
        try {
            BigDecimal totalPhysicalVolume = warehouseZoneRepository.calculateTotalVolumeForStorableZones()
                    .orElse(BigDecimal.ZERO);
            BigDecimal usedCapacity = inventoryRepository.calculateUsedCapacity()
                    .orElse(BigDecimal.ZERO);

            if (totalPhysicalVolume.compareTo(BigDecimal.ZERO) == 0) {
                logger.warn("Общий объем зон равен нулю");
                return 0.0;
            }

            return usedCapacity.multiply(BigDecimal.valueOf(100))
                    .divide(totalPhysicalVolume, 2, RoundingMode.HALF_UP)
                    .doubleValue();
        } catch (Exception e) {
            logger.error("Ошибка при расчете заполненности зон: {}", e.getMessage());
            throw new RuntimeException("Не удалось рассчитать заполненность зон", e);
        }
    }

    @Cacheable(value = "dashboardStats", key = "'transactionCount_'.concat(#startDate).concat('_').concat(#endDate)")
    public long getTransactionCount(LocalDate startDate, LocalDate endDate) {
        try {
            return transactionRepository.countByDateBetween(startDate, endDate);
        } catch (Exception e) {
            logger.error("Ошибка при подсчете транзакций: {}", e.getMessage());
            throw new RuntimeException("Не удалось подсчитать количество транзакций", e);
        }
    }

    @Cacheable(value = "dashboardStats", key = "'topNomenclatures_'.concat(#startDate).concat('_').concat(#endDate).concat('_').concat(#limit)")
    public List<TopNomenclatureDTO> getTopNomenclatures(LocalDate startDate, LocalDate endDate, int limit) {
        try {
            return transactionRepository.findTopNomenclatures(startDate, endDate, PageRequest.of(0, limit));
        } catch (Exception e) {
            logger.error("Ошибка при получении топ-номенклатур: {}", e.getMessage());
            throw new RuntimeException("Не удалось получить топ-номенклатуры", e);
        }
    }

    @Async
    public CompletableFuture<List<TopNomenclatureDTO>> getTopNomenclaturesAsync(LocalDate startDate, LocalDate endDate, int limit) {
        return CompletableFuture.completedFuture(getTopNomenclatures(startDate, endDate, limit));
    }

    @Cacheable(value = "dashboardStats", key = "'lowStockItems'")
    public List<InventoryDTO> getLowStockItems() {
        try {
            return inventoryRepository.findAllByQuantityLessThan(LOW_STOCK_THRESHOLD)
                    .stream()
                    .map(inventoryMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Ошибка при получении товаров с низким запасом", e);
            throw new RuntimeException("Не удалось получить товары с низким запасом", e);
        }
    }

    @Cacheable(value = "dashboardStats", key = "'trendingItems_'.concat(#startDate).concat('_').concat(#endDate).concat('_').concat(#limit)")
    public List<TopNomenclatureDTO> getTrendingItems(LocalDate startDate, LocalDate endDate, int limit) {
        try {
            return transactionRepository.findTrendingItems(startDate, endDate, PageRequest.of(0, limit));
        } catch (Exception e) {
            logger.error("Ошибка при получении трендовых товаров: {}", e.getMessage());
            throw new RuntimeException("Не удалось получить трендовые товары", e);
        }
    }

    @Async
    public CompletableFuture<List<TopNomenclatureDTO>> getTrendingItemsAsync(LocalDate startDate, LocalDate endDate, int limit) {
        return CompletableFuture.completedFuture(getTrendingItems(startDate, endDate, limit));
    }

    @Cacheable(value = "dashboardStats", key = "'demandForecast_'.concat(#startDate).concat('_').concat(#endDate)")
    public List<TopNomenclatureDTO> getDemandForecast(LocalDate startDate, LocalDate endDate) {
        try {
            // Простой прогноз на основе исторических данных
            List<TopNomenclatureDTO> topItems = getTopNomenclatures(startDate, endDate, TOP_NOMENCLATURE_LIMIT);
            return topItems.stream()
                    .map(dto -> new TopNomenclatureDTO(dto.getNomenclatureId(), dto.getNomenclatureName(),
                            dto.getTotalQuantity().multiply(BigDecimal.valueOf(1.1)))) // Увеличение на 10%
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Ошибка при прогнозировании спроса: {}", e.getMessage());
            throw new RuntimeException("Не удалось спрогнозировать спрос", e);
        }
    }

    @Cacheable(value = "dashboardStats", key = "'zoneStats'")
    public List<WarehouseZoneStatsDTO> getZoneStats() {
        try {
            logger.debug("Fetching zone statistics");
            return warehouseZoneRepository.findZoneStats().stream().map(result -> {
                Long id = (Long) result[0];
                String zoneName = (String) result[1];
                String warehouseName = (String) result[2];
                // Безопасное преобразование usedCapacity в BigDecimal
                BigDecimal usedCapacity = result[3] != null ?
                        (result[3] instanceof BigDecimal ? (BigDecimal) result[3] :
                                BigDecimal.valueOf((Double) result[3])) : BigDecimal.ZERO;
                // Безопасное преобразование totalVolume в BigDecimal
                BigDecimal totalVolume = result[4] != null ?
                        (result[4] instanceof BigDecimal ? (BigDecimal) result[4] :
                                BigDecimal.valueOf((Double) result[4])) : BigDecimal.ZERO;

                double fillPercentage = totalVolume.compareTo(BigDecimal.ZERO) == 0 ? 0.0 :
                        usedCapacity.multiply(BigDecimal.valueOf(100))
                                .divide(totalVolume, 2, RoundingMode.HALF_UP)
                                .doubleValue();

                return new WarehouseZoneStatsDTO(id, zoneName, warehouseName, fillPercentage);
            }).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Ошибка при получении статистики зон: {}", e.getMessage());
            throw new RuntimeException("Не удалось получить статистику зон", e);
        }
    }
}