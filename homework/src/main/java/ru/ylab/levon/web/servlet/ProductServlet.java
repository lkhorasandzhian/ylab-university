package ru.ylab.levon.web.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ylab.levon.dto.ProductCreateDto;
import ru.ylab.levon.dto.ProductUpdateDto;
import ru.ylab.levon.mapper.ProductMapper;
import ru.ylab.levon.model.Product;
import ru.ylab.levon.service.api.ProductService;
import ru.ylab.levon.service.api.UserService;
import ru.ylab.levon.web.util.JsonUtils;
import ru.ylab.levon.web.util.ValidatorUtils;

@WebServlet(name = "ProductServlet", urlPatterns = {"/products/*"})
public class ProductServlet extends HttpServlet {
    private ProductService productService;
    private UserService userService;

    /**
     * Инициализация сервисов каталога и пользователей.
     */
    @Override
    public void init() {
        productService = (ProductService) getServletContext().getAttribute("productService");
        userService = (UserService) getServletContext().getAttribute("userService");
    }

    /**
     * Обрабатывает POST-запросы для создания нового продукта.
     *
     * <p>Поддерживаемый эндпоинт:</p>
     * <ul>
     *   <li><b>POST /api/products</b> — создание продукта.</li>
     * </ul>
     *
     * @param req  HTTP-запрос, содержащий JSON с данными нового продукта
     * @param resp HTTP-ответ, содержащий созданный продукт или код ошибки
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!ensureLoggedIn(resp) || !ensureAdmin(resp)) {
            return;
        }

        ProductCreateDto dto;
        try {
            dto = JsonUtils.readJson(req, ProductCreateDto.class);
        } catch (Exception e) {
            JsonUtils.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "Invalid product JSON"));
            return;
        }

        var violations = ValidatorUtils.validate(dto);
        if (!violations.isEmpty()) {
            String message = violations.iterator().next().getMessage();
            JsonUtils.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", message));
            return;
        }

        Product createdProduct;
        try {
            createdProduct = productService.addProduct(dto);
        } catch (IllegalArgumentException e) {
            JsonUtils.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", e.getMessage()));
            return;
        }

        if (createdProduct == null) {
            JsonUtils.writeJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "Product created but not found"));
            return;
        }

        var result = ProductMapper.INSTANCE.toDto(createdProduct);
        JsonUtils.writeJson(resp, HttpServletResponse.SC_CREATED, result);
    }

    /**
     * Обрабатывает GET-запросы для получения информации о продуктах.
     *
     * <p>Поддерживаемые эндпоинты:</p>
     * <ul>
     *   <li><b>GET /api/products</b> — получить список всех продуктов;</li>
     *   <li><b>GET /api/products/{id}</b> — получить продукт по его уникальному идентификатору;</li>
     *   <li><b>GET /api/products</b> с параметрами фильтрации:
     *     <ul>
     *       <li><code>brand</code> — фильтр по бренду;</li>
     *       <li><code>category</code> — фильтр по категории;</li>
     *       <li><code>search</code> — поиск по названию/описанию;</li>
     *       <li><code>minPrice</code> — минимальная цена;</li>
     *       <li><code>maxPrice</code> — максимальная цена.</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * @param req  HTTP-запрос, содержащий путь и параметры фильтрации
     * @param resp HTTP-ответ, содержащий данные о продуктах или код ошибки
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!ensureLoggedIn(resp)) {
            return;
        }

        String idStr = extractId(req);

        if (idStr != null) {
            long id;
            try {
                id = Long.parseLong(idStr);
            } catch (NumberFormatException e) {
                JsonUtils.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                        Map.of("error", "ID must be a number"));
                return;
            }

            Product product = productService.getProduct(id);

            if (product == null) {
                JsonUtils.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                        Map.of("error", "Product not found: " + id));
                return;
            }

            JsonUtils.writeJson(resp, ProductMapper.INSTANCE.toDto(product));
            return;
        }

        String brand = req.getParameter("brand");
        String category = req.getParameter("category");
        String search = req.getParameter("search");
        String minPriceStr = req.getParameter("minPrice");
        String maxPriceStr = req.getParameter("maxPrice");

        List<Product> result;

        if (brand != null) {
            result = productService.findByBrand(brand);
        } else if (category != null) {
            result = productService.findByCategory(category);
        } else if (search != null) {
            result = productService.search(search);
        } else if (minPriceStr != null && maxPriceStr != null) {
            BigDecimal min, max;

            try {
                min = new BigDecimal(minPriceStr);
                max = new BigDecimal(maxPriceStr);
            } catch (NumberFormatException e) {
                JsonUtils.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                        Map.of("error", "minPrice/maxPrice must be numbers"));
                return;
            }

            result = productService.findByPriceRange(min, max);
        } else {
            result = productService.getAllProducts().stream().toList();
        }

        JsonUtils.writeJson(resp, ProductMapper.INSTANCE.toDtoList(result));
    }

    /**
     * Обрабатывает PATCH-запросы для частичного обновления существующего продукта.
     *
     * <p>Поддерживаемый эндпоинт:</p>
     * <ul>
     *   <li><b>PATCH /api/products/{id}</b> — обновление продукта по его уникальному идентификатору.</li>
     * </ul>
     *
     * @param req  HTTP-запрос, содержащий идентификатор продукта и данные для обновления
     * @param resp HTTP-ответ с обновленным продуктом или кодом ошибки
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
            return;
        }
        super.service(req, resp);
    }

    private void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!ensureLoggedIn(resp) || !ensureAdmin(resp)) {
            return;
        }

        Long id = checkID(req, resp);
        if (id == null) {
            return;
        }

        ProductUpdateDto dto;
        try {
            dto = JsonUtils.readJson(req, ProductUpdateDto.class);
        } catch (Exception e) {
            JsonUtils.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "Invalid update JSON"));
            return;
        }

        var violations = ValidatorUtils.validate(dto);
        if (!violations.isEmpty()) {
            String message = violations.iterator().next().getMessage();
            JsonUtils.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", message));
            return;
        }

        boolean updated;
        try {
            updated = productService.updateProduct(id, dto);
        } catch (IllegalArgumentException e) {
            JsonUtils.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", e.getMessage()));
            return;
        }

        if (!updated) {
            JsonUtils.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                    Map.of("error", "Product not found: " + id));
            return;
        }

        Product product = productService.getProduct(id);
        JsonUtils.writeJson(resp, ProductMapper.INSTANCE.toDto(product));
    }

    /**
     * Обрабатывает DELETE-запросы для удаления существующего продукта.
     *
     * <p>Поддерживаемый эндпоинт:</p>
     * <ul>
     *   <li><b>DELETE /api/products/{id}</b> — удаление продукта по его уникальному идентификатору.</li>
     * </ul>
     *
     * @param req  HTTP-запрос, содержащий идентификатор продукта
     * @param resp HTTP-ответ, подтверждающий удаление или содержащий код ошибки
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!ensureLoggedIn(resp) || !ensureAdmin(resp)) {
            return;
        }

        Long id = checkID(req, resp);
        if (id == null) {
            return;
        }

        Product existing = productService.getProduct(id);
        if (existing == null) {
            JsonUtils.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                    Map.of("error", "Product not found: " + id));
            return;
        }

        productService.removeProduct(id);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    /**
     * Извлекает идентификатор продукта из пути запроса.
     * <p>
     * Например, из пути {@code /123} вернёт {@code "123"}.
     *
     * @param req HTTP-запрос
     * @return строковый ID или {@code null}, если ID отсутствует
     */
    private String extractId(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null || path.equals("/"))
            return null;
        return path.substring(1);
    }

    /**
     * Проверяет корректность ID продукта, присутствие его в запросе и валидирует, что он является числом.
     *
     * @param req  HTTP-запрос
     * @param resp HTTP-ответ для записи ошибок
     * @return числовой ID или {@code null}, если он отсутствует или некорректен
     * @throws IOException при записи JSON-ошибки
     */
    private Long checkID(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = extractId(req);
        if (idStr == null) {
            JsonUtils.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "Product ID required"));
            return null;
        }

        long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            JsonUtils.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "ID must be numeric"));
            return null;
        }

        return id;
    }

    /**
     * Проверяет, что пользователь авторизован.
     *
     * @param resp HTTP-ответ для вывода сообщения об ошибке
     * @return true, если пользователь авторизован; иначе false
     * @throws IOException при записи JSON-ошибки
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean ensureLoggedIn(HttpServletResponse resp) throws IOException {
        if (!userService.isLoggedIn()) {
            JsonUtils.writeJson(resp, HttpServletResponse.SC_UNAUTHORIZED,
                    Map.of("error", "You must be logged in"));
            return false;
        }
        return true;
    }

    /**
     * Проверяет, что текущий пользователь обладает правами администратора.
     *
     * @param resp HTTP-ответ для вывода сообщения об ошибке
     * @return true, если пользователь — администратор; иначе false
     * @throws IOException при записи JSON-ошибки
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean ensureAdmin(HttpServletResponse resp) throws IOException {
        if (!userService.isAdmin()) {
            JsonUtils.writeJson(resp, HttpServletResponse.SC_FORBIDDEN,
                    Map.of("error", "Admin rights required"));
            return false;
        }
        return true;
    }
}
