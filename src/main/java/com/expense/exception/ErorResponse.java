package com.expense.exception;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErorResponse {

	private int status;
	private String message;
	private LocalDateTime timestap;
	
	public ErorResponse(int status,String message) {
		this.status=status;
		this.message=message;
		this.timestap=LocalDateTime.now();
	}
}
