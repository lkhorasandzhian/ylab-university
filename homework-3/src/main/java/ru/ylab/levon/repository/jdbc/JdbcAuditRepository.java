package ru.ylab.levon.repository.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import ru.ylab.levon.model.AuditRecord;
import ru.ylab.levon.repository.api.AuditRepository;

public class JdbcAuditRepository implements AuditRepository {
    private final DataSource dataSource;

    public JdbcAuditRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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

    @Override
    public List<AuditRecord> findLast(int count) {
        String sql = """
                    SELECT * FROM domain.audit_records
                    ORDER BY id DESC
                    LIMIT ?
                """;

        List<AuditRecord> result = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, count);

            ResultSet rs = ps.executeQuery();

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
            throw new RuntimeException("Не удалось получить последние записи аудита из БД", e);
        }

        return result;
    }
}
