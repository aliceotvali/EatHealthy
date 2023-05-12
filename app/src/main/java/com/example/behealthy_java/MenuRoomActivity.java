package com.example.behealthy_java;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.util.List;

public class MenuRoomActivity extends AppCompatActivity {

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle arguments = getIntent().getExtras();
        User user = (User) arguments.get("user");
        List<String> products_array = (List<String>) arguments.get("products_array");
        db = user.db;

        //выбор трех блюд
        Integer first_meal = SelectMealFromDB(1);
        Integer second_meal = SelectMealFromDB(2);
        Integer third_meal = SelectMealFromDB(3);

        //запоминаем ккал блюда
        Double first_meal_calories = GetCaloriesOfMeal(first_meal);
        Double second_meal_calories = GetCaloriesOfMeal(second_meal);
        Double third_meal_calories = GetCaloriesOfMeal(third_meal);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_room);
    }

    public Integer SelectMealFromDB(Integer group_meal){
        Integer ID_meal = null;
        String select_food = "SELECT _idMeal FROM MEALS_MAIN WHERE IdGroupMeal = '"+ group_meal + "' ORDER BY RAND() LIMIT 1";
        Cursor food_crs = db.rawQuery(select_food, null);
        while(food_crs.moveToNext()){
            ID_meal  = Integer.valueOf(food_crs.getString(food_crs.getColumnIndexOrThrow ("_idMeal")));
        }
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
}