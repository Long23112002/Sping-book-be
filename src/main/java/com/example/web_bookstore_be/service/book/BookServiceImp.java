package com.example.web_bookstore_be.service.book;

import com.example.web_bookstore_be.dao.BookRepository;
import com.example.web_bookstore_be.dao.GenreRepository;
import com.example.web_bookstore_be.dao.ImageRepository;
import com.example.web_bookstore_be.entity.Book;
import com.example.web_bookstore_be.entity.Genre;
import com.example.web_bookstore_be.entity.Image;
import com.example.web_bookstore_be.service.UploadImage.UploadImageService;
import com.example.web_bookstore_be.service.util.Base64ToMultipartFileConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImp implements BookService {

    private final ObjectMapper objectMapper;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UploadImageService uploadImageService;

    public BookServiceImp(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public ResponseEntity<?> save(JsonNode bookJson) {
        try {
            Book book = objectMapper.treeToValue(bookJson, Book.class);

            // Lưu thể loại của sách
            List<Integer> idGenreList = objectMapper.readValue(bookJson.get("idGenres").traverse(), new TypeReference<List<Integer>>() {
            });
            List<Genre> genreList = new ArrayList<>();
            for (int idGenre : idGenreList) {
                Optional<Genre> genre = genreRepository.findById(idGenre);
                genreList.add(genre.get());
            }
            book.setListGenres(genreList);

            // Lưu trước để lấy id sách đặt tên cho ảnh
            Book newBook = bookRepository.save(book);

            // Lưu thumbnail cho ảnh
            String dataThumbnail = formatStringByJson(String.valueOf((bookJson.get("thumbnail"))));

            Image thumbnail = new Image();
            thumbnail.setBook(newBook);
//            thumbnail.setDataImage(dataThumbnail);
            thumbnail.setThumbnail(true);
            MultipartFile multipartFile = Base64ToMultipartFileConverter.convert(dataThumbnail);
            String thumbnailUrl = uploadImageService.uploadImage(multipartFile, "Book_" + newBook.getIdBook());
            thumbnail.setUrlImage(thumbnailUrl);

            List<Image> imagesList = new ArrayList<>();
            imagesList.add(thumbnail);


            // Lưu những ảnh có liên quan
            String dataRelatedImg = formatStringByJson(String.valueOf((bookJson.get("relatedImg"))));
            List<String> arrDataRelatedImg = objectMapper.readValue(bookJson.get("relatedImg").traverse(), new TypeReference<List<String>>() {
            });

            for (int i = 0; i < arrDataRelatedImg.size(); i++) {
                String img = arrDataRelatedImg.get(i);
                Image image = new Image();
                image.setBook(newBook);
//                image.setDataImage(img);
                image.setThumbnail(false);
                MultipartFile relatedImgFile = Base64ToMultipartFileConverter.convert(img);
                String imgURL = uploadImageService.uploadImage(relatedImgFile, "Book_" + newBook.getIdBook() + "." + i);
                image.setUrlImage(imgURL);
                imagesList.add(image);
            }

            newBook.setListImages(imagesList);
            // Cập nhật lại ảnh
            bookRepository.save(newBook);

            return ResponseEntity.ok("Success!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> update(JsonNode bookJson) {
        try {
            Book book = objectMapper.treeToValue(bookJson, Book.class);
            List<Image> imagesList = imageRepository.findImagesByBook(book);

            // Lưu thể loại của sách
            List<Integer> idGenreList = objectMapper.readValue(bookJson.get("idGenres").traverse(), new TypeReference<List<Integer>>() {
            });
            List<Genre> genreList = new ArrayList<>();
            for (int idGenre : idGenreList) {
                Optional<Genre> genre = genreRepository.findById(idGenre);
                genreList.add(genre.get());
            }
            book.setListGenres(genreList);

            // Kiểm tra xem thumbnail có thay đổi không
            String dataThumbnail = formatStringByJson(String.valueOf((bookJson.get("thumbnail"))));
            if (Base64ToMultipartFileConverter.isBase64(dataThumbnail)) {
                for (Image image : imagesList) {
                    if (image.isThumbnail()) {
//                        image.setDataImage(dataThumbnail);
                        MultipartFile multipartFile = Base64ToMultipartFileConverter.convert(dataThumbnail);
                        String thumbnailUrl = uploadImageService.uploadImage(multipartFile, "Book_" + book.getIdBook());
                        image.setUrlImage(thumbnailUrl);
                        imageRepository.save(image);
                        break;
                    }
                }
            }

            Book newBook = bookRepository.save(book);

            // Kiểm tra ảnh có liên quan
            List<String> arrDataRelatedImg = objectMapper.readValue(bookJson.get("relatedImg").traverse(), new TypeReference<List<String>>() {});

            // Xem có xoá tất ở bên FE không
            boolean isCheckDelete = true;

            for (String img : arrDataRelatedImg) {
                if (!Base64ToMultipartFileConverter.isBase64(img)) {
                    isCheckDelete = false;
                }
            }

            // Nếu xoá hết tất cả
            if (isCheckDelete) {
                imageRepository.deleteImagesWithFalseThumbnailByBookId(newBook.getIdBook());
                Image thumbnailTemp = imagesList.get(0);
                imagesList.clear();
                imagesList.add(thumbnailTemp);
                for (int i = 0; i < arrDataRelatedImg.size(); i++) {
                    String img = arrDataRelatedImg.get(i);
                    Image image = new Image();
                    image.setBook(newBook);
//                    image.setDataImage(img);
                    image.setThumbnail(false);
                    MultipartFile relatedImgFile = Base64ToMultipartFileConverter.convert(img);
                    String imgURL = uploadImageService.uploadImage(relatedImgFile, "Book_" + newBook.getIdBook() + "." + i);
                    image.setUrlImage(imgURL);
                    imagesList.add(image);
                }
            } else {
                // Nếu không xoá hết tất cả (Giữ nguyên ảnh hoặc thêm ảnh vào)
                for (int i = 0; i < arrDataRelatedImg.size(); i++) {
                    String img = arrDataRelatedImg.get(i);
                    if (Base64ToMultipartFileConverter.isBase64(img)) {
                        Image image = new Image();
                        image.setBook(newBook);
//                        image.setDataImage(img);
                        image.setThumbnail(false);
                        MultipartFile relatedImgFile = Base64ToMultipartFileConverter.convert(img);
                        String imgURL = uploadImageService.uploadImage(relatedImgFile, "Book_" + newBook.getIdBook() + "." + i);
                        image.setUrlImage(imgURL);
                        imageRepository.save(image);
                    }
                }
            }

            newBook.setListImages(imagesList);
            // Cập nhật lại ảnh
            bookRepository.save(newBook);

            return ResponseEntity.ok("Success!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public long getTotalBook() {
        return bookRepository.count();
    }

    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }

}

