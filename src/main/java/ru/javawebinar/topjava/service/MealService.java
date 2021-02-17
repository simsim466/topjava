package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFound;

@Service
public class MealService {
    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(Meal meal, int userId)   {
        return repository.save(meal, userId);
    }

    public void delete(int id, int userId)  {
        checkNotFound(repository.delete(id, userId), String.format("id %d and userId %d.", id, userId));
    }

    public Meal get(int id, int userId) {
        return checkNotFound(repository.get(id, userId), String.format("id %d and userId %d.", id, userId));
    }

    public Collection<Meal> getAll(int userId)  {
        return repository.getAll(userId);
    }

    public Collection<Meal> getFiltered(int userId, LocalTime startTime, LocalDate startDate, LocalTime endTime, LocalDate endDate) {
        return repository.getFilteredByTime(userId, startTime, startDate, endTime, endDate);
    }

    public Meal update(Meal meal, int userId)   {
        return checkNotFound(repository.save(meal, userId), String.format("with id %d and userId %d", meal.getUserId(), userId));
    }
}