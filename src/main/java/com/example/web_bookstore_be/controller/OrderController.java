package com.example.web_bookstore_be.controller;

import com.example.web_bookstore_be.service.order.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping("/add-order")
    public ResponseEntity<?> save (@RequestBody JsonNode jsonData) {
        try{
            return orderService.save(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update-order") // update các trạng thái
    public ResponseEntity<?> update (@RequestBody JsonNode jsonData) {
        try{
            return orderService.update(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/cancel-order") // khi thanh toán mà huỷ thanh toán
    public ResponseEntity<?> cancle (@RequestBody JsonNode jsonNode) {
        try{
            return orderService.cancel(jsonNode);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
