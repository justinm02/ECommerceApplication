package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.Model.persistence.User;
import com.example.demo.Model.persistence.repositories.CartRepository;
import com.example.demo.Model.persistence.repositories.UserRepository;
import com.example.demo.Model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepo);
        TestUtils.injectObject(userController, "cartRepository", cartRepo);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void createUserHappyPath() throws Exception {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");

        return user;
    }

    @Test
    public void validateFindByUsername() {
        when(userRepo.findByUsername("test")).thenReturn(createUser());

        final ResponseEntity<User> response = userController.findByUsername("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User actualUser = response.getBody();
        assertNotNull(actualUser);
        assertEquals("test", actualUser.getUsername());
    }

    @Test
    public void validateFindById() {
        User user = createUser();
        Optional<User> optUser = Optional.of(user);
        when(userRepo.findById(1L)).thenReturn(optUser);

        final ResponseEntity<User> response = userController.findById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User actualUser = response.getBody();
        assertNotNull(actualUser);
        assertEquals(user.getUsername(), actualUser.getUsername());
    }
}
