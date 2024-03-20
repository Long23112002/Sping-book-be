package com.example.web_bookstore_be.dao;

import com.example.web_bookstore_be.entity.Order;
import com.example.web_bookstore_be.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(path = "payments")
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
