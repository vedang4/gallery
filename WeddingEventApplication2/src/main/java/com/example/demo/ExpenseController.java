package com.example.demo;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
public class ExpenseController {

    private final ExpenseRepository expenseRepository;

    public ExpenseController(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @GetMapping("/expenses")
    public String viewExpenses(@RequestParam(value = "passkey", required = false) String passkey, Model model) {
        if (!"123890".equals(passkey)) {
            return "redirect:/expenses/auth";
        }
        model.addAttribute("expenses", expenseRepository.findAll());
        model.addAttribute("total", calculateTotalAmount());
        model.addAttribute("expense", new Expense());
        return "expenses";
    }

    @GetMapping("/expenses/auth")
    public String authenticate() {
        return "expense_auth";
    }

    @PostMapping("/expenses")
    public String addExpense(@ModelAttribute Expense expense) {
        expenseRepository.save(expense);
        return "redirect:/expenses?passkey=123890";
    }

    @GetMapping("/expenses/delete/{id}")
    public String deleteExpense(@PathVariable Long id) {
        expenseRepository.deleteById(id);
        return "redirect:/expenses?passkey=123890";
    }

    @GetMapping("/expenses/download-pdf")
    public void downloadPdf(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=expenses.pdf");

        List<Expense> expenses = expenseRepository.findAll();
        double totalAmount = calculateTotalAmount();

        // Create PDF document
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Add heading
        Font headingFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph heading = new Paragraph("#Ved&VaishWedding", headingFont);
        heading.setAlignment(Element.ALIGN_CENTER);
        document.add(heading);

        // Add spacing
        document.add(new Paragraph(" "));

        // Add table for expenses
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        PdfPCell header1 = new PdfPCell(new Phrase("Description"));
        PdfPCell header2 = new PdfPCell(new Phrase("Amount"));
        header1.setHorizontalAlignment(Element.ALIGN_CENTER);
        header2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header1);
        table.addCell(header2);

        for (Expense expense : expenses) {
            table.addCell(expense.getDescription());
            table.addCell(String.valueOf(expense.getAmount()));
        }

        // Add total amount
        PdfPCell totalCell = new PdfPCell(new Phrase("Total"));
        totalCell.setColspan(1);
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(totalCell);
        table.addCell(String.valueOf(totalAmount));

        document.add(table); // Add the table to the document
        document.close();
    }

    private double calculateTotalAmount() {
        return expenseRepository.findAll()
                .stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }
}
