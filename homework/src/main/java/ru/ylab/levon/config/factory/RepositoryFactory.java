package ru.ylab.levon.config.factory;

import ru.ylab.levon.repository.api.*;
import ru.ylab.levon.repository.jdbc.*;

import javax.sql.DataSource;
import java.util.Properties;

public class RepositoryFactory {
    public static ProductRepository createProductRepository(Properties props, DataSource ds) {
        String type = props.getProperty("repo.product", "jdbc").toLowerCase();

        return switch (type) {
            case "jdbc" -> new JdbcProductRepository(ds);
            default -> throw new UnsupportedOperationException();
        };
    }

    public static UserRepository createUserRepository(Properties props, DataSource ds) {
        String type = props.getProperty("repo.user", "jdbc").toLowerCase();

        return switch (type) {
            case "jdbc" -> new JdbcUserRepository(ds);
            default -> throw new UnsupportedOperationException();
        };
    }

    public static AuditRepository createAuditRepository(Properties props, DataSource ds) {
        String type = props.getProperty("repo.audit", "jdbc").toLowerCase();

        return switch (type) {
            case "jdbc" -> new JdbcAuditRepository(ds);
            default -> throw new UnsupportedOperationException();
        };
    }
}
