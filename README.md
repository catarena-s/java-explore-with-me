# Explore With Me
Сервис-афиша для размещения информации о событиях(от выставки до похода в кино), на котором пользователи могут находить компанию для участия в них.
## Стек
* Java 11
* Spring Boot
* Hibernate
* QueryDSL
* PostgreSQL
* Maven
* Docker

## Архитектура
Сервис состоит из двух модулей:
1. [Основной сервис](ewm-service/ReadMe.md) — отвечает за обработку информации, которая связана с событиями.
2. [Сервис статистики](ewm-stats-server/ReadMe.md) — хранит количество просмотров и позволяет делать различные выборки.

## Схема БД
![](ewm-service/doc/DB.png)

## Docker start-up guide
    mvn clean package
    docker-compose up -d

    Main service: http://localhost:8080
    Statistic service: http://localhost:9090