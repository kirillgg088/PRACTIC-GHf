package com.wildberries.unittests;

import com.wildberries.demoapp.model.Cart;
import com.wildberries.demoapp.model.Product;
import com.wildberries.demoapp.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты сервиса корзины")
class CartServiceTest {
    
    private Cart cart;
    private CartService cartService;
    
    @BeforeEach
    void setUp() {
        cart = new Cart();
        cartService = new CartService();
    }
    
    @Test
    @DisplayName("Скидка должна применяться при сумме корзины > 5000")
    void testCalculateDiscount_WhenCartTotalOver5000_ShouldReturnDiscount() {
        // Arrange
        cart.addProduct(new Product("Ноутбук", 45000));
        cart.addProduct(new Product("Мышь", 800));
        
        // Act
        double discount = cartService.calculateDiscount(cart);
        double expectedDiscount = 45800 * CartService.DISCOUNT_RATE;
        
        // Assert
        assertEquals(expectedDiscount, discount, 0.01, 
            "Скидка должна быть 10% от суммы 45800");
    }
    
    @Test
    @DisplayName("Скидка не должна применяться при сумме корзины < 5000")
    void testCalculateDiscount_WhenCartTotalUnder5000_ShouldReturnZero() {
        // Arrange
        cart.addProduct(new Product("Футболка", 1500));
        cart.addProduct(new Product("Шорты", 2000));
        
        // Act
        double discount = cartService.calculateDiscount(cart);
        
        // Assert
        assertEquals(0.0, discount, 0.01,
            "Скидка не должна применяться для суммы 3500");
    }
    
    @Test
    @DisplayName("Финальная цена должна учитывать скидку")
    void testCalculateFinalPrice_WithDiscount() {
        // Arrange
        cart.addProduct(new Product("Телевизор", 60000));
        
        // Act
        double finalPrice = cartService.calculateFinalPrice(cart);
        double expectedPrice = 60000 - (60000 * 0.1);
        
        // Assert
        assertEquals(expectedPrice, finalPrice, 0.01);
    }
    
    @Test
    @DisplayName("Бесплатная доставка должна быть доступна при сумме > 3000")
    void testIsEligibleForFreeShipping_WhenOverThreshold() {
        // Arrange
        cart.addProduct(new Product("Планшет", 35000));
        
        // Act & Assert
        assertTrue(cartService.isEligibleForFreeShipping(cart),
            "Бесплатная доставка должна быть доступна");
    }
    
    @Test
    @DisplayName("Бесплатная доставка не должна быть доступна при сумме < 3000")
    void testIsEligibleForFreeShipping_WhenUnderThreshold() {
        // Arrange
        cart.addProduct(new Product("Книга", 500));
        
        // Act & Assert
        assertFalse(cartService.isEligibleForFreeShipping(cart),
            "Бесплатная доставка не должна быть доступна");
    }
}
