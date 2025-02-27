package com.example.demo;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfService {

    public byte[] generatePdf() {
        Document document = new Document();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("This is a sample PDF document."));
            document.close();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new PdfGenerationException("Failed to generate PDF", e);
        }
    }
}
