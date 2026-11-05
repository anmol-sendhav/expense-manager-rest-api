package com.expense.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expense.entity.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Integer>{


	List<Budget> findAllByUser_Id(Integer userId);

    Optional<Budget> findByIdAndUser_Id(Integer id, Integer userId);
}
