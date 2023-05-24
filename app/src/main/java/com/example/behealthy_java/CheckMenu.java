package com.example.behealthy_java;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class CheckMenu {
    SQLiteDatabase db;
    Double proteins_deviation, fats_deviation, carbo_deviation, deviation;
    List product_array;
    Map ID_deviation = new HashMap<List,Double>(); //словарь, в нем айди и суммарное отклонение от нормы
    Map b_vicinity = new HashMap<List,Double>(), l_vicinity = new HashMap<List,Double>(), d_vicinity = new HashMap<List,Double>();
    //айди + кбжу
    Map calories = new HashMap<Integer,Double>();
    Map proteins = new HashMap<Integer,Double>();
    Map fats = new HashMap<Integer,Double>();
    Map carbo = new HashMap<Integer,Double>();
    List final_meal;
    User user;

    public void CheckMeal(){

        List all_meals_array = SelectMealFromDBUpdated(this.product_array);
        List breakfast_meals_array = ListOfMealsByGroup(1, all_meals_array);
        List lunch_meals_array = ListOfMealsByGroup(2, all_meals_array);
        List dinner_meals_array = ListOfMealsByGroup(3, all_meals_array);

        //рандомный выбор трех блюд
        Integer breakfast = SelectMealFromArray(breakfast_meals_array);
        Integer lunch = SelectMealFromArray(lunch_meals_array);
        Integer dinner = SelectMealFromArray(dinner_meals_array);

        CallPFCofMeal(breakfast, lunch, dinner); //вставляем в словари айдишники + значение КБЖУ

        //записываем все девиации подобранного меню, в т.ч. суммарную
        WriteDeviations(ID_deviation, breakfast, lunch, dinner);
        //System.out.println("Значение по массиву: " + ID_deviation.get(Arrays.asList(breakfast, lunch, dinner)));

        //три окрестности с заменами завтрака, обеда и ужина соответственно
        CreateVicinity(b_vicinity, breakfast_meals_array, Arrays.asList(breakfast, lunch, dinner), "1");
        CreateVicinity(l_vicinity, lunch_meals_array, Arrays.asList(breakfast, lunch, dinner), "2");
        CreateVicinity(d_vicinity, dinner_meals_array, Arrays.asList(breakfast, lunch, dinner), "3");
        //ТУТ БУДЕТ VNS

        //локальные минимумы каждой окрестности собираем в четвертую (последнюю) окрестность
        SelectBestMenu(b_vicinity);
        SelectBestMenu(l_vicinity);
        SelectBestMenu(d_vicinity);

        final_meal = FinalSearch(ID_deviation);
    }

    public List FinalSearch(Map map){
        List best_choice = new ArrayList<>();
        double dev = 10000.0;
        List b = new ArrayList<>();
        List l = new ArrayList<>();
        List d = new ArrayList<>();

        for(Object key: map.keySet()){
            if (Double.parseDouble(String.valueOf(map.get(key))) < dev) {
                dev = Double.parseDouble(String.valueOf(map.get(key)));
                best_choice.clear();
                best_choice = (List) key;
            }
        }

        System.out.println("Массив лучших выборов = " + best_choice);
        System.out.println("Индексы блюд: " + best_choice.get(0) + " " + best_choice.get(1) + " " + best_choice.get(2));

        return best_choice;
    }

    public void SelectBestMenu(Map map){
        List best_choice = new ArrayList<>();
        double dev = 10000.0;
        for(Object key: map.keySet()){
            System.out.println(key);
            if (Double.parseDouble(String.valueOf(map.get(key))) < dev) {
                dev = Double.parseDouble(String.valueOf(map.get(key)));
                best_choice.clear();
                best_choice = (List) key;
            }
        }
        System.out.println("Лучший прием пищи: " + best_choice);
        System.out.println("Девиация: " + dev);
        ID_deviation.put(best_choice, dev);
    }

    //создаем окрестности для локального поиска
    public void CreateVicinity(Map map, List meal_array, List first_combination, String group_meal){

        int m = 1, n = 2 , s = 0; //0 = завтрак, 1 = обед, 2 = ужин
        if (group_meal.equals("3")) {m = 0; n = 1; s = 2;}
        else if (group_meal.equals("2")) {m = 0; n = 2; s = 1;}


        for (int i=0; i<meal_array.size(); i++){
            List arr = new ArrayList<>();
            arr.add(0);
            arr.add(0);
            arr.add(0);

            arr.set(m, Integer.valueOf(String.valueOf(first_combination.get(m))));
            arr.set(n, Integer.valueOf(String.valueOf(first_combination.get(n))));
            arr.set(s, Integer.valueOf(String.valueOf(meal_array.get(i))));

            Integer breakfast = (Integer) arr.get(0);
            Integer lunch = (Integer) arr.get(1);
            Integer dinner = (Integer) arr.get(2);

            CallPFCofMeal(breakfast, lunch, dinner);
            WriteDeviations(map, breakfast, lunch, dinner);
        }
        System.out.println("Созданный словарь группы " + group_meal + ": "  + map);
    }

    //рандомно выбираем блюдо из предложенных
    public Integer SelectMealFromArray(List meals){
        Integer ID_meal = null;
        int b = new Random().nextInt(meals.size());
        ID_meal = Integer.valueOf((String) meals.get(b));
        return ID_meal;
    }

    //делаем массив завтраков/обедов/ужинов из общего массива блюд
    public List ListOfMealsByGroup(Integer group_meal, List array){
        List grouped_meals_array = new ArrayList<>(); //сюда запишем блюда конкретного приема пищи
        //где-то тут мб лучше посчитать сколько раз блюдо входит в массив, если =>2,
        // то в блюде используются нужные продукты =>2 раз => можно включить в множество.
        Set set_of_meals = new HashSet(array); //тут
        List list_from_set = new ArrayList();//и тут
        list_from_set.addAll(set_of_meals);//и тут делаем массив уникальных значений блюд

        for (int i=0; i<list_from_set.size(); i++){
            String select_grouped_meal = "SELECT _idMeal FROM MEALS_MAIN WHERE IdGroupMeal = '" + group_meal + "' AND _idMeal = '" + list_from_set.get(i) + "'";
            Cursor select_grouped_crs = db.rawQuery(select_grouped_meal, null);
            while (select_grouped_crs.moveToNext()){
                grouped_meals_array.add(select_grouped_crs.getString(select_grouped_crs.getColumnIndexOrThrow ("_idMeal")));
            }
        }
        return grouped_meals_array;
    }

    //массив блюд, в которых есть продукты, введенные пользователем (блюда повторяются, не отсортированы)
    public List SelectMealFromDBUpdated(List array){
        List included_meals_array = new ArrayList<>();
        for (int i=0; i<array.size(); i++) {
            //достаем айди продукта по названию
            String id_of_product = "SELECT _idProduct FROM PRODUCTS2 WHERE ProductName = '" + array.get(i) + "'";
            Cursor product_crs = db.rawQuery(id_of_product, null);
            Integer int_id_product = null;
            while (product_crs.moveToNext()) {
                int_id_product = product_crs.getInt(product_crs.getColumnIndexOrThrow("_idProduct"));
            }
            //заносим айди блюда с данным продуктом в массив включенных в рацион блюд
            String include_str = "SELECT IdMeal FROM MEALS_STRUCTURE WHERE IdProduct = '" + int_id_product + "'";
            Cursor included_product_crs = db.rawQuery(include_str, null);
            Integer included_meal = null;
            while (included_product_crs.moveToNext()) {
                included_meal = included_product_crs.getInt(included_product_crs.getColumnIndexOrThrow("IdMeal"));
                included_meals_array.add(included_meal);
            }
        }
        return included_meals_array;
    }

    public void CallPFCofMeal(Integer breakfast, Integer lunch, Integer dinner){
        putInMap(calories, breakfast, "Calories");
        putInMap(calories, lunch, "Calories");
        putInMap(calories, dinner, "Calories");

        putInMap(proteins, breakfast, "Proteins");
        putInMap(proteins, lunch, "Proteins");
        putInMap(proteins, dinner, "Proteins");

        putInMap(fats, breakfast, "Fats");
        putInMap(fats, lunch, "Fats");
        putInMap(fats, dinner, "Fats");

        putInMap(carbo, breakfast, "Carbo");
        putInMap(carbo, lunch, "Carbo");
        putInMap(carbo, dinner, "Carbo");
    }

    //ищем КБЖУ
    public Double PFCofMeal(Integer id_meal, String PFC){
        Double PFCofMeal = null;
        String select_calories = "SELECT " + PFC + " FROM MEALS_MAIN WHERE _idMeal = '" + id_meal + "'";
        Cursor PFC_crs = db.rawQuery(select_calories, null);
        while(PFC_crs.moveToNext()){
            PFCofMeal = Double.valueOf(PFC_crs.getString(PFC_crs.getColumnIndexOrThrow(PFC)));
        }
        return PFCofMeal;
    }

    //вставляем в словарь
    public void putInMap(Map CPFC, Integer meal, String CPFC_str){
        CPFC.put(meal, PFCofMeal(meal, CPFC_str));
    }

    //коэффицент для умножения КБЖУ
    public Double CountCoefOfMeal(Double calories, Integer idMeal){
        Double gramms = null;
        switch (idMeal){
            case 1:
                gramms = (user.CaloriesNorm * 0.3)/calories;
                break;
            case 2:
                gramms = (user.CaloriesNorm * 0.45)/calories;
                break;
            case 3:
                gramms = (user.CaloriesNorm * 0.25)/calories;
                break;
        }
        Double rounded_gramms = Math.round(gramms * 100.00)/100.00;
        return rounded_gramms;
    }

    public void WriteDeviations(Map map, Integer breakfast, Integer lunch, Integer dinner){
        //считаем коэффиценты
        Double b_coef = CountCoefOfMeal((Double) calories.get(breakfast), 1);
        Double l_coef = CountCoefOfMeal((Double) calories.get(lunch), 2);
        Double d_coef = CountCoefOfMeal((Double) calories.get(dinner), 3);

        Double check_proteins = (Double) proteins.get(breakfast)*b_coef + (Double) proteins.get(lunch)*l_coef
                + (Double) proteins.get(dinner)*d_coef;

        if (check_proteins>=user.ProteinsNorm){
            proteins_deviation = Double.valueOf(0);
        } else {
            proteins_deviation = user.ProteinsNorm-check_proteins;
        }

        Double check_fats = (Double) fats.get(breakfast)*b_coef + (Double) fats.get(lunch)*l_coef
                + (Double) fats.get(dinner)*d_coef;

        if (check_fats<=user.FatsNorm){
            fats_deviation = Double.valueOf(0);
        } else {
            fats_deviation = user.FatsNorm-check_fats;
        }

        Double check_carbo = (Double) carbo.get(breakfast)*b_coef + (Double) carbo.get(lunch)*l_coef
                + (Double) carbo.get(dinner)*d_coef;


        if (check_carbo<=user.CarboNorm){
            carbo_deviation = Double.valueOf(0);
        } else {
            carbo_deviation = user.CarboNorm-check_carbo;
        }

        deviation = Math.abs(carbo_deviation) + Math.abs(proteins_deviation) + Math.abs(fats_deviation);

        List this_menu_list = new ArrayList<>();
        this_menu_list.add(breakfast);
        this_menu_list.add(lunch);
        this_menu_list.add(dinner);

        map.put(this_menu_list, deviation);
    }
}
