package com.example.demo;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    public Guest saveGuest(Guest guest) {
        if (guest.getMembers() <= 0) {
            throw new IllegalArgumentException("Members count must be greater than zero.");
        }
        return guestRepository.save(guest);
    }

    public void deleteGuest(Long id) {
        guestRepository.deleteById(id);
    }

    public void sendInviteToGuests(List<Long> guestIds, String message) {
        for (Long id : guestIds) {
            Guest guest = guestRepository.findById(id).orElse(null);
            if (guest != null) {
                System.out.println("Message sent to " + guest.getPhone() + ": " + message);
            }
        }
    }

    public byte[] generateGuestListPdf() throws IOException {
        List<Guest> guests = getAllGuests();
        int totalPeople = guests.stream().mapToInt(Guest::getMembers).sum(); // Calculate total number of people

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);

            document.open();

            // Add the header
            document.add(new Paragraph("#Ved & Vaish Wedding Guest List"));
            document.add(new Paragraph(" ")); // Blank line

            // Create a table with three columns
            PdfPTable table = new PdfPTable(3); // Columns: Name, Phone, Members
            table.setWidthPercentage(100); // Table width fills the page
            table.setSpacingBefore(10f); // Space before the table
            table.setSpacingAfter(10f); // Space after the table

            // Add table headers
            table.addCell("Name");
            table.addCell("Phone");
            table.addCell("Number of People");

            // Add rows for each guest
            for (Guest guest : guests) {
                table.addCell(guest.getName());
                table.addCell(guest.getPhone());
                table.addCell(String.valueOf(guest.getMembers())); // Add number of people
            }

            // Add the table to the document
            document.add(table);

            // Add the total at the end
            document.add(new Paragraph(" ")); // Blank line
            document.add(new Paragraph("Total Number of People: " + totalPeople));

            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
