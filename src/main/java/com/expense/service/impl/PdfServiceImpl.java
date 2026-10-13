package com.expense.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.expense.entity.Expenses;
import com.expense.service.PdfService;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class PdfServiceImpl implements PdfService{

	@Override
	public ByteArrayInputStream generateExpensePdf(List<Expenses> expenses) throws Exception {
		Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);

        document.open();

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);

        Paragraph title = new Paragraph("Expense Report", font);
        title.setAlignment(Element.ALIGN_CENTER);

        document.add(title);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);

        table.addCell("ID");
        table.addCell("Title");
        table.addCell("Amount");
        table.addCell("Category");

        for (Expenses expense : expenses) {
            table.addCell(String.valueOf(expense.getExpenseId()));
            table.addCell(expense.getTitle());
            table.addCell(String.valueOf(expense.getAmount()));
            table.addCell(expense.getCategory().toString());
        }

        document.add(table);

        document.close();

        return new ByteArrayInputStream(out.toByteArray());
	}

}
