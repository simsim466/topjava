package ru.javawebinar.topjava.web;

import ru.javawebinar.topjava.dao.MealDaoImp;
import ru.javawebinar.topjava.model.Meal;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MealController extends HttpServlet {
    private static String UPDATE_OR_EDIT = "/edit_meal.jsp";
    private static String LIST_USER = "/meals.jsp";
    private MealDaoImp dao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String forward="";
        String action = req.getParameter("action");

        if (action.equalsIgnoreCase("delete"))  {
            int mealId = Integer.parseInt(req.getParameter("userId"));
            dao.remove(mealId);
            forward = LIST_USER;
            req.setAttribute("meals", dao.getAllMeals());
        }
        else if (action.equalsIgnoreCase("edit"))   {
            forward = UPDATE_OR_EDIT;
            int userId = Integer.parseInt(req.getParameter("userId"));
            Meal meal = dao.getMeal(userId);
            req.setAttribute("user", meal);
        } else if (action.equalsIgnoreCase("listUser")){
            forward = LIST_USER;
            req.setAttribute("users", dao.getAllMeals());
        } else {
            forward = UPDATE_OR_EDIT;
        }

        RequestDispatcher view = req.getRequestDispatcher(forward);
        view.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Meal meal = new Meal();

        LocalDateTime dateTime = LocalDateTime.parse(req.getParameter("dateTime"), DateTimeFormatter.ofPattern("u-MM-d H:m"));
        meal.setDateTime(dateTime);
        meal.setDescription(req.getParameter("description"));
        meal.setCalories(Integer.parseInt(req.getParameter("calories")));

        String userid = req.getParameter("userid");
        if ( userid == null || userid.isEmpty() )   {
            dao.create(meal);
        }
        else {
            meal.setId(Integer.parseInt(userid));
            dao.update(meal);
        }
        RequestDispatcher view = req.getRequestDispatcher(LIST_USER);
        req.setAttribute("users", dao.getAllMeals());
        view.forward(req, resp);
    }
}
