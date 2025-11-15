package ru.ylab.levon.repository.api;

import ru.ylab.levon.model.Product;

import java.util.Collection;
import java.util.Map;

public interface ProductRepository {

    void save(Product product);

    Product findById(String id);

    void delete(String id);

    Collection<Product> findAll();

    Map<String, Product> getStorage();
}
