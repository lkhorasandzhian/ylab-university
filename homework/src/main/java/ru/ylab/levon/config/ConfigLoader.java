package ru.ylab.levon.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Утилитарный класс для загрузки конфигурационных файлов из classpath.
 */
public final class ConfigLoader {
    // Static class.
    private ConfigLoader() {
    }

    /**
     * Загружает properties-файл из classpath.
     *
     * @param fileName имя файла конфигурации (например, "application.properties")
     * @return объект {@link Properties} с загруженными значениями
     * @throws RuntimeException если файл не найден или произошла ошибка чтения
     */
    public static Properties load(String fileName) {
        try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new RuntimeException("Config not found: " + fileName);
            }

            Properties props = new Properties();
            props.load(inputStream);
            return props;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }
}
