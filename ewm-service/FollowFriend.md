# Feature: Subscription 'Follow friends'

Подписка на друзей и возможность получать список актуальных событий, в которых они принимают участие.

    Реализованы следующие функции:
        - отправить запрос на дружбу
        - принять или отклонить полученные заявки на дружбу
        - отписаться от пользователя(удалить запрос на дружбу) 
        - получить список актуальных событий в которых будут принимать участие друзья 
        - получить список актуальных событий, опубликованных друзьями
        - получить список отправленных заявок на дружбу
        - получить список полученных заявок на дружбу
    Дополнительно:
        - пользователь может скрыть или показать события в которых будет принимать участие
        - пользователь может разрешить автоматический прием заявок на дружбу

### [Тесты postman: /postman/feature.json](https://raw.githubusercontent.com/catarena-s/java-explore-with-me/feature_subscriptions/postman/feature.json)

### Swagger API Specification
___
    Feature service - https://raw.githubusercontent.com/catarena-s/java-explore-with-me/main/ewm-service/doc/ewm_feature.json


# Описание API
- [Private Friendship API](FollowFriend.md#Private-Friendship-API)
- [Private Friends API](FollowFriend.md#Private-Friends-API)
- [Private Request API](FollowFriend.md#Private-Request-API)
- [Private User API](FollowFriend.md#Private-User-API)
___
## ***Private Friendship API***
Приватный API для работы с запросами на дружбу

| HTTP request                                                  | Method                                                                             | Description                                                          |
|---------------------------------------------------------------|------------------------------------------------------------------------------------|----------------------------------------------------------------------|
| **PATCH** /users/{userId}/friendships/approve?ids={ids}       | [**approveFriendship**](FollowFriend.md#approveFriendship)                         | Подтверждение полученных текущим пользователем запросов на дружбу    |
| **DELETE** /users/{userId}/friendships/{subsId}               | [**deleteFriendshipRequest**](FollowFriend.md#deleteFriendshipRequest)             | Удалить запрос на дружбу от текущего пользователя                    |
| **GET** /users/{userId}/friendships/requests?filter={filter}  | [**getFriendshipRequests**](FollowFriend.md#getFriendshipRequests)                 | Получить список заявок на дружбу, отправленных текущим пользователем |
| **GET** /users/{userId}/friendships/followers?filter={filter} | [**getIncomingFriendshipRequests**](FollowFriend.md#getIncomingFriendshipRequests) | Получить список заявок на дружбу, полученных текущим пользователем   |
| **PATCH** /users/{userId}/friendships/reject?ids={ids}        | [**rejectFriendship**](FollowFriend.md#rejectFriendship)                           | Отклонение запросов на дружбу полученных текущи пользователем        |
| **POST** /users/{userId}/friendships/{friendId}               | [**requestFriendship**](FollowFriend.md#requestFriendship)                         | Добавление запроса на дружбу от текущего пользователя                |

<a name="approveFriendship"></a>
# **approveFriendship**
> List&lt;FriendshipShortDto&gt; approveFriendship(userId, ids)

Подтверждение полученных запросов на дружбу текущим пользователем
- Подтвердить можно только запросы в ожидании(Ожидается код ошибки 409)

### Parameters

| Name       | Type           | Description                               | Notes              |
|------------|----------------|-------------------------------------------|--------------------|
| **userId** | **Long**       | id текущего пользователя                  |                    |
| **ids**    | **List<Long>** | список идентификаторов запросов на дружбу | [default to [1,2]] |

### Return type

**List&lt;FriendshipShortDto&gt;**

<a name="deleteFriendshipRequest"></a>
### **deleteFriendshipRequest**
> deleteFriendshipRequest(userId, subsId)

Удалить запрос на дружбу от текущего пользователя

### Parameters

| Name       | Type     | Description              |
|------------|----------|--------------------------|
| **userId** | **Long** | id текущего пользователя |
| **subsId** | **Long** | id запроса на дружбу     |

### Return type

null (empty response body)

<a name="getFriendshipRequests"></a>
### **getFriendshipRequests**
> List&lt;FriendshipShortDto&gt; getFriendshipRequests(userId, filter)

Получить список заявок на дружбу, отправленных текущим пользователем

В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список

### Parameters

| Name       | Type       | Description              | Notes                                                     |
|------------|------------|--------------------------|-----------------------------------------------------------|
| **userId** | **Long**   | id текущего пользователя |                                                           |
| **filter** | **String** | filter                   | [default to ALL] [enum: ALL, APPROVED, PENDING, REJECTED] |

### Return type

[**List&lt;FriendshipShortDto&gt;**](doc/FriendshipShortDto.md)

<a name="getIncomingFriendshipRequests"></a>
### **getIncomingFriendshipRequests**
> List&lt;FriendshipShortDto&gt; getIncomingFriendshipRequests(userId, filter)

Получить список заявок на дружбу, полученных текущим пользователем

В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список

### Parameters

| Name       | Type       | Description              | Notes                                                     |
|------------|------------|--------------------------|-----------------------------------------------------------|
| **userId** | **Long**   | id текущего пользователя |                                                           |
| **filter** | **String** | filter                   | [default to ALL] [enum: ALL, APPROVED, PENDING, REJECTED] |

### Return type

[**List&lt;FriendshipShortDto&gt;**](doc/FriendshipShortDto.md)

<a name="rejectFriendship"></a>
### **rejectFriendship**
> List&lt;FriendshipShortDto&gt; rejectFriendship(userId, ids)

Отклонение запросов на дружбу полученных текущи пользователем

- Отклонить можно только запросы в состоянии PENDING или APPROVED (Ожидается код ошибки 409)

### Parameters

| Name       | Type           | Description                               | Notes              |
|------------|----------------|-------------------------------------------|--------------------|
| **userId** | **Long**       | id текущего пользователя                  |                    |
| **ids**    | **List<Long>** | список идентификаторов запросов на дружбу | [default to [1,2]] |

### Return type

**List&lt;FriendshipShortDto&gt;**

<a name="requestFriendship"></a>
### ***requestFriendship***
> FriendshipDto requestFriendship(userId, friendId)

Добавление запроса на дружбу от текущего пользователя

Примечание:
- дружба не взаимная
- нельзя добавить повторный запрос если текущий статус PENDING или APPROVED (Ожидается код ошибки 409)
- нельзя подписаться на самого себя (Ожидается код ошибки 409)
- если для пользователя отключена пре-модерация запросов на дружбу, то запрос должен автоматически перейти в состояние подтвержденного

### Parameters

| Name         | Type     | Description              |
|--------------|----------|--------------------------|
| **userId**   | **Long** | id текущего пользователя |
| **friendId** | **Long** | id друга                 |

### Return type

[**FriendshipDto**](/doc/FriendshipDto.md)

___
## ***Private Friends API***

| HTTP request                                                   | Method                                                           | Description                                                               |
|----------------------------------------------------------------|------------------------------------------------------------------|---------------------------------------------------------------------------|
| **GET** /users/{userId}/friends/share?from={from}&size={size}  | [**getParticipateEvents**](FollowFriend.md#getParticipateEvents) | Получить события в которых будут участвовать друзья текущего пользователя |
| **GET** /users/{userId}/friends/events?from={from}&size={size} | [**getFriendEvents**](FollowFriend.md#getFriendEvents)           | Получить список событий опубликованных друзьями текущего пользователя     |
| **GET** /users/{userId}/friends                                | [**getFriends**](FollowFriend.md#getFriends)                     | Получить список друзей текущего пользователя                              |
| **GET** /users/{userId}/followers                              | [**getFollowers**](FollowFriend.md#getFollowers)                 | Получить список подписчиков текущего пользователя                         |

<a name="getParticipateEvents"></a>
### **getParticipateEvents**
> List&lt;EventShortDto&gt; getParticipateEvents(userId, from, size)

Получить события в которых будут участвовать друзья текущего пользователя
- событие дата которого начинается не ранее чем через 2 часа после текущего момента

В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список

### Parameters

| Name       | Type        | Description                                                                     | Notes                               |
|------------|-------------|---------------------------------------------------------------------------------|-------------------------------------|
| **userId** | **Long**    | id текущего пользователя                                                        |                                     |
| **from**   | **Integer** | количество элементов, которые нужно пропустить для формирования текущего набора | [optional] [default to 0] [enum: 0] |
| **size**   | **Integer** | количество элементов в наборе                                                   | [optional] [default to 10]          |

### Return type

**List&lt;EventShortDto&gt;**

### ***getFriendEvents***
<a name="getFriendEvents"></a>
> List&lt;EventShortDto&gt; getFriendEvents(userId, from, size)

Получить список событий опубликованных друзьями текущего пользователя
- событие время которого начинается не ранее чем через 2 часа после текущего момента

В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список

### Parameters

| Name       | Type        | Description                                                                     | Notes                               |
|------------|-------------|---------------------------------------------------------------------------------|-------------------------------------|
| **userId** | **Long**    | id текущего пользователя                                                        |                                     |
| **from**   | **Integer** | количество элементов, которые нужно пропустить для формирования текущего набора | [optional] [default to 0] [enum: 0] |
| **size**   | **Integer** | количество элементов в наборе                                                   | [optional] [default to 10]          |

### Return type

**List&lt;EventShortDto&gt;**

<a name="getFriends"></a>
### **getFriends**
> List&lt;UserDto&gt; getFriends(userId)

Получить список друзей текущего пользователя
- заявки в статусе APPROVE В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список

### Parameters

| Name       | Type     | Description              |
|------------|----------|--------------------------|
| **userId** | **Long** | id текущего пользователя |

### Return type

**List&lt;UserDto&gt;**

<a name="getfollowers"></a>
### **getFollowers**
> List&lt;UserDto&gt; getFollowers(userId)

Получить список подписчиков текущего пользователя
- заявки в статусе APPROVE

В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список

### Parameters

| Name       | Type     | Description              |
|------------|----------|--------------------------|
| **userId** | **Long** | id текущего пользователя |

### Return type

**List&lt;UserDto&gt;**
___
## ***Private Request API***
Приватный API для работы с запросами на участии в событиях

| HTTP request                                      | Method                                                     | Description              |
|---------------------------------------------------|------------------------------------------------------------|--------------------------|
| **PATCH** /users/{userId}/requests/hide?ids={ids} | [**hideParticipation**](doc/FollowFriend.md#hideParticipation) | Скрыть события от друзей |
| **PATCH** /users/{userId}/requests/show?ids={ids} | [**showParticipation**](doc/FollowFriend.md#showParticipation) | Показать события друзьям |

<a name="hideParticipation"></a>
### **hideParticipation**
> List&lt;ParticipationRequestDto&gt; hideParticipation(userId, ids)

Скрыть события от друзей
- Изменить видимость можно только у подтвержденных запросов на участие 
- Изменять можно видимость событий в которых участвует текущий пользователь


### Parameters

| Name       | Type                 | Description              |
|------------|----------------------|--------------------------|
| **userId** | **Long**             | id текущего пользователя |
| **ids**    | **List&lt;Long&gt;** | массив id событий        |

### Return type

***List&lt;ParticipationRequestDto&gt;***

<a name="showParticipation"></a>
### **showParticipation**
> List&lt;ParticipationRequestDto&gt; showParticipation(userId, ids)

Скрыть события от друзей
- Изменить видимость можно только у подтвержденных запросов на участие 
- Изменять можно видимость событий в которых участвует текущий пользователь


### Parameters

| Name       | Type                 | Description              |
|------------|----------------------|--------------------------|
| **userId** | **Long**             | id текущего пользователя |
| **ids**    | **List&lt;Long&gt;** | массив id событий        |

### Return type

**List&lt;ParticipationRequestDto&gt;**

___
## ***Privete User API***
Приватный API для работы с пользователями

| HTTP request                               | Method                                                         | Description             |
|--------------------------------------------|----------------------------------------------------------------|-------------------------|
| **PATCH** /users/{userId}/subs?auto={auto} | [**changeSubscribeMode**](FollowFriend.md#changeSubscribeMode) | Изменить режим подписки |

<a name="changeSubscribeMode"></a>
### **changeSubscribeMode**
> List&lt;UserDto&gt; changeSubscribeMode(userId, auto)

Изменить режим подтверждения подписки

### Parameters

| Name       | Type        | Description                                                        | Notes              |
|------------|-------------|--------------------------------------------------------------------|--------------------|
| **userId** | **Long**    | id текущего пользователя                                           |                    |
| **auto**   | **Boolean** | режим подписки(true - автоматическое принятие запроса на подписку) | [default to false] |

### Return type

**List&lt;UserDto&gt;**
