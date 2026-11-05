package com.expense.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.expense.entity.Income;

public interface IncomeRepository extends CrudRepository<Income,Integer>{

	@Query("""
		    SELECT SUM(i.amount)
		    FROM Income i
		    WHERE i.user.id = :userId
		    """)
		BigDecimal getTotalIncomeByUserId(
		        @Param("userId") Integer userId
		);
	long countByUser_Id(Integer userId);
	List<Income> findByUser_Id(Integer userId);
	Optional<Income> findByUserIncomeIdAndUser_Id(
	        Integer incomeId,
	        Integer userId
	);


}
