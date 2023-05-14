package com.example.behealthy_java;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MenuRoomActivity extends AppCompatActivity {

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Double CaloriesNorm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_room);
        Bundle arguments = getIntent().getExtras();
        User user = (User) arguments.get("user");
        List<String> products_array = (List<String>) arguments.get("products_array");
        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.open();

        System.out.println("У меня есть массив " + products_array);
        System.out.println("Мое имя из MenuRoomActivity " + user.name);
        CaloriesNorm = user.CaloriesNorm;

        //выбор трех блюд
        Integer first_meal = SelectMealFromDB(1);
        Integer second_meal = SelectMealFromDB(2);
        Integer third_meal = SelectMealFromDB(3);

        //запоминаем ккал блюда
        Double first_meal_calories = GetCaloriesOfMeal(first_meal);
        Double second_meal_calories = GetCaloriesOfMeal(second_meal);
        Double third_meal_calories = GetCaloriesOfMeal(third_meal);

        //считаем граммы на нужную нам норму
        Double first_meal_gramms = CountGrammOfMeal(first_meal_calories, 1);
        Double second_meal_gramms = CountGrammOfMeal(second_meal_calories, 2);
        Double third_meal_gramms = CountGrammOfMeal(third_meal_calories, 3);

        Double firs_meal_proteins = ProteinsOfMeal(first_meal);
        Double second_meal_proteins = ProteinsOfMeal(second_meal);
        Double third_meal_proteins = ProteinsOfMeal(third_meal);

        Double firs_meal_fats = FatsOfMeal(first_meal);
        Double second_meal_fats = FatsOfMeal(second_meal);
        Double third_meal_fats = FatsOfMeal(third_meal);

        Double firs_meal_carbo = CarboOfMeal(first_meal);
        Double second_meal_carbo = CarboOfMeal(second_meal);
        Double third_meal_carbo = CarboOfMeal(third_meal);

        System.out.println("Норма ккал пользователя: " + CaloriesNorm);
        System.out.println("Завтрак: " + first_meal_calories + " ккал на 100 гр, "+firs_meal_proteins+" гр белка, "+ firs_meal_fats +" гр жиров, "+firs_meal_carbo+" гр углеводов; нужно съесть " + first_meal_gramms + " грамм на завтрак, название блюда: " + GetMealName(first_meal));
        System.out.println("Обед: " + second_meal_calories + " ккал на 100 гр; нужно съесть " + second_meal_gramms + " грамм на обед, название блюда: " + GetMealName(second_meal));
        System.out.println("Ужин: " + third_meal_calories + " ккал на 100 гр; нужно съесть " + third_meal_gramms + " грамм на ужин, название блюда: " + GetMealName(third_meal));

    }

    public Integer SelectMealFromDB(Integer group_meal){

        String select_count_of_food = "SELECT _idMeal FROM MEALS_MAIN WHERE IdGroupMeal = '" + group_meal + "'";
        Cursor select_count_of_food_crs = db.rawQuery(select_count_of_food, null);
        List array = new ArrayList();
        while (select_count_of_food_crs.moveToNext()){
            array.add(select_count_of_food_crs.getString(select_count_of_food_crs.getColumnIndexOrThrow ("_idMeal")));
        }
        System.out.println(array);
        //int a = (int) (Math.random()*(array.size())) + 1;
        int b = new Random().nextInt(array.size());
        Integer ID_meal = Integer.valueOf((String) array.get(b));

        return ID_meal;
    }

    public Integer SelectMealFromDBUpdated(Integer group_meal){

        String select_count_of_food = "SELECT _idMeal FROM MEALS_MAIN WHERE IdGroupMeal = '" + group_meal + "'";
        Cursor select_count_of_food_crs = db.rawQuery(select_count_of_food, null);
        List array = new ArrayList();
        while (select_count_of_food_crs.moveToNext()){
            array.add(select_count_of_food_crs.getString(select_count_of_food_crs.getColumnIndexOrThrow ("_idMeal")));
        }
        System.out.println(array);
        //int a = (int) (Math.random()*(array.size())) + 1;
        int b = new Random().nextInt(array.size());
        Integer ID_meal = Integer.valueOf((String) array.get(b));

        return ID_meal;
    }

    public Double GetCaloriesOfMeal(Integer id_meal){
        Double calories = null;

        String select_calories = "SELECT Calories FROM MEALS_MAIN WHERE _idMeal = '" + id_meal + "'";
        Cursor calories_crs = db.rawQuery(select_calories, null);
        while(calories_crs.moveToNext()){
            calories  = Double.valueOf(calories_crs.getString(calories_crs.getColumnIndexOrThrow ("Calories")));
        }

        return calories;
    }

    public Double CountGrammOfMeal(Double calories, Integer idMeal){
        Double gramms = null;

        switch (idMeal){
            case 1:
                gramms = (CaloriesNorm * 0.3)/calories;
                break;
            case 2:
                gramms = (CaloriesNorm * 0.45)/calories;
                break;
            case 3:
                gramms = (CaloriesNorm * 0.25)/calories;
                break;
        }

        gramms = gramms*100;
        Double rounded_gramms = Math.round(gramms * 100.00)/100.00;
        return rounded_gramms;
    }

    public String GetMealName(Integer id_meal) {

        String select_calories = "SELECT NameMeal FROM MEALS_MAIN WHERE _idMeal = '" + id_meal + "'";
        Cursor calories_crs = db.rawQuery(select_calories, null);
        String meal_name = null;
        while (calories_crs.moveToNext()) {
            meal_name = calories_crs.getString(calories_crs.getColumnIndexOrThrow("NameMeal"));
        }

        return meal_name;
    }

    //переписать в одну функцию подсчет БЖУ
    /*public Double PFCofMeal(Integer id_meal, String PFC){

    }*/

    public Double ProteinsOfMeal(Integer id_meal){
        Double proteins = null;
        String select_calories = "SELECT Proteins FROM MEALS_MAIN WHERE _idMeal = '" + id_meal + "'";
        Cursor calories_crs = db.rawQuery(select_calories, null);
        while(calories_crs.moveToNext()){
            proteins  = Double.valueOf(calories_crs.getString(calories_crs.getColumnIndexOrThrow ("Proteins")));
        }
        return proteins;
    }

    public Double FatsOfMeal(Integer id_meal){
        Double fats = null;
        String select_calories = "SELECT Fats FROM MEALS_MAIN WHERE _idMeal = '" + id_meal + "'";
        Cursor calories_crs = db.rawQuery(select_calories, null);
        while(calories_crs.moveToNext()){
            fats  = Double.valueOf(calories_crs.getString(calories_crs.getColumnIndexOrThrow ("Fats")));
        }
        return fats;
    }

    public Double CarboOfMeal(Integer id_meal){
        Double carbo = null;
        String select_calories = "SELECT Carbo FROM MEALS_MAIN WHERE _idMeal = '" + id_meal + "'";
        Cursor calories_crs = db.rawQuery(select_calories, null);
        while(calories_crs.moveToNext()){
            carbo  = Double.valueOf(calories_crs.getString(calories_crs.getColumnIndexOrThrow ("Carbo")));
        }
        return carbo;
    }
}