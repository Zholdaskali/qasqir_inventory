package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class DashboardDTO {
    private BigDecimal totalInventoryQuantity;
    private double zoneFillPercentage;
    private long transactionCount;
    private List<TopNomenclatureDTO> topNomenclatures;
    private List<InventoryDTO> lowStockItems;
    private List<TopNomenclatureDTO> demandForecast; // Прогноз спроса
    private List<TopNomenclatureDTO> trendingItems;  // Тенденции
    private List<WarehouseZoneStatsDTO> zoneStats;
}