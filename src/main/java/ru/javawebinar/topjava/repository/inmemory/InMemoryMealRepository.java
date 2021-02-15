package ru.javawebinar.topjava.repository.inmemory;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.DateTimeUtil.isBetweenHalfOpen;

public class InMemoryMealRepository implements MealRepository {
    private final static Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final static AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(this::loadUtil);
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if ( meal.isNew() ) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            repository.put(meal.getId(), meal);
            return meal;
        }
        else if ( meal.getUserId() != null && meal.getUserId() == userId )  {
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
        Meal meal = repository.get(id);
        if ( meal != null && meal.getUserId() != null && meal.getUserId() == userId )   {
            return repository.remove(id) != null;
        }

        return false;
    } //проверил

    @Override
    public Meal get(int id, int userId) {
        Meal meal = repository.get(id);
        if ( meal != null && meal.getUserId() != null
                && userId == meal.getUserId())  {
            return meal;
        }

        return null;
    } //проверил

    @Override
    public Collection<Meal> getAll(int userId) {
        return repository.values().stream()
                .filter(x -> x.getUserId() != null && x.getUserId() == userId)
                .sorted((o1, o2) -> o1.getDateTime().isAfter(o2.getDateTime()) ? 1 : o1.getDateTime().isBefore(o2.getDateTime()) ? -1 : 0)
                .collect(Collectors.toList());
    } //проверил

    @Override
    public Collection<Meal> getFilteredByTime(int userId, LocalTime startTime, LocalDate startDate, LocalTime endTime, LocalDate endDate) {

        return getAll(userId).stream()
                .filter(x -> isBetweenHalfOpen(x.getTime(), startTime, endTime) &&
                        isBetweenHalfOpen(x.getDate(), startDate, endDate))
                .collect(Collectors.toList());
    }

    public static boolean deleteExp(int id, int userId) {
         List<Meal> meals = Arrays.asList(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        Map<Integer, Meal> rep = new HashMap<>();
        int count = 0;
        for (Meal meal : meals) {
            meal.setId(count++);
            if ( meal.getId() % 2 == 0 )
                meal.setUserId(1);
            else meal.setUserId(2);

            rep.put(meal.getId(), meal);
        }

        Meal meal = rep.get(id);
        if ( meal != null && meal.getUserId() == userId )   {
            boolean res = rep.remove(id) != null;
            System.out.println(rep);
            return res;
        }
        System.out.println(rep);
        return false;
    } //удалить перед итоговым коммитом
}

