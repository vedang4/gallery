package com.example.demo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/guests")
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @GetMapping
    public String viewGuests(Model model) {
        List<Guest> guests = guestService.getAllGuests();
        int totalPeople = guests.stream().mapToInt(Guest::getMembers).sum();
        model.addAttribute("guests", guests);
        model.addAttribute("totalPeople", totalPeople);
        return "guests"; // guests.html template
    }

    @PostMapping
    public String addGuest(@ModelAttribute Guest guest) {
        guestService.saveGuest(guest);
        return "redirect:/guests";
    }

    @GetMapping("/delete/{id}")
    public String deleteGuest(@PathVariable Long id) {
        guestService.deleteGuest(id);
        return "redirect:/guests";
    }

    @PostMapping("/invite")
    public String sendInvite(@RequestParam("message") String message,
                             @RequestParam("selectedGuests") List<Long> selectedGuests) {
        guestService.sendInviteToGuests(selectedGuests, message);
        return "redirect:/guests";
    }

    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadGuestListPdf() throws IOException {
        try {
            byte[] pdfContent = guestService.generateGuestListPdf();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=VedAndVaishWeddingGuestList.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(pdfContent);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
