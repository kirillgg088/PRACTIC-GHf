package com.wildberries.demoapp.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<Product> items;
    
    public Cart() {
        this.items = new ArrayList<>();
    }
    
    public void addProduct(Product product) {
        items.add(product);
    }
    
    public void removeProduct(Product product) {
        items.remove(product);
    }
    
    public List<Product> getItems() {
        return new ArrayList<>(items);
    }
    
    public double getTotalPrice() {
        return items.stream()
                .mapToDouble(Product::getPrice)
                .sum();
    }
    
    public int getItemCount() {
        return items.size();
    }
    
    public void clear() {
        items.clear();
    }
    
    @Override
    public String toString() {
        return String.format("Cart{items=%d, total=%.2f}", items.size(), getTotalPrice());
    }
}
