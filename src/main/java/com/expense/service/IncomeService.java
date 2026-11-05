package com.expense.service;

import java.util.List;

import com.expense.dto.request.IncomeRequestDto;
import com.expense.dto.response.IncomeResponseDto;
import com.expense.entity.Income;

public interface IncomeService {

	IncomeResponseDto addIncome(IncomeRequestDto request, Integer userId);
	IncomeResponseDto updateIncome(
		    IncomeRequestDto request,
		    Integer incomeId,
		    Integer userId
		);
void deleteIncomeById(Integer incomeId,Integer userId);
public List<IncomeResponseDto> incomeHistory(Integer userId);
public IncomeResponseDto incomeHistoryByIncomeId(Integer incomeId,Integer userId);
}
