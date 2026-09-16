package com.expense.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expense.dto.request.ExpenseRequestDto;
import com.expense.dto.response.ExpenseResponseDto;
import com.expense.entity.Expenses;
import com.expense.enums.Category;
import com.expense.service.ExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

	private final ExpenseService service;
	
	public ExpenseController(ExpenseService service) {
		this.service=service;
	}
	
	@PostMapping("/addExpense")
	public ResponseEntity<?> addExpenses(@Valid @RequestBody ExpenseRequestDto expense) {
			return ResponseEntity.ok(service.addExpense(expense));
			}
	
	
	@GetMapping("/getAllExpenses")
	public List<ExpenseResponseDto> getAllExpenses(){
		return service.getAllExpenses();
	}
	
	@GetMapping("/getExpenseById/{id}")
	public ResponseEntity <ExpenseResponseDto> getAllExpensesById(@PathVariable Integer id){
		return ResponseEntity.ok(service.getExpensesById(id));
	}
	
	@DeleteMapping("/deleteExpenseById/{id}")
	public void deleteExpenses(@PathVariable int id) {
		this.service.deleteExpenseById(id);
	}
	
	@PutMapping("/updateExpenseById/{id}")
	public ResponseEntity <?> updateExpense(@Valid @RequestBody ExpenseRequestDto request
			,@PathVariable int id){
			return ResponseEntity.ok(this.service.updateExpense(request, id));
}
	
	
	//http://localhost:8080/expenses/search?userId=2&category=Food
	@GetMapping("/search")
	public ResponseEntity<List<Expenses>> getByUserIdAndCategory(
			@RequestParam Integer userId,
			@RequestParam Category category){
		return ResponseEntity
				.ok(service
				.searchByCategory(userId, category));
	}

	
	@GetMapping("/search/month")
	public ResponseEntity<List<Expenses>> getByMonth(
	        @RequestParam Integer userId,
	        @RequestParam LocalDate month) {

	    return ResponseEntity.ok(service.searchByMonth(userId, month));
	}
	
	
	// http://localhost:8080/expenses?page=0&size=5
	@GetMapping
	public ResponseEntity<Page<Expenses>> getExpenses(
	        @RequestParam(defaultValue = "0")
	        int page,

	        @RequestParam(defaultValue = "5")
	        int size){

	    return ResponseEntity.ok(service.getExpenses(page,size));
	}
	}
