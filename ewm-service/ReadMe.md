# Main service 

API с тремя уровнями доступа: публичный, приватный и администратора.

## Основной функционал
* Public API - доступен без регистрации
  * просмотр событий и подборок событий
  * просмотр категорий
* Private API - доступен только зарегистрированным пользователям
  * Добавление и редактирование события
  * Работа с запросами на участии в событие
  * Подача/отмена запроса на участие в событии
  * Просмотр информации о запросах на участие
  * Получение полной информации о событиях текущего пользователя
* Admin API - доступен администратору сервиса
  * Добавление/изменение/удаление категорий
  * Добавление/удаление пользователей
  * Создание/редактирование/удаление подборок событий
  * Получение информации о пользователе
  * Обновление информации о событии

## Дополнительный функционал
1. [x] [Подписка на друзей](FollowFriend.md)
2. [ ] Комментарии
3. [ ] Локации
4. [ ] Лайки
5. [ ] Администрирование

## Описание API основного функционала
[Swagger API Specification](https://raw.githubusercontent.com/catarena-s/java-explore-with-me/main/ewm-service/ewm-main-service-spec.json)

### Public API
| Method              | HTTP request                   | Description                                                                    |
|---------------------|--------------------------------|--------------------------------------------------------------------------------|
| **getCategories**   | **GET** /categories            | Получение категорий                                                            |
| **getCategory**     | **GET** /categories/{catId}    | Получение информации о категории по её идентификатору                          |
| **getCompilation**  | **GET** /compilations/{compId} | Получение подборки событий по его id                                           |
| **getCompilations** | **GET** /compilations          | Получение подборок событий                                                     |
| **getEvent1**       | **GET** /events/{id}           | Получение подробной информации об опубликованном событии по его идентификатору |
| **getEvents1**      | **GET** /events                | Получение событий с возможностью фильтрации                                    |

### Admin API
| Method                | HTTP request                            | Description                                                          |
|-----------------------|-----------------------------------------|----------------------------------------------------------------------|
| **addCategory**       | **POST** /admin/categories              | Добавление новой категории                                           |
| **delete**            | **DELETE** /admin/users/{userId}        | Удаление пользователя                                                |
| **deleteCategory**    | **DELETE** /admin/categories/{catId}    | Удаление категории                                                   |
| **deleteCompilation** | **DELETE** /admin/compilations/{compId} | Удаление подборки                                                    |
| **getEvents2**        | **GET** /admin/events                   | Поиск событий                                                        |
| **getUsers**          | **GET** /admin/users                    | Получение информации о пользователях                                 |
| **registerUser**      | **POST** /admin/users                   | Добавление нового пользователя                                       |
| **saveCompilation**   | **POST** /admin/compilations            | Добавление новой подборки (подборка может не содержать событий)      |
| **updateCategory**    | **PATCH** /admin/categories/{catId}     | Изменение категории                                                  |
| **updateCompilation** | **PATCH** /admin/compilations/{compId}  | Обновить информацию о подборке                                       |
| **updateEvent1**      | **PATCH** /admin/events/{eventId}       | Редактирование данных события и его статуса (отклонение/публикация). |

### Private API
| Method                      | HTTP request                                          | Description                                                                                  |
|-----------------------------|-------------------------------------------------------|----------------------------------------------------------------------------------------------|
| **addEvent**                | **POST** /users/{userId}/events                       | Добавление нового события                                                                    |
| **addParticipationRequest** | **POST** /users/{userId}/requests                     | Добавление запроса от текущего пользователя на участие в событии                             |
| **cancelRequest**           | **PATCH** /users/{userId}/requests/{requestId}/cancel | Отмена своего запроса на участие в событии                                                   |
| **changeRequestStatus**     | **PATCH** /users/{userId}/events/{eventId}/requests   | Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя |
| **getEvent**                | **GET** /users/{userId}/events/{eventId}              | Получение полной информации о событии добавленном текущим пользователем                      |
| **getEventParticipants**    | **GET** /users/{userId}/events/{eventId}/requests     | Получение информации о запросах на участие в событии текущего пользователя                   |
| **getEvents**               | **GET** /users/{userId}/events                        | Получение событий, добавленных текущим пользователем                                         |
| **getUserRequests**         | **GET** /users/{userId}/requests                      | Получение информации о заявках текущего пользователя на участие в чужих событиях             |
| **updateEvent**             | **PATCH** /users/{userId}/events/{eventId}            | Изменение события добавленного текущим пользователем                                         |

### [Тесты postman: /postman/ewm-stat-service.json](https://raw.githubusercontent.com/catarena-s/java-explore-with-me/feature_subscriptions/postman/ewm-stat-service.json)