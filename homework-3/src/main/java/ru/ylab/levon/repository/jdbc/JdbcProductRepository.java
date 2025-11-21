package ru.ylab.levon.repository.jdbc;

import ru.ylab.levon.model.Product;
import ru.ylab.levon.repository.api.ProductRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JdbcProductRepository implements ProductRepository {

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
        String sql = "SELECT * FROM domain.product WHERE id=?";

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Не удалось найти продукт", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM domain.product WHERE id = ?";

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Не удалось удалить продукт", e);
        }
    }

    @Override
    public Collection<Product> findAll() {
        String sql = "SELECT * FROM domain.product";

        List<Product> list = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Не удалось получить список продуктов", e);
        }

        return list;
    }

    private void insert(Product product) {
        String sql = """
                    INSERT INTO domain.product(id, name, category, brand, price, description)
                    VALUES (nextval('domain.product_seq'), ?, ?, ?, ?, ?)
                    RETURNING id
                """;

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);

            fillCommonFields(ps, product);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                product.setId(rs.getLong("id"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Не удалось вставить продукт в БД", e);
        }
    }

    private void update(Product product) {
        String sql = """
                    UPDATE domain.product
                    SET name=?, category=?, brand=?, price=?, description=?
                    WHERE id=?
                """;

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);

            fillCommonFields(ps, product);
            ps.setLong(6, product.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Не удалось обновить продукт в БД", e);
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
