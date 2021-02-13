package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
//задание 1 сделано
@Repository
public class InMemoryUserRepository implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);
    private final Map<Integer, User> repository = new ConcurrentHashMap<>();

    @Override
    public boolean delete(int id) {
        User user = repository.remove(id);
        if ( user != null ) {
            log.info("delete {}", user);
            return true;
        }
        log.info("delete processing failed: user with id {} is absent", id);
        return false;
    }

    @Override
    public User save(User user) {
        if ( user != null ) {
            User associatedUser = repository.putIfAbsent(user.getId(), user);
            if ( associatedUser == null )   {
                log.info("save {}", user);
                return user;
            }
            log.info("save {} processing failed", user);
        }
        else log.info("save processing failed");

        return null;
    }

    @Override
    public User get(int id) {
        User user = repository.get(id);
        if ( user != null ) {
            log.info("get {}", user);
        }
        else log.info("get processing failed: user with id {} is absent", id);

        return user;
    }

    @Override
    public List<User> getAll() {
        log.info("getAll");
        List<User> userList = new ArrayList<>(repository.values());
        userList.sort(new Comparator<User>() {
            @Override
            public int compare(User user, User refUser) {
                int result = -1 * (user.getName())
                        .compareTo(refUser.getName());

                return result != 0 ? result :
                        -1 * (user.getEmail())
                        .compareTo(refUser.getEmail());
            }
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
        if ( user == null ) {
            log.info("getByEmail processing failed: user with email {} is absent", email);
        }
        else log.info("getByEmail {}", user);

        return user;
    }
}
