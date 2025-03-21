package com.my.configuration;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

public class AppConfiguration {
    private static final Logger logger = LogManager.getRootLogger();

    private static final String CONFIG_FILE_PATH = "src/main/resources/application.properties";
    private static final Properties properties;

    private AppConfiguration() {
    }

    static {
        properties = new Properties();
        try {
            loadConfig();
        } catch (IOException e) {
            logger.log(Level.ERROR, MessageFormat.format("Failed to load database configuration: {0}", e.getMessage()));
        }
    }

    private static void loadConfig() throws IOException {
        try (FileInputStream inputStream = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(inputStream);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
