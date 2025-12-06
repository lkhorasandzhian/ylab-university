package ru.ylab.levon.service.api;

import ru.ylab.levon.dto.ProductCreateDto;
import ru.ylab.levon.dto.ProductUpdateDto;
import ru.ylab.levon.model.Product;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface ProductService {

    Product addProduct(ProductCreateDto dto);

    Product getProduct(Long id);

    Collection<Product> getAllProducts();

    void removeProduct(Long id);

    boolean updateProduct(Long id, ProductUpdateDto dto);

    List<Product> findByCategory(String category);

    List<Product> findByBrand(String brand);

    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> search(String keyword);
}
