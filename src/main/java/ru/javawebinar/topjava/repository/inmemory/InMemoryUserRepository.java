package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/*•выполни автоформатирование (Ctrl+Alt+L), обрати внимание на отступы, пробелы
        //•counter - не надо статик, относится к конкретному экземпляру InMemoryUserRepository
        //•save -
        //1.как выполняется апдейт?
        //2.стр.35 - может быть такое, что с таким ключом уже есть запись в мапе?
        •delete, save, get - мы всё это уже делали в HW1 (только для InMemoryMealRepository), посмотри решение, можно сделать по аналогии, ничего не изобретая.
        •getAll -
        1.сортировка должна быть не в обратном порядке, а в прямом
        2.подумай, как тут можно использовать chaining comparator (https://www.baeldung.com/java-8-comparator-comparing#using-comparatorthencomparing)
        3.попробуй реализовать через Stream API, будет компактнее и проще
        •getByEmail -x - в аргументах лямбды допускаются короткие и однобуквенные имена, но они должны хотя бы намекать на тип (u, user)*/
@Repository
public class InMemoryUserRepository implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);
    private final Map<Integer, User> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public boolean delete(int id) {
        return repository.remove(id) != null;
    }

    @Override
    public User save(User user) {
        if (user != null) {
            if (user.isNew()) {
                user.setId(counter.incrementAndGet());
            }
            User associatedUser = repository.putIfAbsent(user.getId(), user);
            if (associatedUser == null) {
                log.info("save {}", user);
                return user;
            }
        }
        log.info("save processing failed");

        return null;
    }

    @Override
    public User get(int id) {
        User user = repository.get(id);
        if (user != null) {
            log.info("get {}", user);
        } else log.info("get processing failed: user with id {} is absent", id);

        return user;
    }

    @Override
    public List<User> getAll() {
        log.info("getAll");
        List<User> userList = new ArrayList<>(repository.values());
        userList.sort((user, refUser) -> {
            int result = -1 * (user.getName())
                    .compareTo(refUser.getName());

            return result != 0 ? result :
                    -1 * (user.getEmail())
                            .compareTo(refUser.getEmail());
        });

        return userList;
    }

    @Override
    public User getByEmail(String email) {
        User user = repository.values()
                .stream()
                .filter(x -> x.getEmail().equals(email))
                .findAny()
                .orElse(null);
        if (user == null) {
            log.info("getByEmail processing failed: user with email {} is absent", email);
        } else log.info("getByEmail {}", user);

        return user;
    }
}
