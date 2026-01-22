package com.wildberries.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class TestDataReader {
    private static JsonObject testData;
    
    static {
        loadTestData();
    }
    
    private static void loadTestData() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/test/resources/test-data.json")) {
            testData = gson.fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            try (Reader reader = TestDataReader.class.getClassLoader()
                    .getResourceAsStream("test-data.json")) {
                testData = gson.fromJson(reader, JsonObject.class);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to load test-data.json", ex);
            }
        }
    }
    
    public static JsonArray getTestScenarios() {
        return testData.getAsJsonArray("testScenarios");
    }
    
    public static JsonArray getTestUsers() {
        return testData.getAsJsonArray("testUsers");
    }
    
    public static Map<String, String> getTestScenarioById(int id) {
        Map<String, String> scenario = new HashMap<>();
        JsonArray scenarios = getTestScenarios();
        
        for (var element : scenarios) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.get("id").getAsInt() == id) {
                scenario.put("name", obj.get("name").getAsString());
                scenario.put("description", obj.get("description").getAsString());
                scenario.put("expectedResult", obj.get("expectedResult").getAsString());
                return scenario;
            }
        }
        return null;
    }
}
