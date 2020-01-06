package com.example.demo.controllers;

import com.example.demo.Model.persistence.Cart;
import com.example.demo.Model.persistence.Item;
import com.example.demo.Model.persistence.User;
import com.example.demo.Model.persistence.UserOrder;
import com.example.demo.Model.persistence.repositories.OrderRepository;
import com.example.demo.Model.persistence.repositories.UserRepository;
import com.example.demo.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private OrderRepository orderRepo = mock(OrderRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "orderRepository", orderRepo);
        TestUtils.injectObject(orderController, "userRepository", userRepo);
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setTotal(new BigDecimal(8.97));

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

    @Test
    public void validateSubmitOrder() {
        when(userRepo.findByUsername("test")).thenReturn(createUser());

        final ResponseEntity<UserOrder> response = orderController.submit("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder actualOrder = response.getBody();
        assertNotNull(actualOrder);
        assertEquals("test", actualOrder.getUser().getUsername());
    }

    private List<UserOrder> createUserOrders() {
        List<UserOrder> userOrders = new ArrayList<UserOrder>();
        for (Long i = 0L; i < 3L; i++) {
            UserOrder order = new UserOrder();
            order.setId(i);
            order.setUser(createUser());

            List<Item> items = new ArrayList<Item>();
            for (Long j = 0L; j < 3L; j++) {
                Item item = new Item();
                item.setId(j);
                item.setDescription("test");
                item.setName("test");
                item.setPrice(new BigDecimal(2.99));
                items.add(item);
            }
            order.setItems(items);

            order.setTotal(new BigDecimal(8.97));

            userOrders.add(order);
        }
        return userOrders;
    }

    @Test
    public void validateGetOrdersForUser() {
        User user = createUser();
        when(userRepo.findByUsername("test")).thenReturn(user);
        when(orderRepo.findByUser(user)).thenReturn(createUserOrders());
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> actualOrders = response.getBody();
        assertNotNull(actualOrders);
        assertEquals(new BigDecimal(8.97), actualOrders.get(1).getTotal());
    }
}
