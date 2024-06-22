package com.example.shoppingcartservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shoppingcartservice.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}