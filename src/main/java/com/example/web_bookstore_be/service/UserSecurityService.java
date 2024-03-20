package com.example.web_bookstore_be.service;

import com.example.web_bookstore_be.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserSecurityService extends UserDetailsService {
    public User findByUsername(String username);
}
