package com.example.shoppingcartservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.shoppingcartservice.model.Item;
import com.example.shoppingcartservice.repository.ItemRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    public List<Item> getAllItems() {
        return itemRepository.findAll().stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .collect(Collectors.toList());
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + id));
    }

    public Item addItem(Item item) {
        if (itemRepository.existsById(item.getId())) {
            throw new RuntimeException("An item with ID " + item.getId() + " already exists.");
        }
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
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("Item not found with ID: " + id);
        }
        itemRepository.deleteById(id);
        reorderItems();
    }

    private void reorderItems() {
        List<Item> items = getAllItems();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (!item.getId().equals((long) (i + 1))) {
                item.setId((long) (i + 1));
                itemRepository.save(item);
            }
        }
    }

    public void decreaseStock(Long id, int quantity) {
        Item item = getItemById(id);
        if (item.getInStock() < quantity) {
            throw new RuntimeException("Not enough items in stock. Available: " + item.getInStock());
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