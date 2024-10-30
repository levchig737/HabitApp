# Сервис привычек
## Модели
- [Реляционная модель](https://github.com/levchig737/intensivJavaYLAB//tree/main/Реляционная_модель.png)
## Домашние задания
1. [ПР1](https://github.com/levchig737/intensivJavaYLAB/pull/3)
2. [ПР2](https://github.com/levchig737/intensivJavaYLAB/pull/4)
3. [ПР3](https://github.com/levchig737/intensivJavaYLAB/pull/5)

## Запуск:
### Подключение к бд
```
docker-compose up -d
```
### Запуск в TomCat.
1. Скачать TomCat10 по ссылке: [TomCat10](https://tomcat.apache.org/download-10.cgi)
2. Разархивировать.
3. Запустить проект в IntelliJ IDEA. Maven - lifecycle - package
4. Скопировать папку target/HabitApp в TomCat webapps. 
5. Запустить TomCat:
- Windows - клик на bin/catalina.bt
- Linux - ./bin/catalina.sh (при необходимости вывода в терминал в скрипте исправить строчку на: CATALINA_OUT="/dev/stdout")

### Запросы
Доступ по ссылке: http://localhost:8080/HabitApp