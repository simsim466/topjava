package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.MealServlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
/*
План
1. изменить MealDaoImp
    1.1 он должен изменять и пересчитывать mapOfMeals
    1.2
 */

public class MealDaoImp implements MealDao  {
    private final static Map<Integer, MealTo> mapOfMeals = new HashMap<>();
    private final static AtomicInteger id = new AtomicInteger();

    public MealDaoImp() {
        int count = 0;
        for ( MealTo meal : MealsUtil.)   {
            mapOfMeals.put(count++, meal);
        }
    }

    @Override
    public void create(Meal meal) {
        if ( meal != null ) {
            int index = id.incrementAndGet();
            meal.setId(index);
            mapOfMeals.put(index, meal);
        }
    }

    @Override
    public void update(Meal meal) {
        if ( meal != null ) {
            int index = meal.getId() != null ? meal.getId() : id.incrementAndGet();
            mapOfMeals.put(index, meal);
        }
    }

    @Override
    public void remove(int mealId) {
        mapOfMeals.remove(mealId);
    }

    @Override
    public List<Meal> getAllMeals() {
        return new ArrayList<>(mapOfMeals.values());
    }

    @Override
    public Meal getMeal(int mealId) {
        return mapOfMeals.get(mealId);
    }
}
