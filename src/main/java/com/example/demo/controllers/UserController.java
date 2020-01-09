package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.persistence.Cart;
import com.example.demo.Model.persistence.User;
import com.example.demo.Model.persistence.repositories.CartRepository;
import com.example.demo.Model.persistence.repositories.UserRepository;
import com.example.demo.Model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	public UserRepository userRepository;
	
	@Autowired
	public CartRepository cartRepository;

	@Autowired
	public BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUsername(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		log.info("Username used to find user is ", username);

		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		log.info("Username set with {}", createUserRequest.getUsername());

		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);

		boolean passwordLengthSatisfied = createUserRequest.getPassword().length() >= 7;
		boolean passwordEqualsConfirm = createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword());
		if (!passwordLengthSatisfied || !passwordEqualsConfirm) {
			if (!passwordLengthSatisfied && !passwordEqualsConfirm) {
				log.error("CreateUser failure: Password does not equal confirmPassword. Password length less than required length of 7 characters. Cannot create user {}. ", createUserRequest.getUsername());
			}
			else if (!passwordEqualsConfirm) {
				log.error("CreateUser failure: Password does not equal confirmPassword. Cannot create user {}. ", createUserRequest.getUsername());
			}
			else {
				log.error("CreateUser failure: Password length less than required length of 7 characters. Cannot create user {}. ", createUserRequest.getUsername());
			}
			return ResponseEntity.badRequest().build();
		}

		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		userRepository.save(user);
		log.info("CreateUser success: User {} created with requested username, cart, and encoded password", createUserRequest.getUsername());
		return ResponseEntity.ok(user);
	}
	
}
