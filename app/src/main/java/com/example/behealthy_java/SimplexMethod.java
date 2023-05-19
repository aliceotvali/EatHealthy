package com.example.behealthy_java;

import android.app.appsearch.GetByDocumentIdRequest;
import android.database.sqlite.SQLiteDatabase;

public class SimplexMethod {
    SQLiteDatabase db;
    User user;
    public Double first_meal, second_meal, third_meal; //блюда
    public Double first_breakfast_coef, second_breakfast_coef, third_breakfast_coef, first_lunch_coef, second_lunch_coef, third_lunch_coef, first_dinner_coef, second_dinner_coef, third_dinner_coef;
    public Double my_calories, me_proteins, my_fats, my_carbo;


}
