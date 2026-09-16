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

import com.expense.dto.request.IncomeRequestDto;
import com.expense.dto.response.IncomeResponseDto;
import com.expense.entity.Income;
import com.expense.service.IncomeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/income")
public class IncomeController {

	private IncomeService service;
	
	public IncomeController(IncomeService service) {
		this.service=service;
	}
	
	
	@PostMapping("/addIncome")
	public ResponseEntity<Income> addIncome(@RequestBody IncomeRequestDto request) {
		return ResponseEntity.ok(service.addIncome(request));
}
	
	
	@PutMapping("/updateIncomeByUserId/{id}")
	public ResponseEntity<?> updateIncome(@Valid @RequestBody IncomeRequestDto request,@PathVariable("id")
			Integer userId){
			return ResponseEntity.ok(service.updateIncome(request, userId));
	}
	
	@DeleteMapping("deleteIncomeByIncomeId/{id}")
	public ResponseEntity<?> deleteIncomeById(@PathVariable("id") int userId) {
		try {
		 this.service.deleteIncomeById(userId);
		 return ResponseEntity.status(HttpStatus.ACCEPTED)
				 .build();
	}catch(Exception e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(e.getMessage());
	}
}
	@GetMapping("/getIncomeByIncomeId/{id}")
	public ResponseEntity<IncomeResponseDto> incomeHistoryByIncomeId(@PathVariable int id){
			 try {
			 return ResponseEntity.ok(service.incomeHistoryByIncomeId(id));
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.build();
		}
	}
	
	@GetMapping("/getAllIncome")
	public List <IncomeResponseDto> incomeHistory(){
		return this.service.incomeHistory();
	}
	}