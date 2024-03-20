package com.example.web_bookstore_be.service.email;

public interface EmailService {
    public void sendMessage(String from, String to, String subject, String message);
}
