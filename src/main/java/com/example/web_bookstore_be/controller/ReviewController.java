package com.example.web_bookstore_be.controller;

import com.example.web_bookstore_be.dao.*;
import com.example.web_bookstore_be.entity.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RequestMapping("/review")
@RestController
public class ReviewController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    private final ObjectMapper objectMapper;

    public ReviewController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostMapping("/add-review")
    public ResponseEntity<?> save(@RequestBody JsonNode jsonNode) {
        try{
            int idUser = Integer.parseInt(formatStringByJson(String.valueOf(jsonNode.get("idUser"))));
            int idOrder = Integer.parseInt(formatStringByJson(String.valueOf(jsonNode.get("idOrder"))));
            int idBook = Integer.parseInt(formatStringByJson(String.valueOf(jsonNode.get("idBook"))));
            float ratingValue = Float.parseFloat(formatStringByJson(String.valueOf(jsonNode.get("ratingPoint"))));
            String content = formatStringByJson(String.valueOf(jsonNode.get("content")));

            User user = userRepository.findById(idUser).get();
            Order order = orderRepository.findById(idOrder).get();
            List<OrderDetail> orderDetailList = orderDetailRepository.findOrderDetailsByOrder(order);
            Book book = bookRepository.findById(idBook).get();

            for (OrderDetail orderDetail : orderDetailList) {
                if (orderDetail.getBook().getIdBook() == idBook) {
                    orderDetail.setReview(true);
                    Review review = new Review();
                    review.setBook(book);
                    review.setUsers(user);
                    review.setContent(content);
                    review.setRatingPoint(ratingValue);
                    review.setOrderDetail(orderDetail);
                    // Lấy thời gian hiện tại
                    Instant instant = Instant.now();
                    // Chuyển đổi thành timestamp
                    Timestamp timestamp = Timestamp.from(instant);
                    review.setTimestamp(timestamp);
                    orderDetailRepository.save(orderDetail);
                    reviewRepository.save(review);
                    break;
                }
            }

            // Set lại rating trung bình của quyển sách đó
            List<Review> reviewList = reviewRepository.findAll();
            double sum = 0; // Tổng rating
            int n = 0; // Số lượng rating
            for (Review review : reviewList) {
                if (review.getBook().getIdBook() == idBook) {
                    n++;
                    sum += review.getRatingPoint();
                }
            }
            double ratingAvg = sum / n;
            book.setAvgRating(ratingAvg);
            bookRepository.save(book);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-review")
    public ResponseEntity<?> updateReview(@RequestBody JsonNode jsonNode) {
        try{
            Review reviewRequest = objectMapper.treeToValue(jsonNode, Review.class);
            Review review = reviewRepository.findById(reviewRequest.getIdReview()).get();
            review.setContent(reviewRequest.getContent());
            review.setRatingPoint(reviewRequest.getRatingPoint());

            reviewRepository.save(review);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }


    @PostMapping("/get-review")
    public ResponseEntity<?> getReview(@RequestBody JsonNode jsonNode) {
        try{
            int idOrder = Integer.parseInt(formatStringByJson(String.valueOf(jsonNode.get("idOrder"))));
            int idBook = Integer.parseInt(formatStringByJson(String.valueOf(jsonNode.get("idBook"))));

            Order order = orderRepository.findById(idOrder).get();
            Book book = bookRepository.findById(idBook).get();
            List<OrderDetail> orderDetailList = orderDetailRepository.findOrderDetailsByOrder(order);
            for (OrderDetail orderDetail : orderDetailList) {
                if (orderDetail.getBook().getIdBook() == book.getIdBook()) {
                    Review review = reviewRepository.findReviewByOrderDetail(orderDetail);
                    Review reviewResponse = new Review(); // Trả review luôn bị lỗi không được, nên phải dùng cách này
                    reviewResponse.setIdReview(review.getIdReview());
                    reviewResponse.setContent(review.getContent());
                    reviewResponse.setTimestamp(review.getTimestamp());
                    reviewResponse.setRatingPoint(review.getRatingPoint());
                    return ResponseEntity.status(HttpStatus.OK).body(reviewResponse);
                }
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }

    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}
