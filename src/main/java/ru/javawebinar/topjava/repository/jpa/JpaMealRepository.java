package ru.javawebinar.topjava.repository.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class JpaMealRepository implements MealRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        User user;
        if (meal.getUser() == null) {
            try {
                user = em.getReference(User.class, userId);
                meal.setUser(user);
            } catch (EntityNotFoundException e) {
                return null;
            }
        } else if (meal.getUser().getId() != userId) {
            return null;
        }

        if (!meal.isNew()) {
            Meal test = em.find(Meal.class, meal.getId());
            if (test != null) {
                if (test.getUser().getId() == userId) {
                    return em.merge(meal);
                } else return null;
            }
        }
        em.persist(meal);
        return meal;
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        try {
            Meal meal = em.getReference(Meal.class, id);
            if (meal.getUser().getId() == userId) {
                em.remove(meal);
                return true;
            }
            return false;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = em.find(Meal.class, id);
        if (meal == null) {
            return null;
        }
        return meal.getUser().getId() == userId ? meal : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return em.createNamedQuery(Meal.GET_ALL, Meal.class)
                .setParameter(1, userId)
                .getResultList();
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return em.createNamedQuery(Meal.GET_BETWEEN_HALF_OPEN, Meal.class)
                .setParameter(2, startDateTime)
                .setParameter(3, endDateTime)
                .setParameter(1, userId)
                .getResultList();
    }
}