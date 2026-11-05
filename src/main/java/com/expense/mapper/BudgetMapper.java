package com.expense.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.expense.dto.request.BudgetRequestDto;
import com.expense.dto.response.BudgetResponseDto;
import com.expense.entity.Budget;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "user", ignore = true)
	Budget toEntity(BudgetRequestDto dto);


	@Mapping(target = "date", source = "month")
	@Mapping(target = "totalSpent", ignore = true)
	@Mapping(target = "remainingAmount", ignore = true)
	BudgetResponseDto toDto(Budget budget);


	List<BudgetResponseDto> toDto(List<Budget> budgets);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "user", ignore = true)
	void updateBudgetFromDto(
	    BudgetRequestDto dto,
	    @MappingTarget Budget budget
	);
}