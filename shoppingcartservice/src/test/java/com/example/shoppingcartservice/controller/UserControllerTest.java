package com.example.shoppingcartservice.controller;

import com.example.shoppingcartservice.model.CartItem;
import com.example.shoppingcartservice.model.Item;
import com.example.shoppingcartservice.service.CartService;
import com.example.shoppingcartservice.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllInStockItems() throws Exception {
        when(itemService.getAllItems()).thenReturn(Arrays.asList(
                new Item(1L, "Item 1", 10.0, 5),
                new Item(2L, "Item 2", 20.0, 0),
                new Item(3L, "Item 3", 30.0, 10)
        ));

        mockMvc.perform(get("/user/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[1].name").value("Item 3"))
                .andExpect(jsonPath("$[0].inStock").exists())
                .andExpect(jsonPath("$[1].inStock").exists());
    }

    @Test
    public void testAddToCart() throws Exception {
        Item item = new Item(1L, "Item 1", 10.0, 5);
        CartItem cartItem = new CartItem(item, 2);
        when(cartService.addToCart(eq(1L), anyInt())).thenReturn(cartItem);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", 1L);
        requestBody.put("quantity", 2);

        mockMvc.perform(post("/user/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.item.name").value("Item 1"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.item.inStock").doesNotExist());
    }

    @Test
    public void testUpdateCartItem() throws Exception {
        Item item = new Item(1L, "Item 1", 10.0, 5);
        CartItem cartItem = new CartItem(item, 3);
        when(cartService.updateCartItem(eq(1L), anyInt())).thenReturn(cartItem);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("quantity", 3);

        mockMvc.perform(put("/user/cart/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.name").value("Item 1"))
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.item.inStock").doesNotExist());
    }

    @Test
    public void testRemoveFromCart() throws Exception {
        mockMvc.perform(delete("/user/cart/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetCart() throws Exception {
        Item item1 = new Item(1L, "Item 1", 10.0, 5);
        Item item2 = new Item(2L, "Item 2", 20.0, 10);
        CartItem cartItem1 = new CartItem(item1, 2);
        CartItem cartItem2 = new CartItem(item2, 1);

        when(cartService.getAllCartItems()).thenReturn(Arrays.asList(cartItem1, cartItem2));
        when(cartService.getCartTotal()).thenReturn(40.0);

        mockMvc.perform(get("/user/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].item.name").value("Item 1"))
                .andExpect(jsonPath("$.items[1].item.name").value("Item 2"))
                .andExpect(jsonPath("$.items[0].item.inStock").doesNotExist())
                .andExpect(jsonPath("$.items[1].item.inStock").doesNotExist())
                .andExpect(jsonPath("$.total").value(40.0));
    }
}