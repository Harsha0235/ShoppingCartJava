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

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private CartService cartService;

    @GetMapping("/items")
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @PostMapping("/cart")
    public ResponseEntity<CartItem> addToCart(@RequestBody Map<String, Object> request) {
        Long itemId = Long.parseLong(request.get("itemId").toString());
        int quantity = Integer.parseInt(request.get("quantity").toString());
        return new ResponseEntity<>(cartService.addToCart(itemId, quantity), HttpStatus.CREATED);
    }

    @PutMapping("/cart/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable Long cartItemId, @RequestBody Map<String, Object> request) {
        int newQuantity = Integer.parseInt(request.get("quantity").toString());
        return ResponseEntity.ok(cartService.updateCartItem(cartItemId, newQuantity));
    }

    @DeleteMapping("/cart/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long cartItemId) {
        cartService.removeFromCart(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cart")
    public ResponseEntity<Map<String, Object>> getCart() {
        List<CartItem> cartItems = cartService.getAllCartItems();
        double total = cartService.getCartTotal();
        return ResponseEntity.ok(Map.of("items", cartItems, "total", total));
    }
}