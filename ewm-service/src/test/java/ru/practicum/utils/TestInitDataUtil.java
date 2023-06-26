package ru.practicum.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.enums.EventState;
import ru.practicum.enums.RequestStatus;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static ru.practicum.Constants.FORMATTER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestInitDataUtil {
    public static User makeUser(long id, String name, String email) {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    @NotNull
    public static List<User> getUserList() {
        return List.of(
                makeUser(1L, "Jon", "jon@mail.ru"),
                makeUser(2L, "Jane", "jane@mail.ru"),
                makeUser(3L, "Mary", "mary@mail.ru")
        );
    }

    @NotNull
    public static List<User> getUserList(UserRepository repository) {
        final User user1 = addUser(repository, "Jon", "jon@mail.ru");
        final User user2 = addUser(repository, "Jane", "jane@mail.ru");
        final User user3 = addUser(repository, "Mary", "mary@mail.ru");

        return List.of(user1, user2, user3);
    }

    @NotNull
    public static List<Event> getEventList(EventRepository repository, LocationRepository locationRepository,
                                           List<Category> categories, List<User> users) {
        final Event event1 = addEvent(repository, locationRepository,
                "Title1", "Description1", "Annotation1", categories.get(0), users.get(0));
        final Event event2 = addEvent(repository, locationRepository,
                "Title2", "Description2", "Annotation2", categories.get(1), users.get(1));
        final Event event3 = addEvent(repository, locationRepository,
                "Title3", "Description3", "Annotation3", categories.get(1), users.get(2));

        return List.of(event1, event2, event3);
    }

    private static Event addEvent(EventRepository repository, LocationRepository locationRepository,
                                  String title, String description,String annotation,
                                  Category category, User user) {
        Random r = new Random();
        final Location l = Location.builder().lat(r.nextFloat() * 10).lon(r.nextFloat() * 10).build();
        final Location location = locationRepository.save(l);
        final Event e = Event.builder()
                .title(title)
                .annotation(annotation)
                .eventDate(LocalDateTime.now())
                .createdOn(LocalDateTime.now().plusMonths(1))
                .description(description)
                .category(category)
                .initiator(user)
                .participantLimit(0)
                .location(location)
                .state(EventState.PENDING)
                .build();
        return repository.save(e);
    }

    @NotNull
    private static User addUser(UserRepository userRepository, String Jane, String mail) {
        return userRepository.save(User.builder()
                .name(Jane).email(mail)
                .build());
    }


    public static List<Category> getCategoryList() {
        return List.of(
                makeCategory(1L, "Cinema"),
                makeCategory(2L, "Theater"),
                makeCategory(3L, "Excursion")
        );
    }

    public static Category makeCategory(long id, String category) {
        return Category.builder()
                .id(id)
                .name(category)
                .build();
    }

    public static List<Category> getCategoryList(CategoryRepository repository) {
        return repository.saveAll(getCategoryList());
    }

    public static List<Request> getRequestList(RequestRepository repository, List<User> users, List<Event> events) {
        final Request request1 = makeRequest(repository, users.get(0), events.get(1), RequestStatus.PENDING);
        final Request request2 = makeRequest(repository, users.get(1), events.get(0), RequestStatus.CONFIRMED);
        final Request request3 = makeRequest(repository, users.get(2), events.get(2), RequestStatus.PENDING);
        return List.of(request1, request2, request3);
    }

    private static Request makeRequest(RequestRepository repository, User user, Event event, RequestStatus requestStatus) {
        final Request request = Request.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .status(requestStatus)
                .build();
        return repository.save(request);
    }

    public static List<Event> getEventList(List<Category> categories, List<User> users) {
        return List.of(
                makeEvent(1L, "title", "annotation", "description",
                        LocalDateTime.of(2023, 12, 1, 10, 10),
                        false, 5,
                        users.get(0),
                        new Location(1L, 15.5f, 16.6f), 2, true,
                        LocalDateTime.of(2023, 6, 1, 10, 10),
                        categories.get(0),
                        EventState.PUBLISHED
                ),
                makeEvent(2L, "title2", "annotation2", "description2",
                        LocalDateTime.of(2024, 10, 2, 10, 10),
                        true, 0,
                        users.get(1),
                        new Location(1L, 25.5f, 36.16f), 2, true,
                        LocalDateTime.of(2023, 6, 2, 10, 10),
                        categories.get(1),
                        EventState.PUBLISHED
                ),
                makeEvent(3L, "title2", "annotation2", "description2",
                        LocalDateTime.of(2024, 10, 2, 10, 10),
                        true, 0,
                        users.get(0),
                        new Location(1L, 25.5f, 36.16f), 2, true,
                        LocalDateTime.of(2023, 6, 2, 10, 10),
                        categories.get(1),
                        EventState.PENDING
                ),
                makeEvent(4L, "title4", "annotation4", "description4",
                        LocalDateTime.of(2024, 10, 2, 10, 10),
                        true, 3,
                        users.get(0),
                        new Location(1L, 25.5f, 36.16f), 2, false,
                        LocalDateTime.of(2023, 6, 2, 10, 10),
                        categories.get(1),
                        EventState.PUBLISHED
                ),
                makeEvent(5L, "title5", "annotation5", "description5",
                        LocalDateTime.of(2024, 10, 2, 10, 10),
                        true, 1,
                        users.get(0),
                        new Location(1L, 25.5f, 36.16f), 0, true,
                        LocalDateTime.of(2023, 6, 2, 10, 10),
                        categories.get(1),
                        EventState.CANCELED
                ),
                makeEvent(6L, "title5", "annotation5", "description5",
                        LocalDateTime.of(2024, 10, 2, 10, 10),
                        true, 2,
                        users.get(0),
                        new Location(1L, 25.5f, 36.16f), 2, true,
                        LocalDateTime.of(2023, 6, 2, 10, 10),
                        categories.get(1),
                        EventState.PUBLISHED
                )
        );
    }

    private static Event makeEvent(long id, String title, String annotation, String description,
                                   LocalDateTime eventDate, Boolean paid, Integer participantLimit,
                                   User initiator, Location location, Integer confirmedRequests,
                                   Boolean requestModeration,
                                   LocalDateTime publishedOn, Category category, EventState state) {
        return Event.builder()
                .id(id)
                .title(title)
                .annotation(annotation)
                .description(description)
                .eventDate(eventDate)
                .paid(paid)
                .participantLimit(participantLimit)
                .createdOn(LocalDateTime.now())
                .initiator(initiator)
                .location(location)
                .confirmedRequests(confirmedRequests)
                .requestModeration(requestModeration)
                .publishedOn(publishedOn)
                .category(category)
                .state(state)
                .build();
    }

    public static NewEventDto makeNewEventWithCorrectData() {
        return NewEventDto.builder()
                .title("Знаменитое шоу 'Летающая кукуруза'")
                .annotation("Эксклюзивность нашего шоу гарантирует привлечение максимальной зрительской аудитории")
                .description("Что получится, если соединить кукурузу и полёт? Создатели 'Шоу летающей кукурузы'" +
                        " испытали эту идею на практике и воплотили в жизнь инновационный проект, " +
                        "предлагающий свежий взгляд на развлечения...")
                .eventDate(LocalDateTime.parse("2024-12-31 15:10:05", FORMATTER))
                .paid(true)
                .participantLimit(10)
                .location(new LocationDto(37.62f, 55.754167f))
                .requestModeration(true)
                .category(2L)
                .build();
    }

    public static List<Event> getEventList() {
        return getEventList(getCategoryList(), getUserList());
    }
}
