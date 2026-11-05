package com.expense.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import com.expense.entity.User;
import com.expense.enums.Category;
import com.expense.repository.UserRepository;
import com.expense.service.ExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

	private final ExpenseService service;
	private final UserRepository userRepository;

	public ExpenseController(ExpenseService service,UserRepository userRepository) {
		this.service=service;
		this.userRepository=userRepository;
	}


	@PostMapping("/addExpense")
	public ResponseEntity<ExpenseResponseDto> addExpenses(
	        @Valid @RequestBody ExpenseRequestDto expense,
	        Authentication authentication) {

	    User user = getAuthenticatedUser(authentication);

	    ExpenseResponseDto response =
	            service.addExpense(expense, user.getId());

	    return ResponseEntity.status(201).body(response);
	}

	@GetMapping("/getAllExpenses")
	public List<ExpenseResponseDto> getAllExpenses(
	        Authentication authentication) {
	    User user = userRepository.findByEmail(authentication.getName())
	    .orElseThrow(() -> new RuntimeException("User not found"));
	    return service.getAllExpenses(user.getId());
	}

	@GetMapping("/getExpenseById/{id}")
	public ResponseEntity<ExpenseResponseDto> getExpenseById(
	        @PathVariable Integer id,
	        Authentication authentication) {

	    User user = userRepository.findByEmail(authentication.getName())
	            .orElseThrow(() ->
	                    new RuntimeException("User not found"));

	    return ResponseEntity.ok(
	            service.getExpensesById(id, user.getId())
	    );
	}

	@PutMapping("/updateExpenseById/{id}")
	public ResponseEntity<ExpenseResponseDto> updateExpense(
	        @Valid @RequestBody ExpenseRequestDto request,
	        @PathVariable Integer id,
	        Authentication authentication) {

	    User user = getAuthenticatedUser(authentication);

	    return ResponseEntity.ok(
	            service.updateExpense(request, id, user.getId())
	    );
	}

    @DeleteMapping("/deleteExpenseById/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Integer id,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);
        service.deleteExpenseById(id, user.getId());

        return ResponseEntity.noContent().build();
    }



    private User getAuthenticatedUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found"));
    }

	//http://localhost:8080/expenses/search?userId=2&category=Food
    @GetMapping("/search")
    public ResponseEntity<List<Expenses>> getByUserIdAndCategory(
            @RequestParam Category category,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                service.searchByCategory(user.getId(), category)
        );
    }

    @GetMapping("/search/month")
    public ResponseEntity<List<Expenses>> getByMonth(
            @RequestParam LocalDate month,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                service.searchByMonth(user.getId(), month)
        );
    }

	// http://localhost:8080/expenses?page=0&size=5
	@GetMapping
	public ResponseEntity<Page<Expenses>> getExpenses(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "5") int size,
	        Authentication authentication) {

	    User user = getAuthenticatedUser(authentication);

	    if (page < 0 || size < 1 || size > 100) {
	        return ResponseEntity.badRequest().build();
	    }

	    return ResponseEntity.ok(
	            service.getExpenses(page, size, user.getId())
	    );
	}	}
