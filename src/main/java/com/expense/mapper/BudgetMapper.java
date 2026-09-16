package com.expense.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.expense.dto.request.BudgetRequestDto;
import com.expense.dto.response.BudgetResponseDto;
import com.expense.entity.Budget;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    Budget toEntity(BudgetRequestDto dto);

    BudgetResponseDto toDto(Budget budget);

	List<BudgetResponseDto> toDto(List<Budget> budgets);
	
	void updateBudgetFromDto(
            BudgetRequestDto dto,
            @MappingTarget Budget budget
    );
}