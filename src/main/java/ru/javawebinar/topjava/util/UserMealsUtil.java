package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.chrono.ChronoLocalDateTime;
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
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesCountByDay = new HashMap<>();
        for (UserMeal ml: meals) {
            caloriesCountByDay.merge(ml.getDateTime().toLocalDate(),ml.getCalories(), Integer::sum);
        }

        List<UserMealWithExcess> res = new ArrayList<>();

        for (UserMeal ml:meals) {
            if (ml.getDateTime().toLocalTime().compareTo(startTime)>=0 && ml.getDateTime().toLocalTime().compareTo(endTime)<0)
                res.add(new UserMealWithExcess(ml.getDateTime(),ml.getDescription(),ml.getCalories(), caloriesCountByDay.get(ml.getDateTime().toLocalDate()) > caloriesPerDay));
        }
        return res;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> col = meals.stream().collect(Collectors.toMap(
                ml -> ml.getDateTime().toLocalDate(),
                UserMeal::getCalories, Integer::sum
        ));
        return meals.stream().
                filter(ml->ml.getDateTime().toLocalTime().compareTo(startTime)>=0 && ml.getDateTime().toLocalTime().compareTo(endTime)<0).
                map(ml1 -> new UserMealWithExcess(ml1.getDateTime(),ml1.getDescription(),ml1.getCalories(), col.get(ml1.getDateTime().toLocalDate()) > caloriesPerDay)).
                collect(Collectors.toList());
    }
}
