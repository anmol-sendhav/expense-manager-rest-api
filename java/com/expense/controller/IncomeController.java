package com.expense.controller;

import java.util.List;

//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import com.expense.entity.User;
import com.expense.repository.UserRepository;
import com.expense.service.IncomeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/income")
public class IncomeController {

	private final IncomeService service;
	private final UserRepository userRepository;


	public IncomeController(IncomeService service,UserRepository userRepository) {
		this.service=service;
		this.userRepository=userRepository;
	}



	@PostMapping("/addIncome")
	public ResponseEntity<Income> addIncome(
	        @Valid @RequestBody IncomeRequestDto request,
	        Authentication authentication) {

	    User user = userRepository.findByEmail(authentication.getName())
	            .orElseThrow(() ->
	                    new RuntimeException("User not found"));

	    return ResponseEntity.ok(
	            service.addIncome(request, user.getId())
	    );
	}

	@PutMapping("/updateIncomeByUserId/{id}")
	public ResponseEntity<?> updateIncome(
	        @Valid @RequestBody IncomeRequestDto request,
	        @PathVariable Integer id,
	        Authentication authentication) {

	    User user = userRepository.findByEmail(authentication.getName())
	            .orElseThrow(() ->
	                    new RuntimeException("User not found"));

	    return ResponseEntity.ok(
	            service.updateIncome(request, id, user.getId())
	    );
	}

	@DeleteMapping("/deleteIncomeByIncomeId/{id}")
	public ResponseEntity<?> deleteIncomeById(
	        @PathVariable("id") Integer incomeId,
	        Authentication authentication) {

	    User user = userRepository.findByEmail(authentication.getName())
        .orElseThrow(() -> new RuntimeException("User not found"));

	    try {
	        service.deleteIncomeById(incomeId, user.getId());

	        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(e.getMessage());
	    }
	}
	@GetMapping("/getIncomeByIncomeId/{id}")
	public ResponseEntity<IncomeResponseDto> incomeHistoryByIncomeId(
	        @PathVariable int id,
	        Authentication authentication) {

	    User user = userRepository.findByEmail(authentication.getName())
	            .orElseThrow(() ->
	                    new RuntimeException("User not found"));

	    return ResponseEntity.ok(
	            service.incomeHistoryByIncomeId(id, user.getId())
	    );
	}
	@GetMapping("/getAllIncome")
	public List<IncomeResponseDto> incomeHistory(
	        Authentication authentication) {

	    User user = userRepository.findByEmail(authentication.getName())
	            .orElseThrow(() ->
	                    new RuntimeException("Authenticated user not found"));

	    return service.incomeHistory(user.getId());
	}
	}