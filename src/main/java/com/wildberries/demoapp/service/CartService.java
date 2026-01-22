package com.wildberries.demoapp.service;

import com.wildberries.demoapp.model.Cart;

public class CartService {
    public static final double DISCOUNT_RATE = 0.1; // 10% скидка
    public static final double DISCOUNT_THRESHOLD = 5000.0;
    
    public double calculateDiscount(Cart cart) {
        double total = cart.getTotalPrice();
        if (total > DISCOUNT_THRESHOLD) {
            return total * DISCOUNT_RATE;
        }
        return 0.0;
    }
    
    public double calculateFinalPrice(Cart cart) {
        return cart.getTotalPrice() - calculateDiscount(cart);
    }
    
    public boolean isEligibleForFreeShipping(Cart cart) {
        return cart.getTotalPrice() > 3000.0;
    }
}
