package ru.ylab.levon.repository;

import javax.sql.DataSource;
import java.math.BigDecimal;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import ru.ylab.levon.repository.jdbc.JdbcProductRepository;
import ru.ylab.levon.model.Product;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JdbcProductRepositoryTest {

    private PostgreSQLContainer<?> postgres;
    private JdbcProductRepository repository;
    private DataSource dataSource;

    @BeforeAll
    void startContainer() throws Exception {
        postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"))
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");

        postgres.start();

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(postgres.getJdbcUrl());
        ds.setUsername(postgres.getUsername());
        ds.setPassword(postgres.getPassword());
        ds.setDriverClassName("org.postgresql.Driver");

        this.dataSource = ds;

        Database database = DatabaseFactory.getInstance().openDatabase(
                ds.getJdbcUrl(),
                ds.getUsername(),
                ds.getPassword(),
                null,
                new ClassLoaderResourceAccessor()
        );

        Liquibase liquibase = new Liquibase(
                "db.changelog/db.changelog-master.xml",
                new ClassLoaderResourceAccessor(),
                database
        );

        liquibase.update();

        repository = new JdbcProductRepository(dataSource);
    }

    @Test
    void testCreateAndFindProduct() {
        Product p = new Product(
                null,
                "TestName",
                "TestCategory",
                "BrandX",
                new BigDecimal("123.45"),
                "desc"
        );

        repository.save(p);

        Assertions.assertNotNull(p.getId(), "ID должен быть сгенерирован!");

        Product loaded = repository.findById(p.getId());

        Assertions.assertNotNull(loaded);
        Assertions.assertEquals("TestName", loaded.getName());
        Assertions.assertEquals("BrandX", loaded.getBrand());
    }

    @AfterAll
    void stopContainer() {
        postgres.stop();
    }
}
