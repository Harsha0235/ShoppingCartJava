package com.example.shoppingcartservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shoppingcartservice.model.CartItem;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByItemId(Long itemId);
}