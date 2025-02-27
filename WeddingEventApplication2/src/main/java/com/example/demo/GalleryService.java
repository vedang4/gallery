package com.example.demo;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class GalleryService {

    private static final String GALLERY_PATH = "gallery/";

    public GalleryService() {
        // Ensure gallery directory exists
        new File(GALLERY_PATH).mkdirs();
    }

    public List<String> getAllPhotos() {
        File galleryDir = new File(GALLERY_PATH);
        File[] files = galleryDir.listFiles();
        if (files == null) return List.of();

        return List.of(files).stream()
                .map(File::getName)
                .collect(Collectors.toList());
    }

    public void uploadPhoto(MultipartFile photo) throws IOException {
        if (photo.isEmpty()) {
            throw new IOException("Photo is empty");
        }
        Path destination = Paths.get(GALLERY_PATH, photo.getOriginalFilename());
        Files.copy(photo.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
    }

    public void deletePhoto(String fileName) {
        Path filePath = Paths.get(GALLERY_PATH, fileName);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error deleting file: " + fileName, e);
        }
    }

    public void downloadPhoto(String fileName, HttpServletResponse response) throws IOException {
        File file = new File(GALLERY_PATH + fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + fileName);
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        Files.copy(file.toPath(), response.getOutputStream());
        response.getOutputStream().flush();
    }
}
