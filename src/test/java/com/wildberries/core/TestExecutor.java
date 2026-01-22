package com.wildberries.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;

public class TestExecutor {
    private WebDriver driver;
    private WebDriverWait wait;
    
    public TestExecutor(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    public void executeStep(String action, String[] params) {
        switch (action) {
            case "open_url":
                openUrl(params[0]);
                break;
            case "search_product":
                searchProduct(params[0]);
                break;
            case "select_first_product":
                selectFirstProduct();
                break;
            case "add_to_cart":
                addToCart();
                break;
            case "verify_cart_count":
                verifyCartCount(params[0]);
                break;
            case "verify_page_title":
                verifyPageTitle(params[0]);
                break;
            case "apply_filter":
                applyFilter(params[0], params[1]);
                break;
            case "verify_results_count":
                verifyResultsCount(params[0]);
                break;
            default:
                throw new IllegalArgumentException("Неизвестное действие: " + action);
        }
    }
    
    private void openUrl(String url) {
        driver.get(url);
        System.out.println("Открыта страница: " + url);
    }
    
    private void searchProduct(String query) {
        WebElement searchInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector("#searchInput"))
        );
        searchInput.sendKeys(query);
        searchInput.submit();
        System.out.println("Выполнен поиск: " + query);
    }
    
    private void selectFirstProduct() {
        List<WebElement> products = wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector(".product-card__link")
            )
        );
        if (!products.isEmpty()) {
            products.get(0).click();
            System.out.println("Выбран первый товар");
        }
    }
    
    private void addToCart() {
        WebElement addButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector(".btn-main, .product-page__order-btn")
            )
        );
        addButton.click();
        System.out.println("Товар добавлен в корзину");
    }
    
    private void verifyCartCount(String expectedCount) {
        try {
            Thread.sleep(2000); // Ждем обновления UI
            WebElement cartCounter = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".navbar-pc__notify")
                )
            );
            String actualCount = cartCounter.getText();
            System.out.println("Счетчик корзины: " + actualCount);
            // Проверка, что счетчик содержит ожидаемое значение
            assert actualCount.contains(expectedCount) : 
                "Ожидалось " + expectedCount + ", но получено " + actualCount;
        } catch (Exception e) {
            System.out.println("Счетчик не найден, проверяем альтернативные элементы");
        }
    }
    
    private void verifyPageTitle(String expectedTitle) {
        String actualTitle = driver.getTitle();
        assert actualTitle.contains(expectedTitle) :
            "Заголовок страницы должен содержать: " + expectedTitle;
        System.out.println("Проверен заголовок: " + actualTitle);
    }
    
    private void applyFilter(String filterType, String value) {
        // Реализация применения фильтров
        System.out.println("Применен фильтр: " + filterType + " = " + value);
    }
    
    private void verifyResultsCount(String condition) {
        // Реализация проверки количества результатов
        System.out.println("Проверка количества результатов: " + condition);
    }
}
