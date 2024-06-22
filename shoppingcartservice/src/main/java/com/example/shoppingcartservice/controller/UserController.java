package com.example.shoppingcartservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.shoppingcartservice.model.CartItem;
import com.example.shoppingcartservice.model.Item;
import com.example.shoppingcartservice.service.CartService;
import com.example.shoppingcartservice.service.ItemService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private CartService cartService;

    @GetMapping("/items")
    public ResponseEntity<List<Item>> getAllInStockItems() {
        List<Item> inStockItems = itemService.getAllItems().stream()
                .filter(item -> item.getInStock() > 0)
                .collect(Collectors.toList());
        return ResponseEntity.ok(inStockItems);
    }

    @PostMapping("/cart")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> request) {
        try {
            Long id = Long.parseLong(request.get("id").toString());
            int quantity = Integer.parseInt(request.get("quantity").toString());
            CartItem cartItem = cartService.addToCart(id, quantity);
            return new ResponseEntity<>(removeInStock(cartItem), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/cart/{id}")
    public ResponseEntity<?> updateCartItem(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            int newQuantity = Integer.parseInt(request.get("quantity").toString());
            CartItem cartItem = cartService.updateCartItem(id, newQuantity);
            return ResponseEntity.ok(removeInStock(cartItem));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/cart/{id}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long id) {
        try {
            cartService.removeFromCart(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/cart")
    public ResponseEntity<Map<String, Object>> getCart() {
        List<CartItem> cartItems = cartService.getAllCartItems();
        List<Map<String, Object>> cartItemsWithoutInStock = cartItems.stream()
                .map(this::removeInStock)
                .collect(Collectors.toList());
        double total = cartService.getCartTotal();
        return ResponseEntity.ok(Map.of("items", cartItemsWithoutInStock, "total", total));
    }

    private Map<String, Object> removeInStock(CartItem cartItem) {
        return Map.of(
            "id", cartItem.getId(),
            "item", Map.of(
                "id", cartItem.getItem().getId(),
                "name", cartItem.getItem().getName(),
                "price", cartItem.getItem().getPrice()
            ),
            "quantity", cartItem.getQuantity()
        );
    }
}