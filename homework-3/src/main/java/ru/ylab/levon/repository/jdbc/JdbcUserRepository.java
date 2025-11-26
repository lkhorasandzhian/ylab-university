package ru.ylab.levon.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import ru.ylab.levon.model.Role;
import ru.ylab.levon.model.User;
import ru.ylab.levon.repository.api.UserRepository;

/**
 * Реализация {@link UserRepository}, основанная на JDBC.
 * <p>
 * Выполняет операции сохранения и поиска пользователей
 * с использованием подключённого {@link DataSource}.
 */
public class JdbcUserRepository implements UserRepository {
    private final DataSource dataSource;

    /**
     * Создаёт новый экземпляр репозитория пользователей.
     *
     * @param dataSource пул соединений, используемый для выполнения SQL-операций
     */
    public JdbcUserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Сохраняет пользователя в базе данных.
     * <p>
     * В случае успеха ID пользователя будет обновлён,
     * так как запрос использует `RETURNING id`.
     * <p>
     * Если происходит нарушение уникальности username (SQLSTATE 23505),
     * метод возвращает {@code false} без выброса исключения.
     *
     * @param user объект пользователя для сохранения
     * @return {@code true}, если сохранение прошло успешно; {@code false}, если нарушена уникальность username
     * @throws RuntimeException при любой другой SQL-ошибке
     */
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

    /**
     * Ищет пользователя по имени пользователя (username).
     *
     * @param username уникальное имя пользователя
     * @return найденный {@link User} или {@code null}, если пользователь отсутствует
     * @throws RuntimeException при SQL-ошибках во время поиска
     */
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
