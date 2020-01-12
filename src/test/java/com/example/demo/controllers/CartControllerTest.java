package com.example.demo.controllers;

import com.example.demo.Model.persistence.Cart;
import com.example.demo.Model.persistence.Item;
import com.example.demo.Model.persistence.User;
import com.example.demo.Model.persistence.repositories.CartRepository;
import com.example.demo.Model.persistence.repositories.ItemRepository;
import com.example.demo.Model.persistence.repositories.UserRepository;
import com.example.demo.Model.requests.ModifyCartRequest;
import com.example.demo.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;
    private ItemRepository itemRepo = mock(ItemRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "itemRepository", itemRepo);
        TestUtils.injectObject(cartController, "cartRepository", cartRepo);
        TestUtils.injectObject(cartController, "userRepository", userRepo);
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        //cart.setTotal(new BigDecimal(9));

        List<Item> items = new ArrayList<Item>();
        for (Long i = 0L; i < 3L; i++) {
            Item item = new Item();
            item.setId(i);
            item.setDescription("test");
            item.setName("test");
            item.setPrice(new BigDecimal(2.99));
            cart.addItem(item);
        }
        cart.setItems(items);
        user.setCart(cart);

        return user;
    }

    private Item createItem() {
        Item item = new Item();
        item.setId(1L);
        item.setDescription("test");
        item.setName("test");
        item.setPrice(new BigDecimal(2.99));

        return item;
    }

    private ModifyCartRequest createModifyCartRequest() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(3);
        modifyCartRequest.setUsername("test");

        return modifyCartRequest;
    }

    @Test
    public void validateAddToCart() {
        when(userRepo.findByUsername("test")).thenReturn(createUser());
        when(itemRepo.findById(1L)).thenReturn(Optional.of(createItem()));

        ResponseEntity<Cart> response = cartController.addToCart(createModifyCartRequest());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart actualCart = response.getBody();
        assertNotNull(actualCart);
        assertEquals("test", actualCart.getUser().getUsername());
    }

    @Test
    public void validateRemoveFromCart() {
        when(userRepo.findByUsername("test")).thenReturn(createUser());
        when(itemRepo.findById(1L)).thenReturn(Optional.of(createItem()));

        ResponseEntity<Cart> response = cartController.removeFromCart(createModifyCartRequest());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart actualCart = response.getBody();
        assertNotNull(actualCart);
        assertEquals("test", actualCart.getUser().getUsername());
    }
}
