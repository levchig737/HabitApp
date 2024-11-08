# Сервис привычек
## Модели
### Реляционная модель БД
![Реляционная модель](https://github.com/levchig737/intensivJavaYLAB//tree/main/Реляционная_модель.png)


## Стартеры
В отдельный модуль habit-logging-starter вынесены AuditLoggingAspect и LoggingAspect.
- AuditLoggingAspect предназначен для: Логирования медленные методы, которые работают дольше заданного порога (1000 мс). 
Объявляется аннотацией @EnableAuditTrackExecutionTime перед главным классом приложения.
- LoggingAspect предназначен для: Логирования методов, вызываемых в репозиториях, сервисах и контроллерах.
Работает без объявления аннотации.


## Запуск:
### Подключение к бд
```
docker-compose up -d
```

### Запуск в TomCat 10.
- Windows - клик на bin/catalina.bt
- Linux - ./bin/catalina.sh (при необходимости вывода в терминал в скрипте исправить строчку на: CATALINA_OUT="/dev/stdout")

### Запуск через IDEA Intellij (предпочтительно)
- В habit-logging-starter: mvn clean; maven package; maven install
- В основном проекте: mnv clean; compile
- Запустить HabitAppApplication

### Запросы
Доступ по ссылке: http://localhost:8080/