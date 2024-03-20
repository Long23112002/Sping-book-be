package com.example.web_bookstore_be.dao;

import com.example.web_bookstore_be.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "roles")
public interface RoleRepository extends JpaRepository<Role, Integer> {
    public Role findByNameRole(String nameRole);
}
