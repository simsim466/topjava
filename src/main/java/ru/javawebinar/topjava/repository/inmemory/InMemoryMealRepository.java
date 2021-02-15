package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.DateTimeUtil.isBetweenHalfOpen;
@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);
    private final static Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final static AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(this::loadUtil);
    }

    @Override
    public Meal save(Meal meal, int userId) {
        log.info("saveMeal for user {}", userId);
        if ( meal.isNew() ) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            repository.put(meal.getId(), meal);
            return meal;
        }
        else if ( meal.getUserId() == userId )  {
            return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
        }

        return null;
    }

    @Override
    public Meal loadUtil(Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }

        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        log.info("deleteMeal with id {} for user {}",id, userId);
        Meal meal = repository.get(id);
        if ( meal != null && !meal.isNew() && meal.getUserId() == userId )   {
            return repository.remove(id) != null;
        }

        return false;
    }

    @Override
    public Meal get(int id, int userId) {
        log.info("getMeal with id {} for user {}",id, userId);
        Meal meal = repository.get(id);
        if ( meal != null && !meal.isNew()
                && userId == meal.getUserId())  {
            return meal;
        }

        return null;
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        log.info("getAll for user {}", userId);
        return repository.values().stream()
                .filter(x -> x.getUserId() != null && x.getUserId() == userId)
                .sorted((o1, o2) -> o1.getDateTime().isAfter(o2.getDateTime()) ? 1 : o1.getDateTime().isBefore(o2.getDateTime()) ? -1 : 0)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Meal> getFilteredByTime(int userId, LocalTime startTime, LocalDate startDate, LocalTime endTime, LocalDate endDate) {
        log.info("getFilteredByTime for user {}", userId);
        return getAll(userId).stream()
                .filter(x -> isBetweenHalfOpen(x.getTime(), startTime, endTime) &&
                        isBetweenHalfOpen(x.getDate(), startDate, endDate))
                .collect(Collectors.toList());
    }
}

