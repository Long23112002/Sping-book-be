package com.example.web_bookstore_be.service.cart;

import com.example.web_bookstore_be.dao.CartItemRepository;
import com.example.web_bookstore_be.dao.UserRepository;
import com.example.web_bookstore_be.entity.CartItem;
import com.example.web_bookstore_be.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImp implements CartService {
    private final ObjectMapper objectMapper;
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public CartItemRepository cartItemRepository;
    public CartServiceImp(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseEntity<?> save(JsonNode jsonData) {
        try{
            int idUser = 0;
            // Danh sách item của data vừa truyền
            List<CartItem> cartItemDataList = new ArrayList<>();
            for (JsonNode jsonDatum : jsonData) {
                CartItem cartItemData = objectMapper.treeToValue(jsonDatum, CartItem.class);
                idUser = Integer.parseInt(formatStringByJson(String.valueOf(jsonDatum.get("idUser"))));
                cartItemDataList.add(cartItemData);
            }
            Optional<User> user = userRepository.findById(idUser);
            // Danh sách item của user
            List<CartItem> cartItemList = user.get().getListCartItems();

            // Lặp qua từng item và xử lý
            for (CartItem cartItemData : cartItemDataList) {
                boolean isHad = false;
                for (CartItem cartItem : cartItemList) {
                    // Nếu trong cart của user có item đó rồi thì sẽ update lại quantity
                    if (cartItem.getBook().getIdBook() == cartItemData.getBook().getIdBook()) {
                        cartItem.setQuantity(cartItem.getQuantity() + cartItemData.getQuantity());
                        isHad = true;
                        break;
                    }
                }
                // Nếu chưa có thì thêm mới item đó
                if (!isHad) {
                    CartItem cartItem = new CartItem();
                    cartItem.setUsers(user.get());
                    cartItem.setQuantity(cartItemData.getQuantity());
                    cartItem.setBook(cartItemData.getBook());
                    cartItemList.add(cartItem);
                }
            }
            user.get().setListCartItems(cartItemList);
            User newUser = userRepository.save(user.get());


            if (cartItemDataList.size() == 1) {
                List<CartItem> cartItemListTemp = newUser.getListCartItems();
                return ResponseEntity.ok(cartItemListTemp.get(cartItemList.size() - 1).getIdCart());
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<?> update(JsonNode jsonData) {
        try{
            int idCart = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("idCart"))));
            int quantity = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("quantity"))));
            Optional<CartItem> cartItem = cartItemRepository.findById(idCart);
            cartItem.get().setQuantity(quantity);
            cartItemRepository.save(cartItem.get());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}
