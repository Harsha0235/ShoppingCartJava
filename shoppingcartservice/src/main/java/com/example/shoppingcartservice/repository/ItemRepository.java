package com.example.shoppingcartservice.repository;

import java.util.List;

import com.example.shoppingcartservice.model.Item;

public interface ItemRepository {
    Item findByName(String name);
    List<Item> findAll();
    Item save(Item item);
}