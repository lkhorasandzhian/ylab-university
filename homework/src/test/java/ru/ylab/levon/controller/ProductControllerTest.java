package ru.ylab.levon.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.ylab.levon.dto.ProductCreateDto;
import ru.ylab.levon.model.Product;
import ru.ylab.levon.service.api.ProductService;
import ru.ylab.levon.service.api.UserService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductService productService;

    @MockBean
    UserService userService;

    @Test
    void getAll_whenNotLoggedIn_returnsUnauthorized() throws Exception {
        when(userService.isLoggedIn()).thenReturn(false);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("You must be logged in"));
    }

    @Test
    void getAll_whenLoggedIn_returnsAllProducts() throws Exception {
        when(userService.isLoggedIn()).thenReturn(true);

        var p1 = new Product(
                1L,
                "Phone",
                "Mobile",
                "Samsung",
                BigDecimal.valueOf(1000),
                "desc"
        );
        var p2 = new Product(
                2L,
                "Laptop",
                "Tech",
                "Dell",
                BigDecimal.valueOf(2000),
                "good"
        );

        when(productService.getAllProducts()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Phone"))
                .andExpect(jsonPath("$[1].brand").value("Dell"));
    }

    @Test
    void getAll_whenBrandFilter_returnsFiltered() throws Exception {
        when(userService.isLoggedIn()).thenReturn(true);

        var p = new Product(
                1L,
                "Phone",
                "Mobile",
                "Samsung",
                BigDecimal.valueOf(1000),
                "desc"
        );

        when(productService.findByBrand("Samsung")).thenReturn(List.of(p));

        mockMvc.perform(get("/api/products?brand=Samsung"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brand").value("Samsung"));
    }

    @Test
    void getById_whenNotLoggedIn_returnsUnauthorized() throws Exception {
        when(userService.isLoggedIn()).thenReturn(false);

        mockMvc.perform(get("/api/products/10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getById_whenProductNotFound_returnsNotFound() throws Exception {
        when(userService.isLoggedIn()).thenReturn(true);
        when(productService.getProduct(99L)).thenReturn(null);

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Product not found: 99"));
    }

    @Test
    void getById_whenFound_returnsProduct() throws Exception {
        when(userService.isLoggedIn()).thenReturn(true);

        var product = new Product(
                1L,
                "TV",
                "Electronics",
                "Sony",
                BigDecimal.valueOf(1500),
                "4k HDR"
        );

        when(productService.getProduct(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TV"))
                .andExpect(jsonPath("$.brand").value("Sony"));
    }

    @Test
    void create_whenNotLoggedIn_returnsUnauthorized() throws Exception {
        when(userService.isLoggedIn()).thenReturn(false);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "name": "Phone",
                                      "category": "Mobile",
                                      "brand": "Samsung",
                                      "price": 1000,
                                      "description": "desc"
                                    }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create_whenNotAdmin_returnsForbidden() throws Exception {
        when(userService.isLoggedIn()).thenReturn(true);
        when(userService.isAdmin()).thenReturn(false);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "name": "Phone",
                                      "category": "Mobile",
                                      "brand": "Samsung",
                                      "price": 1000
                                    }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Admin rights required"));
    }

    @Test
    void create_whenValidData_returnsCreated() throws Exception {
        when(userService.isLoggedIn()).thenReturn(true);
        when(userService.isAdmin()).thenReturn(true);

        var product = new Product(
                1L,
                "Phone",
                "Mobile",
                "Samsung",
                BigDecimal.valueOf(1000),
                "desc"
        );

        when(productService.addProduct(any(ProductCreateDto.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "name": "Phone",
                                      "category": "Mobile",
                                      "brand": "Samsung",
                                      "price": 1000,
                                      "description": "desc"
                                    }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Phone"))
                .andExpect(jsonPath("$.brand").value("Samsung"));
    }

    @Test
    void update_whenNotFound_returnsNotFound() throws Exception {
        when(userService.isLoggedIn()).thenReturn(true);
        when(userService.isAdmin()).thenReturn(true);

        when(productService.updateProduct(eq(50L), any())).thenReturn(false);

        mockMvc.perform(patch("/api/products/50")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    { "price": 2000 }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Product not found: 50"));
    }

    @Test
    void update_whenValid_returnsUpdated() throws Exception {
        when(userService.isLoggedIn()).thenReturn(true);
        when(userService.isAdmin()).thenReturn(true);

        when(productService.updateProduct(eq(1L), any())).thenReturn(true);

        var updated = new Product(
                1L,
                "Phone",
                "Mobile",
                "Samsung",
                BigDecimal.valueOf(1200),
                "updated"
        );

        when(productService.getProduct(1L)).thenReturn(updated);

        mockMvc.perform(patch("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    { "price": 1200 }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(1200));
    }

    @Test
    void delete_whenNotFound_returnsNotFound() throws Exception {
        when(userService.isLoggedIn()).thenReturn(true);
        when(userService.isAdmin()).thenReturn(true);

        when(productService.getProduct(10L)).thenReturn(null);

        mockMvc.perform(delete("/api/products/10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Product not found: 10"));
    }

    @Test
    void delete_whenExists_returnsNoContent() throws Exception {
        when(userService.isLoggedIn()).thenReturn(true);
        when(userService.isAdmin()).thenReturn(true);

        var product = new Product(
                10L,
                "TV",
                "Electronics",
                "Sony",
                BigDecimal.valueOf(1500),
                "HDR"
        );

        when(productService.getProduct(10L)).thenReturn(product);

        mockMvc.perform(delete("/api/products/10"))
                .andExpect(status().isNoContent());

        verify(productService).removeProduct(10L);
    }
}
