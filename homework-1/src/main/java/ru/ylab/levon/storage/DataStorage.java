package ru.ylab.levon.storage;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.ylab.levon.model.Product;
import ru.ylab.levon.model.User;
import ru.ylab.levon.model.AuditRecord;

/**
 * Класс {@code DataStorage} отвечает за сохранение и загрузку данных приложения.
 * <p>
 * Хранит информацию о товарах, пользователях и записях аудита
 * в сериализованных бинарных файлах внутри директории {@code data/}.
 * <p>
 * Использует стандартную Java-сериализацию через {@link ObjectOutputStream}
 * и {@link ObjectInputStream}.
 */
public class DataStorage {
    private static final String DATA_DIR = "serialize_data/";
    private static final String PRODUCTS_FILE = DATA_DIR + "products.dat";
    private static final String USERS_FILE = DATA_DIR + "users.dat";
    private static final String AUDIT_FILE = DATA_DIR + "audit.dat";

    /**
     * Сохраняет все данные приложения в файлы:
     * <ul>
     *     <li>Товары — {@code products.dat}</li>
     *     <li>Пользователи — {@code users.dat}</li>
     *     <li>Аудит — {@code audit.dat}</li>
     * </ul>
     * Если директория {@code data/} отсутствует, она создаётся автоматически.
     *
     * @param products хранилище товаров
     * @param users    хранилище пользователей
     * @param audit    список записей аудита
     */
    public void saveData(Map<String, Product> products,
                         Map<String, User> users,
                         List<AuditRecord> audit) {
        File dir = new File(DATA_DIR);

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.err.println("Не удалось создать директорию данных: " + dir.getAbsolutePath());
                return;
            }
        }

        saveObject(products, PRODUCTS_FILE);
        saveObject(users, USERS_FILE);
        saveObject(audit, AUDIT_FILE);
    }

    /**
     * Загружает сохранённые товары из файла {@code products.dat}.
     *
     * @return хранилище товаров; пустая, если файл отсутствует или повреждён
     */
    @SuppressWarnings("unchecked")
    public Map<String, Product> loadProducts() {
        Object obj = loadObject(PRODUCTS_FILE);
        return obj instanceof Map ? (Map<String, Product>) obj : new HashMap<>();
    }

    /**
     * Загружает сохранённых пользователей из файла {@code users.dat}.
     *
     * @return хранилище пользователей; пустая, если файл отсутствует или повреждён
     */
    @SuppressWarnings("unchecked")
    public Map<String, User> loadUsers() {
        Object obj = loadObject(USERS_FILE);
        return obj instanceof Map ? (Map<String, User>) obj : new HashMap<>();
    }

    /**
     * Загружает журнал аудита из файла {@code audit.dat}.
     *
     * @return список записей аудита; пустой, если файл отсутствует или повреждён
     */
    @SuppressWarnings("unchecked")
    public List<AuditRecord> loadAudit() {
        Object obj = loadObject(AUDIT_FILE);
        return obj instanceof List ? (List<AuditRecord>) obj : new java.util.ArrayList<>();
    }

    /**
     * Сохраняет переданный объект в указанный файл.
     *
     * @param obj      объект для сериализации
     * @param filePath путь к файлу
     */
    private void saveObject(Object obj, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }

    /**
     * Загружает объект из указанного файла.
     *
     * @param filePath путь к файлу
     * @return восстановленный объект или {@code null}, если загрузка не удалась
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
