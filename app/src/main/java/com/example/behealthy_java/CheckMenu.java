package com.example.behealthy_java;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckMenu {
    SQLiteDatabase db;
    Double proteins_deviation, fats_deviation, carbo_deviation, deviation;
    List breakfast_list, lunch_list, dinner_list;
    Map ID_deviation = new HashMap<List,Double>(); //словарь, в нем айди и суммарное отклонение от нормы
    //айди + кбжу
    Map calories = new HashMap<Integer,Double>();
    Map proteins = new HashMap<Integer,Double>();
    Map fats = new HashMap<Integer,Double>();
    Map carbo = new HashMap<Integer,Double>();
    User user;

    public void CheckMeal(Integer breakfast, Integer lunch, Integer dinner){

        CallPFCofMeal(breakfast, lunch, dinner); //вставляем в словари айдишники + значение КБЖУ

        //записываем все девиации подобранного меню, в т.ч. суммарную
        WriteDeviations(breakfast, lunch, dinner);

        //пробуем заменить одно из блюд
        //ТУТ БУДЕТ VNS


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

    public void putInMap(Map CPFC, Integer meal, String CPFC_str){
        CPFC.put(meal, PFCofMeal(meal, CPFC_str));
    }

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

    public void WriteDeviations(Integer breakfast, Integer lunch, Integer dinner){
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

        if (check_fats>=user.FatsNorm){
            fats_deviation = Double.valueOf(0);
        } else {
            fats_deviation = user.FatsNorm-check_fats;
        }

        Double check_carbo = (Double) carbo.get(breakfast)*b_coef + (Double) carbo.get(lunch)*l_coef
                + (Double) carbo.get(dinner)*d_coef;


        if (check_carbo>=user.CarboNorm){
            carbo_deviation = Double.valueOf(0);
        } else {
            carbo_deviation = user.CarboNorm-check_carbo;
        }

        deviation = carbo_deviation + proteins_deviation + fats_deviation;
        System.out.println("Девиации БЖУ: " + proteins_deviation + " " + fats_deviation + " " +
                carbo_deviation + ", суммарная: " + deviation);

        List this_menu_list = new ArrayList<>();
        this_menu_list.add(breakfast);
        this_menu_list.add(lunch);
        this_menu_list.add(dinner);

        ID_deviation.put(this_menu_list, deviation);
    }
}
