package com.example.web_bookstore_be.controller;

import com.example.web_bookstore_be.dao.BookRepository;
import com.example.web_bookstore_be.dao.FavoriteBookRepository;
import com.example.web_bookstore_be.dao.UserRepository;
import com.example.web_bookstore_be.entity.Book;
import com.example.web_bookstore_be.entity.FavoriteBook;
import com.example.web_bookstore_be.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/favorite-book")
public class FavoriteBookController {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FavoriteBookRepository favoriteBookRepository;

    @GetMapping("/get-favorite-book/{idUser}")
    public ResponseEntity<?> getAllFavoriteBookByIdUser(@PathVariable Integer idUser) {
        try{
            User user = userRepository.findById(idUser).get();
            List<FavoriteBook> favoriteBookList = favoriteBookRepository.findFavoriteBooksByUsers(user);
            List<Integer> idBookListOfFavoriteBook = new ArrayList<>();
            for (FavoriteBook favoriteBook : favoriteBookList) {
                idBookListOfFavoriteBook.add(favoriteBook.getBook().getIdBook());
            }
            return ResponseEntity.ok().body(idBookListOfFavoriteBook);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }
    @PostMapping("/add-book")
    public ResponseEntity<?> save(@RequestBody JsonNode jsonNode) {
        try{
            int idBook = Integer.parseInt(formatStringByJson(jsonNode.get("idBook").toString()));
            int idUser = Integer.parseInt(formatStringByJson(jsonNode.get("idUser").toString()));

            Book book = bookRepository.findById(idBook).get();
            User user = userRepository.findById(idUser).get();

            FavoriteBook favoriteBook = FavoriteBook.builder().book(book).users(user).build();

            favoriteBookRepository.save(favoriteBook);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-book")
    public ResponseEntity<?> remove(@RequestBody JsonNode jsonNode) {
        try{
            int idBook = Integer.parseInt(formatStringByJson(jsonNode.get("idBook").toString()));
            int idUser = Integer.parseInt(formatStringByJson(jsonNode.get("idUser").toString()));

            Book book = bookRepository.findById(idBook).get();
            User user = userRepository.findById(idUser).get();

            FavoriteBook favoriteBook = favoriteBookRepository.findFavoriteBookByBookAndUsers(book, user);

            favoriteBookRepository.delete(favoriteBook);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}
