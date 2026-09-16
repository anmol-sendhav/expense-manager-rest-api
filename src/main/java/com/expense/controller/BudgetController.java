package com.expense.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expense.dto.request.BudgetRequestDto;
import com.expense.dto.request.BudgetUsageDto;
import com.expense.dto.response.BudgetResponseDto;
import com.expense.service.BudgetService;

@RestController
@RequestMapping("/budget")
public class BudgetController {

	private BudgetService budgetService;
	
	public BudgetController(BudgetService budgetService) {
		this.budgetService=budgetService;
	}
	
	@PostMapping("/addBudget")
	public ResponseEntity<?> savesBudget(@RequestBody BudgetRequestDto budget){
		try {
			this.budgetService.saveBudget(budget);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Budget Added");
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
	
	@GetMapping("/getAllBudget")
	public List<BudgetResponseDto> getAllBudget(){
		return this.budgetService.getAllBudgetsForUser();
		}
	
	@GetMapping("/getBudget/{id}")
	public ResponseEntity<?> getBudgetByIds(@PathVariable int id){
		try {
			return ResponseEntity.ok(budgetService.getBudgetById(id));
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.build();
		}
	}
	
	
	@DeleteMapping("/deleteBudget/{id}")
	public void deleteBudgetById(@PathVariable int id) {
		this.budgetService.deleteBudgetById(id);
	}
	
	@PutMapping("/modifyBudget/{id}")
	public ResponseEntity<?> modifyBudget(@RequestBody BudgetRequestDto dto,
		@PathVariable int id){
		try {
			return ResponseEntity.ok(this.budgetService.updateBudget(dto, id));
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
	
	@GetMapping("/{budgetId}/usage")
	public ResponseEntity<BudgetUsageDto> budgetUsage(@PathVariable Integer budgetId){
		return ResponseEntity.ok(budgetService.getBudgetUsage(budgetId));
	}
	}
	