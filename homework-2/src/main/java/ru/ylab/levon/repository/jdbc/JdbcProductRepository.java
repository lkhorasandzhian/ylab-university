package ru.ylab.levon.repository.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import ru.ylab.levon.model.Product;
import ru.ylab.levon.repository.api.ProductRepository;

/**
 * JDBC-реализация {@link ProductRepository}, обеспечивающая
 * сохранение, обновление, удаление и выборку продуктов
 * с использованием SQL-запросов и {@link DataSource}.
 */
public class JdbcProductRepository implements ProductRepository {
    private static final String GET_PRODUCT_BY_ID = """
            SELECT * FROM domain.product WHERE id=?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM domain.product WHERE id=?
            """;

    private static final String GET_ALL_PRODUCTS_SQL = """
            SELECT * FROM domain.product
            """;

    private static final String PUT_PRODUCT_SQL = """
            INSERT INTO domain.product(id, name, category, brand, price, description)
            VALUES (nextval('domain.product_seq'), ?, ?, ?, ?, ?)
            RETURNING id
            """;

    private static final String UPDATE_PRODUCT_SQL = """
            UPDATE domain.product
            SET name=?, category=?, brand=?, price=?, description=?
            WHERE id=?
            """;

    private final DataSource dataSource;

    public JdbcProductRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Product product) {
        if (product.getId() == null) {
            insert(product);
        } else {
            update(product);
        }
    }

    @Override
    public Product findById(Long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_PRODUCT_BY_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка доступа к данным при поиске продукта id=" + id, e);
        }
    }

    @Override
    public void delete(Long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка доступа к данным при удалении продукта id=" + id, e);
        }
    }

    @Override
    public Collection<Product> findAll() {
        List<Product> list = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ALL_PRODUCTS_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка доступа к данным при получении списка продуктов", e);
        }

        return list;
    }

    private void insert(Product product) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(PUT_PRODUCT_SQL)) {

            fillCommonFields(ps, product);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    product.setId(rs.getLong("id"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка доступа к данным при вставке продукта: " + product, e);
        }
    }

    private void update(Product product) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_PRODUCT_SQL)) {

            fillCommonFields(ps, product);
            ps.setLong(6, product.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка доступа к данным при обновлении продукта id=" + product.getId(), e);
        }
    }

    private void fillCommonFields(PreparedStatement ps, Product product) throws SQLException {
        ps.setString(1, product.getName());
        ps.setString(2, product.getCategory());
        ps.setString(3, product.getBrand());
        ps.setBigDecimal(4, product.getPrice());
        ps.setString(5, product.getDescription());
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getString("brand"),
                rs.getBigDecimal("price"),
                rs.getString("description")
        );
    }
}
