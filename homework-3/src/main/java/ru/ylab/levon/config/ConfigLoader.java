package ru.ylab.levon.config;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    /**
     * Загружает *.properties* файл из classpath.
     */
    public Properties load(String file) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(file)) {
            if (is == null) {
                throw new RuntimeException("Config not found: " + file);
            }
            Properties props = new Properties();
            props.load(is);
            return props;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }
}
