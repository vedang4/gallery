package com.example.demo;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/gallery")
public class GalleryController {

    @Autowired
    private GalleryService galleryService;

    private static final String CORRECT_PASSKEY = "890123"; // Replace with the correct passkey
    private static final String GOOGLE_DRIVE_FOLDER_URL = "https://drive.google.com/drive/folders/1QJHOrauukM2rV0qvx3HQVA0tFj3QiEQo"; // Replace with your folder link

    @GetMapping
    public String redirectToGoogleDrive() {
        return "redirect:" + GOOGLE_DRIVE_FOLDER_URL; // Redirect to the Google Drive folder
    }

    @PostMapping("/authenticate")
    public String authenticate(@RequestParam String passkey, RedirectAttributes redirectAttributes) {
        if (CORRECT_PASSKEY.equals(passkey)) {
            return "redirect:/gallery"; // Redirect to Google Drive on correct passkey
        } else {
            redirectAttributes.addFlashAttribute("error", "Incorrect passkey. Please try again.");
            return "redirect:/login"; // Redirect back to the login page with an error message
        }
    }

    @PostMapping("/upload")
    public String uploadPhoto(@RequestParam("photo") MultipartFile photo, RedirectAttributes redirectAttributes) {
        try {
            galleryService.uploadPhoto(photo);
            redirectAttributes.addFlashAttribute("success", "Photo uploaded successfully.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload photo.");
        }
        return "redirect:/gallery"; // Redirect back to Google Drive
    }

    @GetMapping("/delete/{fileName}")
    public String deletePhoto(@PathVariable String fileName, RedirectAttributes redirectAttributes) {
        try {
            galleryService.deletePhoto(fileName);
            redirectAttributes.addFlashAttribute("success", "Photo deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete photo.");
        }
        return "redirect:/gallery"; // Redirect back to Google Drive
    }

    @GetMapping("/download/{fileName}")
    public void downloadPhoto(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        galleryService.downloadPhoto(fileName, response);
    }
}
