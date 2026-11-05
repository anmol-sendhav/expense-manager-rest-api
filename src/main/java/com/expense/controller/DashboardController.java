package com.expense.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expense.dto.response.DashboardResponseDto;
import com.expense.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardResponseDto> getSummary(
            Authentication authentication) {
        String email = authentication.getName();
        DashboardResponseDto response = dashboardService.getDashboardSummary(email);
        return ResponseEntity.ok(response);
    }
}