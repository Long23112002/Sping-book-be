package com.example.web_bookstore_be.dao;

import com.example.web_bookstore_be.entity.Delivery;
import com.example.web_bookstore_be.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(path = "deliveries")
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
}
