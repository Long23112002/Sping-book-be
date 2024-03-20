package com.example.web_bookstore_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_book")
    private int idBook; // Mã sách
    @Column(name = "name_book" , columnDefinition = "NVARCHAR(255)")
    private String nameBook; // Tên sách
    @Column(name = "author")
    private String author; // Tên tác giả
    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description; // Mô tả
    @Column(name = "list_price")
    private double listPrice; // Giá niêm yết
    @Column(name = "sell_price")
    private double sellPrice; // Giá bán
    @Column(name = "quantity")
    private int quantity; // Số lượng
    @Column(name = "avg_rating")
    private double avgRating; // Trung bình xếp hạng
    @Column(name = "sold_quantity")
    private int soldQuantity; // Đã bán bao nhiêu
    @Column(name = "discount_percent")
    private int discountPercent; // Giảm giá bao nhiêu %

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "book_genre", joinColumns = @JoinColumn(name = "id_book"), inverseJoinColumns = @JoinColumn(name = "id_genre"))
    private List<Genre> listGenres; // Danh sách thể loại

    @OneToMany(mappedBy = "book",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Image> listImages; // Danh sách ảnh

    @OneToMany(mappedBy = "book",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> listReviews; // Danh sách đánh giá

    @OneToMany(mappedBy = "book",fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<OrderDetail> listOrderDetails; // Danh sách chi tiết đơn hàng

    @OneToMany(mappedBy = "book",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FavoriteBook> listFavoriteBooks; // Danh sách sách yêu thích

    @OneToMany(mappedBy = "book",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CartItem> listCartItems;

}
