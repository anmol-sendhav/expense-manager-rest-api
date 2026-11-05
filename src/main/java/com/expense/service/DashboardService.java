package com.expense.service;

import com.expense.dto.response.DashboardResponseDto;

public interface DashboardService {

	DashboardResponseDto getDashboardSummary(String email);
}
