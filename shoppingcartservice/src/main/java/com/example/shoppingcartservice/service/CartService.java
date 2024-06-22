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

    public CartItem addToCart(Long itemId, int quantity) {
        Item item = itemService.getItemById(itemId);
        itemService.decreaseStock(itemId, quantity);
        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    public CartItem updateCartItem(Long cartItemId, int newQuantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        int quantityDifference = newQuantity - cartItem.getQuantity();
        if (quantityDifference > 0) {
            itemService.decreaseStock(cartItem.getItem().getId(), quantityDifference);
        } else if (quantityDifference < 0) {
            itemService.increaseStock(cartItem.getItem().getId(), -quantityDifference);
        }
        cartItem.setQuantity(newQuantity);
        return cartItemRepository.save(cartItem);
    }

    public void removeFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        itemService.increaseStock(cartItem.getItem().getId(), cartItem.getQuantity());
        cartItemRepository.deleteById(cartItemId);
    }

    public double getCartTotal() {
        List<CartItem> cartItems = getAllCartItems();
        return cartItems.stream()
                .mapToDouble(item -> item.getItem().getPrice() * item.getQuantity())
                .sum();
    }
}