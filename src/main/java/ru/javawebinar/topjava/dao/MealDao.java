package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {
    void create(Meal meal);
    void update(Meal meal);
    void remove(int mealId);
    List<Meal> getAllMeals();
    Meal getMeal(int mealId);

}
