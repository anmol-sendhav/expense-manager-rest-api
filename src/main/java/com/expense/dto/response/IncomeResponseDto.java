package com.expense.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.expense.enums.PaymentWay;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class IncomeResponseDto {

	private String title;
	private String source;
	@Enumerated(EnumType.STRING)
	private PaymentWay paymentWay;
	private BigDecimal amount;
	private String description;
	private Integer userId;
	private LocalDate amountDate;
}
