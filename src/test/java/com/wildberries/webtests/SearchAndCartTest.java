package com.wildberries.webtests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тест поиска и добавления в корзину")
class SearchAndCartTest extends BaseTest {
    
    @Test
    @DisplayName("Поиск товара и добавление в корзину")
    void testSearchProductAndAddToCart() throws InterruptedException {
        // 1. Найти поле поиска и ввести запрос
        WebElement searchInput = driver.findElement(By.cssSelector("#searchInput"));
        assertNotNull(searchInput, "Поле поиска должно быть найдено");
        
        searchInput.sendKeys("футболка мужская черная");
        searchInput.sendKeys(Keys.ENTER);
        
        // 2. Подождать загрузки результатов
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.titleContains("футболка"));
        
        // 3. Проверить, что есть результаты поиска
        WebElement resultsContainer = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".product-card-list")
            )
        );
        assertTrue(resultsContainer.isDisplayed(), "Результаты поиска должны отображаться");
        
        // 4. Получить количество найденных товаров
        java.util.List<WebElement> products = driver.findElements(
            By.cssSelector(".product-card__link")
        );
        assertFalse(products.isEmpty(), "Должен быть найден хотя бы один товар");
        
        System.out.println("Найдено товаров: " + products.size());
        
        // 5. Кликнуть на первый товар
        products.get(0).click();
        
        // 6. Подождать загрузки страницы товара
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".product-page")
        ));
        
        // 7. Проверить наличие кнопки добавления в корзину
        WebElement addToCartButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector(".btn-main, .product-page__order-btn")
            )
        );
        assertTrue(addToCartButton.isDisplayed(), 
            "Кнопка добавления в корзину должна отображаться");
        
        // 8. Добавить товар в корзину
        addToCartButton.click();
        
        // 9. Проверить уведомление или счетчик корзины
        Thread.sleep(2000); // Краткая задержка для обновления UI
        
        // Проверяем различные возможные селекторы для счетчика корзины
        try {
            WebElement cartCounter = driver.findElement(
                By.cssSelector(".navbar-pc__notify, .j-item-count, [data-count]")
            );
            if (cartCounter.isDisplayed()) {
                String countText = cartCounter.getText();
                assertTrue(countText.contains("1") || countText.equals("1"), 
                    "Счетчик корзины должен показывать 1 товар");
            }
        } catch (Exception e) {
            // Если счетчик не найден, проверяем появление уведомления
            WebElement notification = driver.findElement(
                By.cssSelector(".notifications, .alert, .toast")
            );
            assertTrue(notification.isDisplayed(), 
                "Должно появиться уведомление о добавлении в корзину");
        }
        
        System.out.println("✅ Тест завершен успешно: товар добавлен в корзину");
    }
}
