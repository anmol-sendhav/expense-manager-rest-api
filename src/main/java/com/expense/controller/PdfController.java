package com.expense.controller;


import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expense.entity.Expenses;
import com.expense.repository.ExpenseRepository;
import com.expense.service.PdfService;

@RestController
@RequestMapping()
public class PdfController {


	private final ExpenseRepository expenseRepository;
	private final PdfService pdfService;
	
	public PdfController(ExpenseRepository expenseRepository,PdfService pdfService) {
		this.expenseRepository=expenseRepository;
		this.pdfService=pdfService;
	}
	@GetMapping("/expenses/pdf")
	public ResponseEntity<byte[]> generatePdf() throws Exception {

	    List<Expenses> expenses = expenseRepository.findAll();

	    ByteArrayInputStream pdf =
	            pdfService.generateExpensePdf(expenses);

	    HttpHeaders headers = new HttpHeaders();

	    headers.add(
	        HttpHeaders.CONTENT_DISPOSITION,
	        "attachment; filename=expenses.pdf"
	    );

	    return ResponseEntity.ok()
	            .headers(headers)
	            .contentType(MediaType.APPLICATION_PDF)
	            .body(pdf.readAllBytes());
	}
}
