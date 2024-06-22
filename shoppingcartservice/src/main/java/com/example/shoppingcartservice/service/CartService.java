package com.example.shoppingcartservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.shoppingcartservice.model.CartItem;
import com.example.shoppingcartservice.model.Item;
import com.example.shoppingcartservice.repository.CartItemRepository;
import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ItemService itemService;

    public List<CartItem> getAllCartItems() {
        return cartItemRepository.findAll();
    }

    public CartItem addToCart(Long id, int quantity) {
        Item item = itemService.getItemById(id);
        itemService.decreaseStock(id, quantity);
        
        CartItem cartItem = cartItemRepository.findById(id)
                .orElse(new CartItem(item, 0));
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        
        return cartItemRepository.save(cartItem);
    }

    public CartItem updateCartItem(Long id, int newQuantity) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found in cart: " + id));
        int quantityDifference = newQuantity - cartItem.getQuantity();
        if (quantityDifference > 0) {
            itemService.decreaseStock(id, quantityDifference);
        } else if (quantityDifference < 0) {
            itemService.increaseStock(id, -quantityDifference);
        }
        cartItem.setQuantity(newQuantity);
        return cartItemRepository.save(cartItem);
    }

    public void removeFromCart(Long id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found in cart: " + id));
        itemService.increaseStock(id, cartItem.getQuantity());
        cartItemRepository.deleteById(id);
    }

    public double getCartTotal() {
        return getAllCartItems().stream()
                .mapToDouble(item -> item.getItem().getPrice() * item.getQuantity())
                .sum();
    }
}