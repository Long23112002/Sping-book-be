package com.example.web_bookstore_be.dao;

import com.example.web_bookstore_be.entity.Feedbacks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "feedbacks")
public interface FeedBackRepository extends JpaRepository<Feedbacks, Integer> {
    long countBy();
}
