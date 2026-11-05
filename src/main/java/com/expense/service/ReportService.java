package com.expense.service;

import java.time.LocalDate;

import com.expense.dto.response.ReportResponseDto;

public interface ReportService {

    ReportResponseDto getReport(
            String email,
            LocalDate startDate,
            LocalDate endDate
    );
}