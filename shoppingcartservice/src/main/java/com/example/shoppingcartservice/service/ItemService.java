package com.example.shoppingcartservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.shoppingcartservice.model.Item;
import com.example.shoppingcartservice.repository.ItemRepository;
import java.util.List;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    public Item updateItem(Long id, Item item) {
        Item existingItem = getItemById(id);
        existingItem.setName(item.getName());
        existingItem.setPrice(item.getPrice());
        existingItem.setInStock(item.getInStock());
        return itemRepository.save(existingItem);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    public void decreaseStock(Long id, int quantity) {
        Item item = getItemById(id);
        if (item.getInStock() < quantity) {
            throw new RuntimeException("Not enough items in stock");
        }
        item.setInStock(item.getInStock() - quantity);
        itemRepository.save(item);
    }

    public void increaseStock(Long id, int quantity) {
        Item item = getItemById(id);
        item.setInStock(item.getInStock() + quantity);
        itemRepository.save(item);
    }
}