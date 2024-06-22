package com.example.shoppingcartservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shoppingcartservice.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}