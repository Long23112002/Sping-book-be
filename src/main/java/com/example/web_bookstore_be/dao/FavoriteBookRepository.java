package com.example.web_bookstore_be.dao;

import com.example.web_bookstore_be.entity.Book;
import com.example.web_bookstore_be.entity.FavoriteBook;
import com.example.web_bookstore_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "favorite-books")
public interface FavoriteBookRepository extends JpaRepository<FavoriteBook, Integer> {
    public FavoriteBook findFavoriteBookByBookAndUsers(Book book, User user);
    public List<FavoriteBook> findFavoriteBooksByUsers(User user);
}
