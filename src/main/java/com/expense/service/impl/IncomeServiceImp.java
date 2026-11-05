package com.expense.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.expense.dto.request.IncomeRequestDto;
import com.expense.dto.response.IncomeResponseDto;
import com.expense.entity.Income;
import com.expense.entity.User;
import com.expense.exception.ResourceNotFoundException;
import com.expense.repository.IncomeRepository;
import com.expense.repository.UserRepository;
import com.expense.service.IncomeService;

@Service
public class IncomeServiceImp implements IncomeService {

	private IncomeRepository incomeRepository;
	private UserRepository userRepository;

	public IncomeServiceImp(IncomeRepository incomeRepository,UserRepository userRepository) {
		this.incomeRepository=incomeRepository;
		this.userRepository=userRepository;
	}

	@Override
	public IncomeResponseDto addIncome(
	        IncomeRequestDto request, Integer userId) {

	    User user = userRepository.findById(userId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("User not found"));

	    Income income = new Income();

	    income.setAmount(request.getAmount());
	    income.setDescription(request.getDescription());
	    income.setPaymentWay(request.getPaymentWay());
	    income.setSource(request.getSource());
	    income.setTitle(request.getTitle());
	    income.setUser(user);
	    income.setAmountDate(LocalDate.now());

	    // Save income
	    Income savedIncome = incomeRepository.save(income);

	    // Convert entity into DTO
	    IncomeResponseDto response = new IncomeResponseDto();

	    response.setIncomeId(savedIncome.getUserIncomeId());
	    response.setTitle(savedIncome.getTitle());
	    response.setSource(savedIncome.getSource());
	    response.setAmount(savedIncome.getAmount());
	    response.setDescription(savedIncome.getDescription());
	    response.setAmountDate(savedIncome.getAmountDate());
	    response.setPaymentWay(savedIncome.getPaymentWay());

	    return response;
	}

	@Override
	public IncomeResponseDto updateIncome(
	        IncomeRequestDto request,
	        Integer incomeId,
	        Integer userId) {

	    Income income = incomeRepository
	            .findByUserIncomeIdAndUser_Id(incomeId, userId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Income not found"));

	    income.setAmount(request.getAmount());
	    income.setDescription(request.getDescription());
	    income.setPaymentWay(request.getPaymentWay());
	    income.setSource(request.getSource());
	    income.setTitle(request.getTitle());

	    Income updatedIncome = incomeRepository.save(income);

	    // Convert entity into DTO
	    IncomeResponseDto response = new IncomeResponseDto();

	    response.setIncomeId(updatedIncome.getUserIncomeId());
	    response.setTitle(updatedIncome.getTitle());
	    response.setSource(updatedIncome.getSource());
	    response.setAmount(updatedIncome.getAmount());
	    response.setDescription(updatedIncome.getDescription());
	    response.setAmountDate(updatedIncome.getAmountDate());
	    response.setPaymentWay(updatedIncome.getPaymentWay());

	    return response;
	}

@Override
public void deleteIncomeById(Integer incomeId, Integer userId) {
    Income income = incomeRepository
            .findByUserIncomeIdAndUser_Id(incomeId, userId)
            .orElseThrow(() ->
                    new RuntimeException("Income not found"));

    incomeRepository.delete(income);
}


@Override
public List<IncomeResponseDto> incomeHistory(Integer userId) {

    List<Income> incomes = incomeRepository.findByUser_Id(userId);
    List<IncomeResponseDto> responses = new ArrayList<>();

    for (Income income : incomes) {
        IncomeResponseDto response = new IncomeResponseDto();

        response.setIncomeId(income.getUserIncomeId());
        response.setTitle(income.getTitle());
        response.setSource(income.getSource());
        response.setAmount(income.getAmount());
        response.setDescription(income.getDescription());
        response.setAmountDate(income.getAmountDate());
        response.setPaymentWay(income.getPaymentWay());

        responses.add(response);
    }

    return responses;
}


	@Override
	public IncomeResponseDto incomeHistoryByIncomeId(
	        Integer incomeId,
	        Integer userId) {

	    Income income = incomeRepository
	            .findByUserIncomeIdAndUser_Id(incomeId, userId)
	            .orElseThrow(() ->
	                    new RuntimeException("Income not found"));

	    IncomeResponseDto response = new IncomeResponseDto();

	    response.setIncomeId(income.getUserIncomeId());
	    response.setTitle(income.getTitle());
	    response.setSource(income.getSource());
	    response.setAmount(income.getAmount());
	    response.setDescription(income.getDescription());
	    response.setAmountDate(income.getAmountDate());
	    response.setPaymentWay(income.getPaymentWay());

	    return response;
	}
}
