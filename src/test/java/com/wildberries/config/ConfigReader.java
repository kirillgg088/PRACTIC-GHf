package com.wildberries.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties = new Properties();
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        try (InputStream input = new FileInputStream("src/test/resources/config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            try (InputStream input = ConfigReader.class.getClassLoader()
                    .getResourceAsStream("config.properties")) {
                properties.load(input);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to load config.properties", ex);
            }
        }
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public static String getBaseUrl() {
        return getProperty("base.url");
    }
    
    public static String getBrowser() {
        return getProperty("browser");
    }
    
    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless"));
    }
    
    public static int getTimeout() {
        return Integer.parseInt(getProperty("timeout"));
    }
    
    public static String getLocator(String elementName) {
        return getProperty(elementName);
    }
}
