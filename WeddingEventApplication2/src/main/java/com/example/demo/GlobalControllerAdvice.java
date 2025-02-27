package com.example.demo;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void setHeaders(HttpServletResponse response) {
        response.setHeader("X-Frame-Options", "ALLOW-FROM *");
    }
}
