package com.example.web_bookstore_be.service.order;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    public ResponseEntity<?> save(JsonNode jsonData);
    public ResponseEntity<?> update(JsonNode jsonData);
    public ResponseEntity<?> cancel (JsonNode jsonData);
}
