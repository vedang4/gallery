package com.example.demo;

public class PdfPCell {
    private String content;

    // Constructor for PdfPCell
    public PdfPCell(String content) {
        this.content = content;
    }

    // Getter for content
    public String getContent() {
        return content;
    }

    // Setter for content
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
