package com.expense.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.expense.entity.Expenses;

public interface PdfService {

	ByteArrayInputStream generateExpensePdf(List<Expenses> expenses)
			throws Exception;
}
