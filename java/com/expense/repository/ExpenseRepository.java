package com.expense.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.expense.entity.Expenses;
import com.expense.enums.Category;

@Repository
public interface ExpenseRepository extends JpaRepository<Expenses, Integer>{

	@Query("""
		    SELECT COALESCE(SUM(e.amount), 0)
		    FROM Expenses e
		    WHERE e.user.id = :userId
		    AND e.category = :category
		    AND e.date >= :startDate
		    AND e.date < :endDate
		""")
	    BigDecimal getTotalExpenseForBudget(
	            @Param("userId") Integer userId,
	            @Param("category") Category category,
	            @Param("startDate") LocalDate startDate,
	            @Param("endDate") LocalDate endDate
	    );

	@Query("""
		    SELECT SUM(e.amount)
		    FROM Expenses e
		    WHERE e.user.id = :userId
		    """)
		BigDecimal getTotalExpensesByUserId(
		        @Param("userId") Integer userId
		);

	List<Expenses> findByUserIdAndCategory(Integer userId,Category category);

	List<Expenses> findByUserIdAndDateBetween(
            Integer userId,
            LocalDate from,
            LocalDate to);

    List<Expenses> findByUserIdAndDateGreaterThanEqualAndDateLessThan(
            Integer userId,
            LocalDate startDate,
            LocalDate endDate);

    long countByUser_Id(Integer userId);
    List<Expenses> findByUser_Id(Integer userId);
    Optional<Expenses> findByExpenseIdAndUser_Id(
            Integer expenseId,
            Integer userId
    );
    Page<Expenses> findByUser_Id(Integer userId, Pageable pageable);

}
