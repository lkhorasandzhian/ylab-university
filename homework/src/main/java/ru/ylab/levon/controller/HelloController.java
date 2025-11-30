package ru.ylab.levon.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloController {
    /**
     * Простой health-check контроллер.
     *
     * @return статус проверки
     */
    @GetMapping("/hello")
    public ResponseEntity<Map<String, String>> hello() {
        var body = Map.of(
                "status", "OK",
                "service", "Product Catalog Service",
                "timestamp", Instant.now().toString()
        );

        return ResponseEntity.ok(body);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> apiIndex() {
        var body = Map.of(
                "service", "Product Catalog Service",
                "timestamp", Instant.now().toString(),

                "auth", List.of(
                        "POST /api/auth/register",
                        "POST /api/auth/login",
                        "POST /api/auth/logout"
                ),

                "products_user", List.of(
                        "GET /api/products",
                        "GET /api/products/{id}",
                        "GET /api/products?brand=X",
                        "GET /api/products?category=X",
                        "GET /api/products?search=X",
                        "GET /api/products?minPrice=A&maxPrice=B"
                ),

                "products_admin", List.of(
                        "POST /api/products",
                        "PATCH /api/products/{id}",
                        "DELETE /api/products/{id}"
                ),

                "audit", List.of(
                        "GET /api/audit"
                )
        );

        return ResponseEntity.ok(body);
    }
}
