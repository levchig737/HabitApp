# Сервис привычек
## Стартеры
В отдельный модуль habit-logging-starter вынесены AuditLoggingAspect и LoggingAspect.
- AuditLoggingAspect предназначен для: Логирования медленные методы, которые работают дольше заданного порога (1000 мс).
  Объявляется аннотацией @EnableAuditTrackExecutionTime перед главным классом приложения.
- LoggingAspect предназначен для: Логирования методов, вызываемых в репозиториях, сервисах и контроллерах.
  Работает без объявления аннотации.


## Запуск:
1. Клонируйте репозиторий

```shell
git clone https://github.com/levchig737/intensivJavaYLAB.git && cd intensivJavaYLAB
```

2. Создание контейнера PostgresSql

```shell
docker-compose up -d
```

3. Скомпилируйте проект с помощью Maven

```shell
mvn clean compile
```

4. Установите стартер habit-logging-starter

```shell
mvn -f ./habit-logging-starter install
```

5. Запустите приложение

```shell
mvn mvn spring-boot:run
```

## Запросы
Доступ по ссылке: http://localhost:8080/

## Структура базы данных
### Реляционная модель БД
<img src="./Реляционная_модель.png" width="520" height="425" alt="Реляционная модель"/>

### Доступные пользователи (Таблица users):
| id | name  | email              | password  | is_admin |
|----|-------|--------------------|-----------|----------|
| 1  | root  | root               | root      | true     |
| 2  | user  | user               | user      | false    |
| 3  | Bob   | bob@example.com     | 1234      | false    |

### Доступные привычки (Таблица habits)
| id | name           | description                    | frequency | created_date | user_id |
|----|----------------|--------------------------------|-----------|--------------|---------|
| 1  | Daily Exercise | Morning workout routine        | day       | 2024-10-15   | 1       |
| 2  | Reading        | Read one book per month        | month     | 2024-10-15   | 2       |
| 3  | Yoga           | Practice yoga every evening    | day       | 2024-10-15   | 3       |

### Доступные связи привычек и пользователей (Таблица habit_completion_history)
| id | habit_id | user_id |
|----|----------|---------|
| 1  | 1        | 1       |
| 2  | 2        | 2       |
| 3  | 3        | 3       |