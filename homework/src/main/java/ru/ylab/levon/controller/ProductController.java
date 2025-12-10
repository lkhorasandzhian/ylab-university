package ru.ylab.levon.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ylab.levon.dto.ProductCreateDto;
import ru.ylab.levon.dto.ProductFilterDto;
import ru.ylab.levon.dto.ProductUpdateDto;
import ru.ylab.levon.mapper.ProductMapper;
import ru.ylab.levon.model.Product;
import ru.ylab.levon.service.api.ProductService;
import ru.ylab.levon.service.api.UserService;


/**
 * REST-контроллер для управления товарами.
 * <p>
 * Поддерживаемые эндпоинты:
 *
 * <h3>Общие (USER & ADMIN)</h3>
 * <ul>
 *     <li>GET /api/products — получить все товары</li>
 *     <li>GET /api/products/{id} — получить товар по ID</li>
 *     <li>GET /api/products?brand=X — фильтр по бренду</li>
 *     <li>GET /api/products?category=X — фильтр по категории</li>
 *     <li>GET /api/products?search=X — поиск по названию/описанию</li>
 *     <li>GET /api/products?minPrice=A&amp;maxPrice=B — фильтр по ценам</li>
 * </ul>
 *
 * <h3>Только ADMIN</h3>
 * <ul>
 *     <li>POST /api/products — создать товар</li>
 *     <li>PATCH /api/products/{id} — обновить товар</li>
 *     <li>DELETE /api/products/{id} — удалить товар</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final UserService userService;

    /**
     * Создание нового продукта (ADMIN).
     *
     * @param dto данные нового товара
     * @return 201 CREATED и DTO созданного товара
     */
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ProductCreateDto dto) {
        var loginError = requireLogin();
        if (loginError != null) {
            return loginError;
        }

        var adminError = requireAdmin();
        if (adminError != null) {
            return adminError;
        }

        Product createdProduct;
        try {
            createdProduct = productService.addProduct(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ProductMapper.INSTANCE.toDto(createdProduct));
    }

    /**
     * Получение списка товаров или фильтрация.
     *
     * @return 200 OK — список товаров
     */
    @GetMapping
    public ResponseEntity<?> getAll(@Valid @ModelAttribute ProductFilterDto dto) {
        var loginError = requireLogin();
        if (loginError != null) {
            return loginError;
        }

        List<Product> result;

        if (dto.brand() != null) {
            result = productService.findByBrand(dto.brand());
        } else if (dto.category() != null) {
            result = productService.findByCategory(dto.category());
        } else if (dto.search() != null) {
            result = productService.search(dto.search());
        } else if (dto.minPrice() != null && dto.maxPrice() != null) {
            result = productService.findByPriceRange(dto.minPrice(), dto.maxPrice());
        } else {
            result = productService.getAllProducts().stream().toList();
        }

        return ResponseEntity.ok(ProductMapper.INSTANCE.toDtoList(result));
    }

    /**
     * Получение конкретного товара по ID.
     *
     * @param id идентификатор товара
     * @return 200 OK — найденный товар,
     * 404 NOT FOUND — если товар отсутствует
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        var loginError = requireLogin();
        if (loginError != null) {
            return loginError;
        }

        Product product = productService.getProduct(id);
        if (product == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Product not found: " + id));
        }

        return ResponseEntity.ok(ProductMapper.INSTANCE.toDto(product));
    }

    /**
     * Частичное обновление товара (ADMIN).
     *
     * @param id  идентификатор товара
     * @param dto DTO обновления
     * @return 200 OK или 404 NOT FOUND
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDto dto) {
        var loginError = requireLogin();
        if (loginError != null) {
            return loginError;
        }

        var adminError = requireAdmin();
        if (adminError != null) {
            return adminError;
        }

        boolean isUpdated;
        try {
            isUpdated = productService.updateProduct(id, dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }

        if (!isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Product not found: " + id));
        }

        Product product = productService.getProduct(id);
        return ResponseEntity.ok(ProductMapper.INSTANCE.toDto(product));
    }

    /**
     * Удаление товара (ADMIN).
     *
     * @param id идентификатор товара
     * @return 204 NO CONTENT или 404 NOT FOUND
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        var loginError = requireLogin();
        if (loginError != null) {
            return loginError;
        }

        var adminError = requireAdmin();
        if (adminError != null) {
            return adminError;
        }

        Product product = productService.getProduct(id);
        if (product == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Product not found: " + id));
        }

        productService.removeProduct(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<?> requireLogin() {
        if (!userService.isLoggedIn()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "You must be logged in"));
        }
        return null;
    }

    private ResponseEntity<?> requireAdmin() {
        if (!userService.isAdmin()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Admin rights required"));
        }
        return null;
    }
}
