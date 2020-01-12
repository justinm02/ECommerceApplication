package com.example.demo.controllers;

import com.example.demo.Model.persistence.Item;
import com.example.demo.Model.persistence.repositories.ItemRepository;
import com.example.demo.TestUtils;
import com.example.demo.Model.persistence.User;
import com.example.demo.Model.persistence.repositories.CartRepository;
import com.example.demo.Model.persistence.repositories.UserRepository;
import com.example.demo.Model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepo);
    }

    private Item createItem() {
        Item item = new Item();

        item.setId(1L);
        item.setName("test");
        item.setDescription("test");
        item.setPrice(new BigDecimal(2.99));

        return item;
    }

    @Test
    public void validateGetItemById() {
        Item item = createItem();
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        final ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item actualItem = response.getBody();
        assertNotNull(actualItem);
        assertEquals("test", actualItem.getName());
    }

    @Test
    public void validateGetItemsByName() {
        Item item = createItem();
        List<Item> items = new ArrayList<Item>(Arrays.asList(item, item, item));
        when(itemRepo.findByName("test")).thenReturn(items);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> actualItems = response.getBody();
        assertNotNull(actualItems);
        assertEquals("test", actualItems.get(0).getName());
    }

    @Test
    public void validateGetItems() {
        Item item = createItem();
        List<Item> items = new ArrayList<>(Arrays.asList(item, item, item));
        when(itemRepo.findAll()).thenReturn(items);

        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> actualItems = response.getBody();
        assertNotNull(actualItems);
        assertEquals(item.getPrice(), actualItems.get(1).getPrice());
    }
}

