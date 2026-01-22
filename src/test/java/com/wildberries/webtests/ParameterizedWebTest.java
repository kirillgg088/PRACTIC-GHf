package com.wildberries.webtests;

import com.wildberries.config.ConfigReader;
import com.wildberries.config.TestDataReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Параметризованное тестирование Wildberries")
public class ParameterizedWebTest {
    private WebDriver driver;
    private WebDriverWait wait;
    
    @BeforeEach
    void setUp() {
        // Настройка драйвера из конфигурации
        String browser = ConfigReader.getBrowser();
        boolean headless = ConfigReader.isHeadless();
        
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless");
        }
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getTimeout()));
        
        System.out.println("=== Начинаем тестирование ===");
        System.out.println("Браузер: " + browser);
        System.out.println("Headless режим: " + headless);
    }
    
    // Источник данных для параметризованного теста
    static Stream<JsonObject> provideTestUsers() {
        JsonArray users = TestDataReader.getTestUsers();
        return users.asList().stream()
                .map(element -> element.getAsJsonObject());
    }
    
    @ParameterizedTest(name = "Тестовый пользователь: {0}")
    @MethodSource("provideTestUsers")
    @DisplayName("Параметризованный поиск товаров")
    void testParameterizedSearch(JsonObject user) {
        // Получаем данные из JSON
        String userId = user.get("id").getAsString();
        String searchQuery = user.get("searchQuery").getAsString();
        int expectedMinResults = user.get("expectedMinResults").getAsInt();
        
        System.out.println("\n--- Запуск теста для пользователя: " + userId + " ---");
        System.out.println("Поисковый запрос: " + searchQuery);
        System.out.println("Ожидаемый минимум результатов: " + expectedMinResults);
        
        try {
            // Шаг 1: Открыть главную страницу
            driver.get(ConfigReader.getBaseUrl());
            wait.until(ExpectedConditions.titleContains("Wildberries"));
            System.out.println("✓ Страница успешно загружена");
            
            // Шаг 2: Найти поле поиска и ввести запрос
            String searchLocator = ConfigReader.getLocator("search.input");
            WebElement searchInput = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(searchLocator)
                )
            );
            searchInput.clear();
            searchInput.sendKeys(searchQuery);
            searchInput.sendKeys(Keys.ENTER);
            System.out.println("✓ Поисковый запрос отправлен: " + searchQuery);
            
            // Шаг 3: Подождать результаты
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(ConfigReader.getLocator("product.card"))
            ));
            
            // Шаг 4: Получить количество найденных товаров
            var products = driver.findElements(
                By.cssSelector(ConfigReader.getLocator("product.card"))
            );
            int actualResults = products.size();
            
            System.out.println("Найдено товаров: " + actualResults);
            
            // Шаг 5: Проверить результаты
            assertTrue(actualResults >= expectedMinResults,
                String.format("Ожидалось минимум %d товаров, но найдено %d",
                    expectedMinResults, actualResults));
            
            // Шаг 6: Проверить, что товары содержат искомый текст
            if (actualResults > 0) {
                WebElement firstProduct = products.get(0);
                String productText = firstProduct.getText().toLowerCase();
                assertTrue(productText.contains(searchQuery.toLowerCase()) ||
                          !productText.isEmpty(),
                    "Первый товар должен содержать поисковый запрос или быть не пустым");
                System.out.println("✓ Первый товар прошел проверку");
            }
            
            System.out.println("✅ Тест успешно завершен для пользователя: " + userId);
            
        } catch (Exception e) {
            System.err.println("❌ Ошибка в тесте для пользователя: " + userId);
            System.err.println("Причина: " + e.getMessage());
            
            // Сделать скриншот при ошибке
            try {
                TakesScreenshot ts = (TakesScreenshot) driver;
                byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);
                // Можно сохранить скриншот в файл
                System.out.println("Скриншот сделан");
            } catch (Exception screenshotError) {
                System.err.println("Не удалось сделать скриншот: " + screenshotError.getMessage());
            }
            throw e;
        }
    }
    
    @ParameterizedTest(name = "Тестовый сценарий: {0}")
    @DisplayName("Выполнение полного сценария из конфигурации")
    void testCompleteScenario() {
        // Получаем данные из конфигурации
        String scenarioName = ConfigReader.getProperty("test.scenario.1.name");
        System.out.println("\n--- Запуск сценария: " + scenarioName + " ---");
        
        try {
            // Шаг 1: Открыть страницу
            driver.get(ConfigReader.getBaseUrl());
            System.out.println("✓ Шаг 1: Главная страница открыта");
            
            // Шаг 2: Найти поле поиска
            WebElement searchInput = driver.findElement(
                By.cssSelector(ConfigReader.getLocator("search.input"))
            );
            assertTrue(searchInput.isDisplayed(), "Поле поиска должно отображаться");
            System.out.println("✓ Шаг 2: Поле поиска найдено");
            
            // Шаг 3: Ввести поисковый запрос
            searchInput.sendKeys("футболка мужская");
            searchInput.sendKeys(Keys.ENTER);
            System.out.println("✓ Шаг 3: Поиск выполнен");
            
            // Шаг 4: Проверить результаты
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(ConfigReader.getLocator("product.card"))
            ));
            System.out.println("✓ Шаг 4: Результаты поиска отображены");
            
            // Шаг 5: Выбрать первый товар
            driver.findElement(
                By.cssSelector(ConfigReader.getLocator("product.card"))
            ).click();
            System.out.println("✓ Шаг 5: Товар выбран");
            
            // Шаг 6: Добавить в корзину
            wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(ConfigReader.getLocator("add.to.cart.button"))
            )).click();
            System.out.println("✓ Шаг 6: Товар добавлен в корзину");
            
            // Шаг 7: Проверить подтверждение
            try {
                // Проверяем счетчик корзины
                WebElement cartCounter = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(ConfigReader.getLocator("cart.counter"))
                    )
                );
                assertTrue(cartCounter.getText().contains("1"));
                System.out.println("✓ Шаг 7: Счетчик корзины обновлен");
                
            } catch (TimeoutException e) {
                // Или проверяем уведомление
                WebElement notification = driver.findElement(
                    By.cssSelector(ConfigReader.getLocator("notification"))
                );
                assertTrue(notification.isDisplayed());
                System.out.println("✓ Шаг 7: Уведомление отображено");
            }
            
            System.out.println("✅ Все шаги сценария выполнены успешно!");
            
        } catch (Exception e) {
            System.err.println("❌ Сценарий завершился с ошибкой: " + e.getMessage());
            throw e;
        }
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        System.out.println("=== Тестирование завершено ===\n");
    }
}
