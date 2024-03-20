package com.example.web_bookstore_be.controller;

import com.example.web_bookstore_be.service.book.BookService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookService bookService;

    @PostMapping(path = "/add-book")
    public ResponseEntity<?> save(@RequestBody JsonNode jsonData) {
        try {
            return bookService.save(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi r");
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/update-book")
    public ResponseEntity<?> update(@RequestBody JsonNode jsonData) {
        try{
            return bookService.update(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi r");
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/get-total")
    public long getTotal() {
        return bookService.getTotalBook();
    }
}
