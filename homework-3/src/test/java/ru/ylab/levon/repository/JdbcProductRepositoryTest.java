package ru.ylab.levon.repository;

import java.math.BigDecimal;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import com.zaxxer.hikari.HikariDataSource;
import ru.ylab.levon.repository.jdbc.JdbcProductRepository;
import ru.ylab.levon.model.Product;

/**
 * Интеграционные тесты для {@link JdbcProductRepository},
 * выполняемые с использованием Testcontainers и реального PostgreSQL.
 * <p>
 * Данный тестовый класс создаёт PostgreSQL-контейнер,
 * применяет Liquibase-миграции и проверяет корректность
 * операций сохранения и поиска продукта.
 */
@DisplayName("Интеграционные тесты JdbcProductRepository")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JdbcProductRepositoryTest {
    private PostgreSQLContainer<?> postgres;
    private JdbcProductRepository repository;

    /**
     * Инициализирует контейнер PostgreSQL, запускает его,
     * настраивает пул соединений, применяет Liquibase-миграции
     * и создаёт экземпляр репозитория.
     *
     * @throws Exception при ошибках запуска контейнера или миграций
     */
    @BeforeAll
    void startContainer() throws Exception {
        // noinspection resource
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

        new liquibase.command.CommandScope("update")
                .addArgumentValue("changeLogFile", "db.changelog/db.changelog-master.xml")
                .addArgumentValue("url", ds.getJdbcUrl())
                .addArgumentValue("username", ds.getUsername())
                .addArgumentValue("password", ds.getPassword())
                .execute();

        repository = new JdbcProductRepository(ds);
    }

    /**
     * Проверяет корректность создания и последующей загрузки продукта
     * из базы данных через {@link JdbcProductRepository}.
     * <p>
     * Тест гарантирует:
     * <ul>
     *     <li>ID продукта корректно генерируется;</li>
     *     <li>Продукт успешно сохраняется;</li>
     *     <li>Метод {@code findById()} возвращает корректные данные.</li>
     * </ul>
     */
    @Test
    @DisplayName("Сохранение и последующая загрузка продукта должны работать корректно")
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

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(p.getId())
                .as("ID должен быть сгенерирован!")
                .isNotNull();

        Product loaded = repository.findById(p.getId());

        softly.assertThat(loaded)
                .as("Продукт должен быть найден в базе")
                .isNotNull();

        if (loaded != null) {
            softly.assertThat(loaded.getName()).isEqualTo("TestName");
            softly.assertThat(loaded.getBrand()).isEqualTo("BrandX");
            softly.assertThat(loaded.getCategory()).isEqualTo("TestCategory");
            softly.assertThat(loaded.getPrice()).isEqualByComparingTo("123.45");
            softly.assertThat(loaded.getDescription()).isEqualTo("desc");
        }

        softly.assertAll();
    }

    /**
     * Останавливает контейнер PostgreSQL после выполнения всех тестов.
     */
    @AfterAll
    void stopContainer() {
        postgres.stop();
    }
}
