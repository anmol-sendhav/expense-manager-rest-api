package com.expense.service.impl;

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
	public Income addIncome(IncomeRequestDto request) {
	
		User user =userRepository.findById(request.getUserId())
		        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
		
		Income incomes=new Income();
		
		incomes.setAmount(request.getAmount());
		incomes.setAmountDate(request.getAmountDate());
		incomes.setDescription(request.getDescription());
		incomes.setPaymentWay(request.getPaymentWay());
		incomes.setSource(request.getSource());
		incomes.setTitle(request.getTitle());
		incomes.setUser(user);
		//incomes.setUserIncomeId(request.getUserId());
		
		return incomeRepository.save(incomes);
	}

	@Override
	public Income updateIncome(IncomeRequestDto request, Integer userId) {
		Income income=incomeRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("income not found"+userId));
		if(income != null) {
			income.setAmount(request.getAmount());
			income.setAmountDate(request.getAmountDate());
			income.setDescription(request.getDescription());
			income.setPaymentWay(request.getPaymentWay());
			income.setSource(request.getSource());
			income.setTitle(request.getTitle());
			income.setUserIncomeId(request.getUserId());
			incomeRepository.save(income);
		}
		return null;
	}

	@Override
	public void deleteIncomeById(int userId) {
		this.incomeRepository.deleteById(userId);
		
	}

	@Override
	public List <IncomeResponseDto> incomeHistory() {
		List<Income> incomes=(List<Income>) incomeRepository.findAll();
	    List<IncomeResponseDto> responses=new ArrayList<>();
		
		for(Income income : incomes) {
			IncomeResponseDto response=new IncomeResponseDto();
			
			response.setAmount(income.getAmount());
			response.setAmountDate(income.getAmountDate());
			response.setDescription(income.getDescription());
			response.setPaymentWay(income.getPaymentWay());
			response.setSource(income.getSource());
			response.setTitle(income.getTitle());
			response.setUserId(income.getUserIncomeId());
			
			responses.add(response);
		}
		return responses;
	}

	@Override
	public IncomeResponseDto incomeHistoryByIncomeId(int id) {
		Income income=incomeRepository.findById(id).orElseThrow(() ->
		new ResourceNotFoundException("Income Not Found With"+id));
		
		IncomeResponseDto response=new IncomeResponseDto();
		response.setAmount(income.getAmount());
		response.setAmountDate(income.getAmountDate());
		response.setDescription(income.getDescription());
		response.setPaymentWay(income.getPaymentWay());
		response.setSource(income.getSource());
		response.setTitle(income.getTitle());
		response.setUserId(income.getUserIncomeId());
		
		return response;
	}

}
