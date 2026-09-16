package com.expense.service;

import java.util.List;

import com.expense.dto.request.IncomeRequestDto;
import com.expense.dto.response.IncomeResponseDto;
import com.expense.entity.Income;

public interface IncomeService {

public Income addIncome(IncomeRequestDto request);	
public Income updateIncome(IncomeRequestDto request,Integer userId);
public void deleteIncomeById(int userId);
public List<IncomeResponseDto> incomeHistory();
IncomeResponseDto incomeHistoryByIncomeId(int id);
}
