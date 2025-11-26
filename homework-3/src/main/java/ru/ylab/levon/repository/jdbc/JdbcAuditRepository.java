package ru.ylab.levon.repository.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;
import javax.sql.DataSource;

import ru.ylab.levon.model.AuditRecord;
import ru.ylab.levon.repository.api.AuditRepository;

/**
 * JDBC-реализация {@link AuditRepository}, обеспечивающая
 * сохранение и выборку записей аудита.
 */
public class JdbcAuditRepository implements AuditRepository {
    private final DataSource dataSource;

    /**
     * Создаёт репозиторий аудита на основе предоставленного {@link DataSource}.
     *
     * @param dataSource источник соединений с базой данных
     */
    public JdbcAuditRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Добавляет новую запись аудита в базу данных.
     * <p>
     * Использует последовательность `domain.audit_seq` и возвращает
     * сгенерированный идентификатор, который сохраняется в объекте {@link AuditRecord}.
     *
     * @param record запись аудита
     * @throws RuntimeException при ошибке SQL
     */
    @Override
    public void add(AuditRecord record) {
        String sql = """
                    INSERT INTO domain.audit_records(id, username, action, timestamp)
                    VALUES (nextval('domain.audit_seq'), ?, ?, ?)
                    RETURNING id
                """;

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, record.getUsername());
            ps.setString(2, record.getAction());
            ps.setTimestamp(3, Timestamp.valueOf(record.getTimestamp()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                record.setId(rs.getLong("id"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Не удалось добавить запись аудита в БД", e);
        }
    }

    /**
     * Возвращает все записи аудита, отсортированные по ID в порядке возрастания.
     *
     * @return список всех записей аудита
     * @throws RuntimeException при ошибке SQL
     */
    @Override
    public List<AuditRecord> findAll() {
        String sql = "SELECT * FROM domain.audit_records ORDER BY id";

        List<AuditRecord> result = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                AuditRecord r = new AuditRecord(
                        rs.getString("username"),
                        rs.getString("action"),
                        rs.getTimestamp("timestamp").toLocalDateTime()
                );
                r.setId(rs.getLong("id"));
                result.add(r);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Не удалось получить все записи аудита из БД", e);
        }

        return result;
    }
}
