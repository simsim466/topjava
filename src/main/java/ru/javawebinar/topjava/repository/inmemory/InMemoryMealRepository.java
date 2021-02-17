package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.DateTimeUtil.isBetweenHalfOpen;

/*
5.потестируй работу метода с фильтрацией (пока в SpringMain) на данных из MealsUtil.
а) конечная дата должна попадать в отбор (т.е. по дате фильтр "включая", см.демо)
б) попробуй отфильтровать с 31.01.2020 00:00 - 31.01.2020 23:00 и  с 31.01.2020 10:00 - 31.01.2020 11:00. Проследи за excess в еде за 31.01.2020 10:00. То, что юзер переел за день никак не должно зависеть от установленного фильтра по времени.
 */
@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);
    private final Map<Integer, Map<Integer, Meal>> repoAdvanced = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, 1));
    }

    @Override
    public synchronized Meal save(Meal meal, int userId) {
        log.info("saveMeal for user {}", userId);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
        }

        Map<Integer, Meal> map = repoAdvanced.get(userId);
        if (map != null) {
            map.put(meal.getId(), meal);
            repoAdvanced.put(userId, map);
            return meal;
        }

        return null;
    }

    @Override
    public synchronized boolean delete(int id, int userId) {
        log.info("deleteMeal with id {} for user {}", id, userId);
        Map<Integer, Meal> map = repoAdvanced.get(userId);
        if (map != null) {
            Meal meal = map.get(id);
            if (meal != null) {
                map.remove(id);
                repoAdvanced.put(userId, map);
                return true;
            }
        }

        return false;
    }

    @Override
    public synchronized Meal get(int id, int userId) {
        log.info("getMeal with id {} for user {}", id, userId);
        return repoAdvanced.get(userId).get(id);
    }

    @Override
    public synchronized Collection<Meal> getAll(int userId) {
        log.info("getAll for user {}", userId);
        return getFiltered(userId, meal -> true);
    }

    @Override
    public synchronized Collection<Meal> getFilteredByTime(int userId, LocalTime startTime, LocalDate startDate, LocalTime endTime, LocalDate endDate) {
        log.info("getFilteredByTime for user {}", userId);
        return getFiltered(userId, x -> isBetweenHalfOpen(x.getTime(), startTime, endTime) &&
                isBetweenHalfOpen(x.getDate(), startDate, endDate));
    }

    private Collection<Meal> getFiltered(int userId, Predicate<Meal> filter) {
        return repoAdvanced.get(userId)
                .values()
                .stream()
                .filter(filter)
                .sorted((m1, m2) -> m2.getDateTime().compareTo(m1.getDateTime()))
                .collect(Collectors.toList());
    }
}

