# java-explore-with-me

**Pull request**: https://github.com/catarena-s/java-explore-with-me/pull/5
### Фича: Подписки(вариант 2).
Подписка на друзей и возможность получать список актуальных событий, в которых они принимают участие.

### [Описание фичи](ewm-service/FollowFriend.md)

### Схема БД
___
![](ewm-service/doc/DB.png)

### Docker start-up guide
___
    mvn clean package
    docker-compose up -d

    Main service: http://localhost:8080
    Statistic service: http://localhost:9090
