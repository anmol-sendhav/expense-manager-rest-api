package com.expense.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expense.entity.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Integer>{

}
