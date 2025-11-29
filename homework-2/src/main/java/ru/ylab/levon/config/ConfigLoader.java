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
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new RuntimeException("Не найден файл конфигурации: " + fileName);
            }

            Properties props = new Properties();
            props.load(input);
            return props;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки конфигурации", e);
        }
    }
}
