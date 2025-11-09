package ru.ylab.levon.storage;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.ylab.levon.model.Product;
import ru.ylab.levon.model.User;
import ru.ylab.levon.model.AuditRecord;

public class DataStorage {
    private static final String DATA_DIR = "data/";
    private static final String PRODUCTS_FILE = DATA_DIR + "products.dat";
    private static final String USERS_FILE = DATA_DIR + "users.dat";
    private static final String AUDIT_FILE = DATA_DIR + "audit.dat";

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

    @SuppressWarnings("unchecked")
    public Map<String, Product> loadProducts() {
        Object obj = loadObject(PRODUCTS_FILE);
        return obj instanceof Map ? (Map<String, Product>) obj : new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public Map<String, User> loadUsers() {
        Object obj = loadObject(USERS_FILE);
        return obj instanceof Map ? (Map<String, User>) obj : new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public List<AuditRecord> loadAudit() {
        Object obj = loadObject(AUDIT_FILE);
        return obj instanceof List ? (List<AuditRecord>) obj : new java.util.ArrayList<>();
    }

    private void saveObject(Object obj, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }

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
