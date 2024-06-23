package com.example.shoppingcartservice.controller;

import com.example.shoppingcartservice.model.Item;
import com.example.shoppingcartservice.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllItems() throws Exception {
        when(itemService.getAllItems()).thenReturn(Arrays.asList(
                new Item(1L, "Item 1", 10.0, 5),
                new Item(2L, "Item 2", 20.0, 10)
        ));

        mockMvc.perform(get("/admin/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[1].name").value("Item 2"));
    }

    @Test
    public void testGetItemById() throws Exception {
        when(itemService.getItemById(1L)).thenReturn(new Item(1L, "Item 1", 10.0, 5));

        mockMvc.perform(get("/admin/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item 1"));
    }

    @Test
    public void testAddItem() throws Exception {
        Item newItem = new Item(1L, "New Item", 15.0, 7);
        when(itemService.addItem(any(Item.class))).thenReturn(newItem);

        mockMvc.perform(post("/admin/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Item"));
    }

    @Test
    public void testUpdateItem() throws Exception {
        Item updatedItem = new Item(1L, "Updated Item", 25.0, 15);
        when(itemService.updateItem(eq(1L), any(Item.class))).thenReturn(updatedItem);

        mockMvc.perform(put("/admin/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"));
    }

    @Test
    public void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/admin/items/1"))
                .andExpect(status().isNoContent());
    }
}