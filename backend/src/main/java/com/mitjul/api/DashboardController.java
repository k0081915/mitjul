package com.mitjul.api;

import com.mitjul.dto.dashboard.DashboardSummaryResponse;
import com.mitjul.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary(
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month
    ) {
        return dashboardService.getSummary(year, month);
    }
}
