package com.example.demo.Model.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.persistence.Cart;
import com.example.demo.Model.persistence.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Cart findByUser(User user);
}
