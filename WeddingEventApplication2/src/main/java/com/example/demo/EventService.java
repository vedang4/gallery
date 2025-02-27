package com.example.demo;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public Event editEvent(Long id, Event updatedEvent) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        existingEvent.setName(updatedEvent.getName());
        existingEvent.setDate(updatedEvent.getDate());
        existingEvent.setLocation(updatedEvent.getLocation());
        existingEvent.setTime(updatedEvent.getTime()); // No need for format conversion
        existingEvent.setDressCode(updatedEvent.getDressCode());
        existingEvent.setNumberOfPeople(updatedEvent.getNumberOfPeople());
        return eventRepository.save(existingEvent);
    }

    public ByteArrayInputStream generateEventPdf() {
        List<Event> events = getAllEvents();

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Add Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Event Details", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Create Table
            PdfPTable table = new PdfPTable(6); // 6 columns: Name, Date, Location, Time, Dress Code, Number of People
            table.setWidthPercentage(100);

            // Add Header
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            PdfPCell header;
            String[] headers = {"Name", "Date", "Location", "Time", "Dress Code", "Number of People"};
            for (String column : headers) {
                header = new PdfPCell(new Phrase(column, headerFont));
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(header);
            }

            // Add Data
            int totalPeople = 0;
            for (Event event : events) {
                table.addCell(event.getName());
                table.addCell(event.getDate().toString());
                table.addCell(event.getLocation());
                table.addCell(event.getTime());
                table.addCell(event.getDressCode());
                table.addCell(String.valueOf(event.getNumberOfPeople()));
                totalPeople += event.getNumberOfPeople();
            }

            document.add(table);

            // Add Total
            Paragraph total = new Paragraph("Total Number of People: " + totalPeople, headerFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

        } catch (DocumentException ex) {
            ex.printStackTrace();
        } finally {
            document.close();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
