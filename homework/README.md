# Homework 4 — Знакомство с Spring Framework

Домашнее задание по теме **"Spring Framework"**.  

---

## Как запустить проект

### Поднять PostgreSQL через Docker

В корне проекта находится `docker-compose.yml`.  
Запустить контейнер:

```sh
docker-compose up -d
```
### После запуска PostgreSQL будет доступен:

- host: localhost
- port: 5432
- database: ylabdb
- user: ylab_user
- password: ylab_pass

### Запуск приложения (Swagger)

Приложение разворачивается как JAR и доступно через Swagger UI.
Liquibase автоматически применяет миграции при старте.

По умолчанию создается admin с правами ADMINISTRATOR.  
Данные для входа: admin/admin.

## 📚 REST API — Полная таблица эндпоинтов
Приложение доступно по ссылке:
http://localhost:8080/swagger-ui/index.html

### 🔐 Auth (аутентификация)

|  Метод  |       Endpoint        |                        Описание                        |
|:-------:|:---------------------:|:------------------------------------------------------:|
|  POST   | `/api/auth/register`  |  Регистрация нового пользователя *(роль всегда USER)*  |
|  POST   |   `/api/auth/login`   |                Авторизация пользователя                |
|  POST   |  `/api/auth/logout`   |                   Выход из аккаунта                    |

---

### 🛒 Products (товары)
Доступно всем авторизованным пользователям (USER + ADMIN)

| Метод | Endpoint                              | Описание                   |
|-------|---------------------------------------|----------------------------|
| GET   | `/api/products`                       | Получить все товары        |
| GET   | `/api/products/{id}`                  | Получить товар по ID       |
| GET   | `/api/products?brand=X`               | Фильтр по бренду           |
| GET   | `/api/products?category=X`            | Фильтр по категории        |
| GET   | `/api/products?search=X`              | Поиск по названию/описанию |
| GET   | `/api/products?minPrice=A&maxPrice=B` | Фильтр по цене             |

### 🧑‍💼 Только ADMIN

| Метод  | Endpoint             | Описание                    |
|--------|----------------------|-----------------------------|
| POST   | `/api/products`      | Создать новый товар         |
| PATCH  | `/api/products/{id}` | Частичное обновление товара |
| DELETE | `/api/products/{id}` | Удалить товар               |

---

### 📜 Audit (аудит)

| Метод | Endpoint                  | Описание            |
|-------|---------------------------|---------------------|
| GET   | `/api/audit`              | Все записи аудита   |


## Пример запросов
### 1. Авторизация админа (admin / admin)
```http request
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin"
}
```


### 2. Создание продуктов (админ или любой пользователь)
#### 2.1 Создать продукт 1
```http request
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "iPhone 15",
  "category": "Smartphones",
  "brand": "Apple",
  "price": 1299.99,
  "description": "Flagship Apple smartphone"
}
```

#### 2.2 Создать продукт 2
```http request
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "Galaxy S24",
  "category": "Smartphones",
  "brand": "Samsung",
  "price": 1199.99,
  "description": "New Samsung Galaxy phone"
}
```

#### 2.3 Создать продукт 3
```http request
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "PlayStation 5",
  "category": "Gaming",
  "brand": "Sony",
  "price": 499.99,
  "description": "Next-gen gaming console"
}
```


### 3. Получить список всех продуктов
```http request
GET http://localhost:8080/api/products
```


### 4. Регистрация нового пользователя (user1)
```http request
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "user1",
  "password": "qwerty123"
}
```


### 5. Авторизация нового пользователя
```http request
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "user1",
  "password": "qwerty123"
}
```


### 6. Выход пользователя
```http request
POST http://localhost:8080/api/auth/logout
```

## Аспекты — Spring AOP
Реализованы аспекты для:
- Логирования времени работы методов в миллисекундах;
- Аудита действий пользователя.

## Тестирование
Добавлены WebMVC-тесты на следующие контроллеры:
- HelloController.java
- AuditController.java 
- AuthController.java
- ProductController.java