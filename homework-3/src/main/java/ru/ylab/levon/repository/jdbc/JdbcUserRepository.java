package ru.ylab.levon.repository.jdbc;

import java.sql.*;

import javax.sql.DataSource;
import ru.ylab.levon.model.Role;
import ru.ylab.levon.model.User;
import ru.ylab.levon.repository.api.UserRepository;

public class JdbcUserRepository implements UserRepository {

    private final DataSource dataSource;

    public JdbcUserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean save(User user) {
        String sql = """
                    INSERT INTO domain.users(id, username, password, role)
                    VALUES (nextval('domain.users_seq'), ?, ?, ?)
                    RETURNING id
                """;

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole().name());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user.setId(rs.getLong("id"));
            }

            return true;

        } catch (SQLException e) {
            // Проверка на потенциальное нарушение уникальности username.
            if ("23505".equals(e.getSQLState())) {
                // Обработка SQLSTATE-кода, сигнализирующем об ошибке целостности.
                return false;
            }
            throw new RuntimeException("Не удалось сохранить пользователя в БД", e);
        }
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM domain.users WHERE username = ?";

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role"))
                );
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Не удалось найти пользователя в БД", e);
        }
    }
}
