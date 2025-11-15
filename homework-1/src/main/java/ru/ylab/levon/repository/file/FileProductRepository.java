package ru.ylab.levon.repository.file;

import ru.ylab.levon.model.Product;
import ru.ylab.levon.repository.api.ProductRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FileProductRepository implements ProductRepository {

    private final Map<String, Product> products;

    public FileProductRepository(Map<String, Product> initialData) {
        this.products = new HashMap<>(initialData);
    }

    @Override
    public void save(Product product) {
        products.put(product.getId(), product);
    }

    @Override
    public Product findById(String id) {
        return products.get(id);
    }

    @Override
    public void delete(String id) {
        products.remove(id);
    }

    @Override
    public Collection<Product> findAll() {
        return products.values();
    }

    @Override
    public Map<String, Product> getStorage() {
        return products;
    }
}
