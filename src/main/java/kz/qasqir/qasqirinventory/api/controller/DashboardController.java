package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.dto.DashboardDTO;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/v1/employee/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // Получение общей аналитической доски за период
    @GetMapping("/stats")
    @Cacheable(value = "dashboardStats", key = "{#startDate, #endDate}")
    public MessageResponse<?> getDashboardStats(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1); // Последний месяц по умолчанию
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // Валидация дат
        if (startDate.isAfter(endDate)) {
            return MessageResponse.of("Ошибка в дате");
        }

        DashboardDTO dashboardDTO = dashboardService.getDashboardStats(startDate, endDate);
        return MessageResponse.of(dashboardDTO);
    }

    @GetMapping("/current")
    @Cacheable(value = "currentDashboardStats")
    public MessageResponse<DashboardDTO> getCurrentDashboardStats() {
        LocalDate today = LocalDate.now();
        DashboardDTO dashboardDTO = dashboardService.getDashboardStats(today, today);
        return MessageResponse.of(dashboardDTO);
    }
}