package com.my.configuration;

import com.my.Main;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

public class AppConfiguration {
    private static final Logger logger = LogManager.getRootLogger();

    private static final String CONFIG_FILE_PATH = "/application.properties";
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
        properties.load(Main.class.getResourceAsStream(CONFIG_FILE_PATH));
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
