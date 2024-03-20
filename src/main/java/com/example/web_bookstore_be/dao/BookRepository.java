package com.example.web_bookstore_be.dao;

import com.example.web_bookstore_be.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

@RepositoryRestResource(path = "books")
public interface BookRepository extends JpaRepository<Book, Integer> {
    Page<Book> findByNameBookContaining(@RequestParam("nameBook") String nameBook, Pageable pageable);
    Page<Book> findByListGenres_idGenre(@RequestParam("idGenre") int idGenre, Pageable pageable);
    Page<Book> findByNameBookContainingAndListGenres_idGenre(@RequestParam("nameBook") String nameBook ,@RequestParam("idGenre") int idGenre, Pageable pageable);
    long count();
}