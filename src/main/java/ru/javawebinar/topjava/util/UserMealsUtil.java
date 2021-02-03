package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410),
                new UserMeal(LocalDateTime.of(1983, Month.MARCH, 30, 20, 0), "Ужин после 18", 410),
                new UserMeal(LocalDateTime.of(1983, Month.MARCH, 30, 17, 0), "Ужин до 18", 410),
                new UserMeal(LocalDateTime.of(1983, Month.MARCH, 30, 15, 35), "Полдник", 410),
                new UserMeal(LocalDateTime.of(1983, Month.MARCH, 30, 9, 30), "Завтрак", 410),
                new UserMeal(LocalDateTime.of(1983, Month.MARCH, 30, 13, 30), "Обед", 410)
        );

        List<UserMealWithExcess> mealsToByCycles = filteredByCycles(meals, LocalTime.of(10, 0), LocalTime.of(20, 0), 2000);
        mealsToByCycles.forEach(System.out::println);
        System.out.println("__________________________________________________________________________________________");

        List<UserMealWithExcess> mealsToByStreams = filteredByStreams(meals, LocalTime.of(10, 0), LocalTime.of(20, 0), 2000);
        mealsToByStreams.forEach(System.out::println);
        System.out.println("__________________________________________________________________________________________");

        List<UserMealWithExcess> mealsToByCyclesOptional = filteredByCycleOptional(meals, LocalTime.of(10, 0), LocalTime.of(20, 0), 2000);
        mealsToByCyclesOptional.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime,
                                                            LocalTime endTime, int caloriesPerDay) {
        Map<Integer, Integer> dailyCounter = new HashMap<>();
        for (UserMeal meal : meals) {
            int hashDate = meal.getHashDate();
            dailyCounter.merge(hashDate, meal.getCalories(), Integer::sum);
        }

        List<UserMealWithExcess> filteredMealsWithExcess = new ArrayList<>();
        for (UserMeal meal : meals) {
            if ( TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime) )   {
                int hashDate = meal.getHashDate();
                boolean excess = dailyCounter.get(hashDate) > caloriesPerDay;

                filteredMealsWithExcess.add(getMealWithExcess(meal, excess));
            }
        }
        return filteredMealsWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime,
                                                             LocalTime endTime, int caloriesPerDay) {
        Map<Integer, Integer> dailyCounter = meals.stream()
                .collect(Collectors.toMap(UserMeal::getHashDate, UserMeal::getCalories, Integer::sum));

        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> getMealWithExcess(meal, dailyCounter.get(meal.getHashDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    private static UserMealWithExcess getMealWithExcess(UserMeal meal, boolean excess) {
        return new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess);
    }

    public static List<UserMealWithExcess> filteredByCycleOptional(List<UserMeal> meals, LocalTime startTime,
                                                           LocalTime endTime, int caloriesPerDay)   {
        Map<Integer, Integer> countMealsPerDay = new HashMap<>();
        Map<Integer, Integer> countCaloriesPerDay = new HashMap<>();
        Map<String, Integer> matchOfIndexes = new HashMap<>();
        List<UserMealWithExcess> result = new ArrayList<>();

        int count = 0;
        for ( UserMeal meal : meals )   {
                int hashDate = meal.getHashDate();
                countMealsPerDay.merge(hashDate, 1, Integer::sum);
                countCaloriesPerDay.merge(hashDate, meal.getCalories(), Integer::sum);
                boolean isOverDose = countCaloriesPerDay.get(hashDate) > caloriesPerDay;
                matchOfIndexes.put("" + hashDate + countMealsPerDay.get(hashDate), count);
                if ( TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime) ) {
                    result.add(getMealWithExcess(meal, isOverDose));
                    count++;
                }

                if ( isOverDose )   {
                    int index = countMealsPerDay.get(hashDate) - 1;
                    while ( index > 0 ) {
                        int value = matchOfIndexes.get("" + hashDate + index);
                        result.get(value).changeExcessAndReturn(true);
                        index--;
                    }
                }
        }
        return result;
    }
}
