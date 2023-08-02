# Stats service 

Сервис статистики собирает информацию о количестве просмотров событий
и позволяет делать различные выборки для анализа работы приложения. 

## Описание API
[Swagger API Specification](https://raw.githubusercontent.com/catarena-s/java-explore-with-me/main/ewm-stats-service-spec.json)

| Method       | HTTP request   | Description                                             |
|--------------|----------------|---------------------------------------------------------|
| **getStats** | **GET** /stats | Получение статистики по посещениям.                     |
| **hit**      | **POST** /hit  | Сохранение информации о том, что к эндпоинту был запрос |

### [Тесты postman: /postman/ewm-stat-service.json](https://raw.githubusercontent.com/catarena-s/java-explore-with-me/feature_subscriptions/postman/ewm-stat-service.json)
