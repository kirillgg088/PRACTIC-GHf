package com.wildberries.webtests;

import com.wildberries.core.TestExecutor;
import com.wildberries.config.TestDataReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.stream.Stream;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Конфигурационное тестирование Wildberries")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConfigBasedTest {
    private WebDriver driver;
    
    @BeforeAll
    void setupAll() {
        WebDriverManager.chromedriver().setup();
    }
    
    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }
    
    // Источник данных: все сценарии из конфигурации
    Stream<Arguments> provideTestScenarios() {
        JsonArray scenarios = TestDataReader.getTestScenarios();
        return scenarios.asList().stream()
            .map(element -> {
                JsonObject scenario = element.getAsJsonObject();
                return arguments(
                    scenario.get("id").getAsInt(),
                    scenario.get("name").getAsString(),
                    scenario.get("expectedResult").getAsString()
                );
            });
    }
    
    @ParameterizedTest(name = "Сценарий #{0}: {1}")
    @MethodSource("provideTestScenarios")
    @DisplayName("Выполнение сценариев из конфигурации")
    void testScenarioFromConfig(int scenarioId, String scenarioName, String expectedResult) {
        System.out.println("\n========================================");
        System.out.println("Запуск сценария #" + scenarioId);
        System.out.println("Название: " + scenarioName);
        System.out.println("Ожидаемый результат: " + expectedResult);
        System.out.println("========================================");
        
        TestExecutor executor = new TestExecutor(driver);
        JsonArray scenarios = TestDataReader.getTestScenarios();
        
        // Находим нужный сценарий
        for (var element : scenarios) {
            JsonObject scenario = element.getAsJsonObject();
            if (scenario.get("id").getAsInt() == scenarioId) {
                JsonArray steps = scenario.getAsJsonArray("steps");
                
                // Выполняем каждый шаг
                steps.forEach(stepElement -> {
                    JsonObject step = stepElement.getAsJsonObject();
                    String action = step.get("action").getAsString();
                    
                    // Извлекаем параметры
                    var paramsArray = step.getAsJsonArray("params");
                    String[] params = new String[paramsArray.size()];
                    for (int i = 0; i < paramsArray.size(); i++) {
                        params[i] = paramsArray.get(i).getAsString();
                    }
                    
                    // Выполняем шаг
                    try {
                        executor.executeStep(action, params);
                        System.out.println("✓ Шаг выполнен: " + action);
                    } catch (Exception e) {
                        System.err.println("✗ Ошибка при выполнении шага: " + action);
                        System.err.println("Ошибка: " + e.getMessage());
                        throw new AssertionError("Сценарий прерван на шаге: " + action, e);
                    }
                });
                
                System.out.println("✅ Сценарий успешно завершен!");
                System.out.println("Фактический результат соответствует ожидаемому: " + expectedResult);
                return;
            }
        }
        
        throw new IllegalArgumentException("Сценарий с ID " + scenarioId + " не найден");
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
