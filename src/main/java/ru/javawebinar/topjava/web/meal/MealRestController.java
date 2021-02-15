package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.MealsUtil.convertToTO;
import static ru.javawebinar.topjava.util.MealsUtil.getTos;
import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private MealService service;


    public List<MealTo> getAllTOs()  {
        log.info("getAllTo");
        return getTos(service.getAll(authUserId()), authUserCaloriesPerDay());
    }

    public List<MealTo> getFilteredTOs(LocalTime startTime, LocalDate startDate, LocalTime endTime, LocalDate endDate)    {
        log.info("getFilteredTOs");
        return getTos(service.getFiltered(authUserId(), startTime, startDate, endTime, endDate), authUserCaloriesPerDay());
    }

    public void delete(int id)   {
        log.info("delete {}", id);
        service.delete(id, authUserId());
    }

    public MealTo get(int id)   {
        log.info("get by id {}", id);
        return convertToTO(service.getAll(authUserId()), service.get(id, authUserId()), authUserCaloriesPerDay());
    }

    public Meal save(Meal meal) {
        log.info("create {}", meal);
        return service.create(meal, authUserId());
    }

    public void update(Meal meal, int id)   {
        log.info("update {} with id={}", meal, id);
        assureIdConsistent(meal, id);
        service.update(meal, authUserId());
    }


}