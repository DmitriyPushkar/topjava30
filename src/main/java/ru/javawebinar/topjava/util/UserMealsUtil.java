package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
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
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

//        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
//        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public LocalTime toLocalTime(LocalDateTime localDateTime){
        return localDateTime.toLocalTime();
    }
    public LocalDate toLocalDate(LocalDateTime localDateTime){
        return localDateTime.toLocalDate();
    }
    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<UserMeal, LocalDate> filteredListByDate=new HashMap<>();
        for(UserMeal m:meals){
            filteredListByDate.put(m,m.getDateTime().toLocalDate());}
        Map<LocalDate, Integer> caloriesByDate= new HashMap<>();
        filteredListByDate.forEach((meal, date) ->
                caloriesByDate.merge(date, meal.getCalories(), Integer::sum));

        List<UserMealWithExcess> filteredList=new ArrayList<>();
        for(UserMeal m: meals){
            boolean excess=false;
            if(caloriesByDate.get(m.getDateTime().toLocalDate())>caloriesPerDay){
                excess=true;}
            if(m.getDateTime().toLocalTime().isAfter(startTime)&&m.getDateTime().toLocalTime().isBefore(endTime)){
            filteredList.add(new UserMealWithExcess(m.getDateTime(),m.getDescription(),m.getCalories(),excess));}
        }
        return filteredList;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDate=meals.stream()
                .collect(Collectors.groupingBy(UserMeal::getDate,
                        Collectors.summingInt(UserMeal::getCalories)));

        List<UserMealWithExcess> filteredList=meals.stream()
                .filter(meal->meal.getTime().isAfter(startTime)&& meal.getTime().isBefore(endTime))
                .map(meal ->(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(),meal.getCalories(),
                        caloriesByDate.get(meal.getDateTime().toLocalDate())>caloriesPerDay)))
                .toList();
        return filteredList;
    }
}
