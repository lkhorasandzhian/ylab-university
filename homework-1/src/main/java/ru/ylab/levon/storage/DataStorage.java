package ru.ylab.levon.storage;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.ylab.levon.model.Product;
import ru.ylab.levon.model.User;
import ru.ylab.levon.model.AuditRecord;

/**
 * Класс для сохранения и загрузки данных приложения.
 * <p>
 * Отвечает за сериализацию и десериализацию товаров, пользователей
 * и записей аудита в бинарные файлы, расположенные в директории
 * {@code serialize_data/}.
 * <p>
 * Использует стандартные средства сериализации Java — {@link ObjectOutputStream}
 * и {@link ObjectInputStream}.
 */
public class DataStorage {

    private static final String DATA_DIR = "serialize_data/";
    private static final String PRODUCTS_FILE = DATA_DIR + "products.dat";
    private static final String USERS_FILE = DATA_DIR + "users.dat";
    private static final String AUDIT_FILE = DATA_DIR + "audit.dat";

    /**
     * Сохраняет в файловую систему все сущности приложения —
     * товары, пользователей и записи аудита.
     * <p>
     * Если директория для хранения данных отсутствует, она создаётся.
     *
     * @param products хранилище товаров
     * @param users    хранилище пользователей
     * @param audit    список записей аудита
     */
    public void saveData(Map<String, Product> products,
                         Map<String, User> users,
                         List<AuditRecord> audit) {

        File dir = new File(DATA_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println("Не удалось создать директорию данных: " + dir.getAbsolutePath());
            return;
        }

        saveObject(products, PRODUCTS_FILE);
        saveObject(users, USERS_FILE);
        saveObject(audit, AUDIT_FILE);
    }

    /**
     * Загружает сохранённые товары.
     *
     * @return хранилище товаров или пустое хранилище, если загрузить не удалось
     */
    @SuppressWarnings("unchecked")
    public Map<String, Product> loadProducts() {
        Object obj = loadObject(PRODUCTS_FILE);
        return obj instanceof Map ? (Map<String, Product>) obj : new HashMap<>();
    }

    /**
     * Загружает сохранённых пользователей.
     *
     * @return хранилище пользователей или пустое хранилище при неудаче
     */
    @SuppressWarnings("unchecked")
    public Map<String, User> loadUsers() {
        Object obj = loadObject(USERS_FILE);
        return obj instanceof Map ? (Map<String, User>) obj : new HashMap<>();
    }

    /**
     * Загружает записи аудита.
     *
     * @return список записей аудита или пустой список, если загрузка невозможна
     */
    @SuppressWarnings("unchecked")
    public List<AuditRecord> loadAudit() {
        Object obj = loadObject(AUDIT_FILE);
        return obj instanceof List ? (List<AuditRecord>) obj : new java.util.ArrayList<>();
    }

    /**
     * Сериализует переданный объект и сохраняет его в указанный файл.
     *
     * @param obj      объект для записи
     * @param filePath путь к файлу, в который будет сохранён объект
     */
    private void saveObject(Object obj, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }

    /**
     * Десериализует объект из указанного файла.
     *
     * @param filePath путь к сериализованному файлу
     * @return восстановленный объект или {@code null}, если операция завершилась неудачей
     */
    private Object loadObject(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при загрузке данных: " + e.getMessage());
            return null;
        }
    }
}
