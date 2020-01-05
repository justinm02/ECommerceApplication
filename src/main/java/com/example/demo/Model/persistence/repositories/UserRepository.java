package com.example.demo.Model.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.persistence.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
}
