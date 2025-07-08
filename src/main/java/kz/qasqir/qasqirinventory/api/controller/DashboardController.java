package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.dto.DashboardDTO;
import kz.qasqir.qasqirinventory.api.model.dto.InventoryDTO;
import kz.qasqir.qasqirinventory.api.model.dto.TopNomenclatureDTO;
import kz.qasqir.qasqirinventory.api.model.dto.WarehouseZoneStatsDTO;
import kz.qasqir.qasqirinventory.api.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/v1/employee/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public CompletableFuture<ResponseEntity<DashboardDTO>> getDashboardStats(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                BigDecimal totalInventory = dashboardService.getTotalInventoryQuantity();
                double zoneFillPercentage = dashboardService.getZoneFillPercentage();
                long transactionCount = dashboardService.getTransactionCount(startDate, endDate);
                List<TopNomenclatureDTO> topNomenclatures = dashboardService.getTopNomenclatures(startDate, endDate, 5);
                List<InventoryDTO> lowStockItems = dashboardService.getLowStockItems();
                List<TopNomenclatureDTO> demandForecast = dashboardService.getDemandForecast(startDate, endDate);
                List<TopNomenclatureDTO> trendingItems = dashboardService.getTrendingItems(startDate, endDate, 5);
                List<WarehouseZoneStatsDTO> zoneStats = dashboardService.getZoneStats();

                DashboardDTO stats = new DashboardDTO(
                        totalInventory,
                        zoneFillPercentage,
                        transactionCount,
                        topNomenclatures,
                        lowStockItems,
                        demandForecast,
                        trendingItems,
                        zoneStats
                );

                return ResponseEntity.ok(stats);
            } catch (Exception e) {
                return ResponseEntity.status(500).body(null);
            }
        });
    }
}