# Homework 2 — JDBC, Liquibase, Docker, Testcontainers

Домашнее задание по теме **"JDBC + Миграции БД + PostgreSQL"**.  
Проект полностью переведён с файлового хранения на работу через базу данных.

---

## Как запустить проект

### Поднять PostgreSQL через Docker

В корне проекта находится `docker-compose.yml`.  
Запустить  контейнер:

```sh
docker-compose up -d
```
### После запуска PostgreSQL будет доступен:

- host: localhost
- port: 5432
- database: ylabdb
- user: ylab_user
- password: ylab_pass

## 🧪 Тесты (Testcontainers)

Тесты используют временный PostgreSQL внутри Docker, без подключения к рабочей базе.

### Запуск тестов:

Тесты используют временный PostgreSQL внутри Docker, без подключения к рабочей базе.

Запуск тестов:

```sh
mvn test
```

Testcontainers сам:
- поднимет контейнер PostgreSQL;
- применит миграции Liquibase;
- выполнит тесты;
- удалит контейнер.
