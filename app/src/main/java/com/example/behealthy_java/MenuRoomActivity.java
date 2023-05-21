package com.example.behealthy_java;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MenuRoomActivity extends AppCompatActivity {

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Double CaloriesNorm;
    TextView breakfast_name, lunch_name, dinner_name, description_breakfast, description_lunch, description_dinner,
            structure_breakfast, structure_lunch, structure_dinner,
            breakfast_CPFC, lunch_CPFC, dinner_CPFC, CPFC_final;

    public Double first_meal_coef, second_meal_coef, third_meal_coef;
    public Double my_calories, my_proteins, my_fats, my_carbo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_room);
        Bundle arguments = getIntent().getExtras();
        User user = (User) arguments.get("user");
        List<String> products_array = (List<String>) arguments.get("products_array");
        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.open();

        //названия
        breakfast_name = findViewById(R.id.breakfast_name_text);
        lunch_name = findViewById(R.id.lunch_name_text);
        dinner_name = findViewById(R.id.dinner_name_text);
        //рецепт
        description_breakfast = findViewById(R.id.breakfast_description_text);
        description_lunch = findViewById(R.id.lunch_description_text);
        description_dinner = findViewById(R.id.dinner_description_text);
        //состав
        structure_breakfast = findViewById(R.id.breakfast_structure_text);
        structure_lunch = findViewById(R.id.lunch_structure_text);
        structure_dinner = findViewById(R.id.dinner_structure_text);
        //КБЖУ
        breakfast_CPFC = findViewById(R.id.breakfast_CPFC);
        lunch_CPFC = findViewById(R.id.lunch_CPFC);
        dinner_CPFC = findViewById(R.id.dinner_CPFC);
        CPFC_final = findViewById(R.id.CPFC_final_text);

        System.out.println("У меня есть массив " + products_array);
        System.out.println("Мое имя из MenuRoomActivity " + user.name);
        CaloriesNorm = user.CaloriesNorm;

        System.out.println("Норма ккал пользователя: " + CaloriesNorm);

        List all_meals_array = SelectMealFromDBUpdated(products_array);
        List breakfast_meals_array = ListOfMealsByGroup(1, all_meals_array);
        List lunch_meals_array = ListOfMealsByGroup(2, all_meals_array);
        List dinner_meals_array = ListOfMealsByGroup(3, all_meals_array);

        //выбор трех блюд
        Integer first_meal = SelectMealFromArray(breakfast_meals_array);
        Integer second_meal = SelectMealFromArray(lunch_meals_array);
        Integer third_meal = SelectMealFromArray(dinner_meals_array);

        //запоминаем ккал блюда
        Double first_meal_calories = GetCaloriesOfMeal(first_meal);
        Double second_meal_calories = GetCaloriesOfMeal(second_meal);
        Double third_meal_calories = GetCaloriesOfMeal(third_meal);

        //на это будем умножать для подсчета итогового кол-ва ингридиентов и КБЖУ
        first_meal_coef = CountGrammOfMeal(first_meal_calories, 1);
        second_meal_coef = CountGrammOfMeal(second_meal_calories, 2);
        third_meal_coef = CountGrammOfMeal(third_meal_calories, 3);

        //считаем граммы на нужную нам норму
        Double first_meal_gramms = first_meal_coef*100;
        Double second_meal_gramms = second_meal_coef*100;
        Double third_meal_gramms = third_meal_coef*100;

        System.out.println("Тут есть нужные продукты:" + all_meals_array);
        System.out.println("Это массив возможных завтраков:" + breakfast_meals_array);
        System.out.println("Это массив возможных обедов:" + lunch_meals_array);
        System.out.println("Это массив возможных ужинов:" + dinner_meals_array);

        System.out.println("Коэффицент для завтрака: " + first_meal_coef);
        System.out.println("Коэффицент для обеда: " + second_meal_coef);
        System.out.println("Коэффицент для ужина: " + third_meal_coef);

        //считаем состав завтрака
        String breakfast_structure = StructureOfMeal(first_meal, first_meal_coef);
        System.out.println("Состав завтрака: " + breakfast_structure);

        String lunch_structure = StructureOfMeal(second_meal, second_meal_coef);
        System.out.println("Состав обеда: " + lunch_structure);

        String dinner_structure = StructureOfMeal(third_meal, third_meal_coef);
        System.out.println("Состав ужина: " + dinner_structure);

        Double b_ccal_final = Math.round(GetCaloriesOfMeal(first_meal)*first_meal_coef*100.00)/100.00;
        Double b_pro_final = Math.round(PFCofMeal(first_meal, "Proteins")*first_meal_coef*100.00)/100.00;
        Double b_fat_final = Math.round(PFCofMeal(first_meal, "Fats")*first_meal_coef*100.00)/100.00;
        Double b_car_final = Math.round(PFCofMeal(first_meal, "Carbo")*first_meal_coef*100.00)/100.00;

        Double l_ccal_final = Math.round(GetCaloriesOfMeal(second_meal)*second_meal_coef*100.00)/100.00;
        Double l_pro_final = Math.round(PFCofMeal(second_meal, "Proteins")*second_meal_coef*100.00)/100.00;
        Double l_fat_final = Math.round(PFCofMeal(second_meal, "Fats")*second_meal_coef*100.00)/100.00;
        Double l_car_final = Math.round(PFCofMeal(second_meal, "Carbo")*second_meal_coef*100.00)/100.00;

        Double d_ccal_final = Math.round(GetCaloriesOfMeal(third_meal)*third_meal_coef*100.00)/100.00;
        Double d_pro_final = Math.round(PFCofMeal(third_meal, "Proteins")*third_meal_coef*100.00)/100.00;
        Double d_fat_final = Math.round(PFCofMeal(third_meal, "Fats")*third_meal_coef*100.00)/100.00;
        Double d_car_final = Math.round(PFCofMeal(third_meal, "Carbo")*third_meal_coef*100.00)/100.00;

        CPFC_final.setText("КБЖУ: " + Math.round((b_ccal_final+l_ccal_final+d_ccal_final)*100.00)/100.00 +
                "/" + Math.round((b_pro_final+l_pro_final+d_pro_final)*100.00)/100.00 +
                "/" + Math.round((b_fat_final+l_fat_final+d_fat_final)*100.00)/100.00 +
                "/" + Math.round((b_car_final+l_car_final+d_car_final)*100.00)/100.00);

        breakfast_name.setText(GetMealName(first_meal));
        breakfast_CPFC.setText("КБЖУ: " + b_ccal_final + "/" + b_pro_final + "/" + b_fat_final + "/" + b_car_final);
        structure_breakfast.setText(breakfast_structure);
        description_breakfast.setText(GetMealDescription(first_meal));

        lunch_name.setText(GetMealName(second_meal));
        lunch_CPFC.setText("КБЖУ: " + l_ccal_final + "/" + l_pro_final + "/" + l_fat_final + "/" + l_car_final);
        structure_lunch.setText(lunch_structure);
        description_lunch.setText(GetMealDescription(second_meal));

        dinner_name.setText(GetMealName(third_meal));
        dinner_CPFC.setText("КБЖУ: " + d_ccal_final + "/" + d_pro_final + "/" + d_fat_final + "/" + d_car_final);
        structure_dinner.setText(dinner_structure);
        description_dinner.setText(GetMealDescription(third_meal));

        //СИМПЛЕКС-МЕТОД НАЧАЛО
        /*SimplexMethod simplex = new SimplexMethod();
        simplex.user = user;
        simplex.caloriesNorm = CaloriesNorm;
        simplex.proteinsNorm = user.ProteinsNorm;
        simplex.fatsNorm = user.FatsNorm;
        simplex.carboNorm = user.CarboNorm;

        simplex.b_ccal = first_meal_calories;
        simplex.b_proteins = PFCofMeal(first_meal, "Proteins");
        simplex.b_fats = PFCofMeal(first_meal, "Fats");
        simplex.b_carbo = PFCofMeal(first_meal, "Carbo");

        simplex.l_ccal = second_meal_calories;
        simplex.l_proteins = PFCofMeal(second_meal, "Proteins");
        simplex.l_fats = PFCofMeal(second_meal, "Fats");
        simplex.l_carbo = PFCofMeal(second_meal, "Carbo");

        simplex.d_ccal = third_meal_calories;
        simplex.d_proteins = PFCofMeal(third_meal, "Proteins");
        simplex.d_fats = PFCofMeal(third_meal, "Fats");
        simplex.d_carbo = PFCofMeal(third_meal, "Carbo");

        //массив калорийностей
        simplex.ccal[0] = first_meal_calories;
        simplex.ccal[1] = second_meal_calories;
        simplex.ccal[2] = third_meal_calories;

        //массив белков
        simplex.proteins[0] = PFCofMeal(first_meal, "Proteins");
        simplex.proteins[1] = PFCofMeal(second_meal, "Proteins");
        simplex.proteins[2] = PFCofMeal(third_meal, "Proteins");

        //массив жиров
        simplex.fats[0] = PFCofMeal(first_meal, "Fats");
        simplex.fats[1] = PFCofMeal(second_meal, "Fats");
        simplex.fats[2] = PFCofMeal(third_meal, "Fats");

        //массив углеводов
        simplex.carbo[0] = PFCofMeal(first_meal, "Carbo");
        simplex.carbo[1] = PFCofMeal(second_meal, "Carbo");
        simplex.carbo[2] = PFCofMeal(third_meal, "Carbo");

        simplex.initialize();*/

    }

    //рандомно выбираем блюдо из предложенных
    public Integer SelectMealFromArray(List meals){
        int b = new Random().nextInt(meals.size());
        Integer ID_meal = Integer.valueOf((String) meals.get(b));
        return ID_meal;
    }

    //вытаскиваем строку с составом блюда
    public String StructureOfMeal(Integer id_meal, Double coef){
        List structure = new ArrayList<>();
        String structure_str = "";

        String find_in_db_str = "SELECT IdProduct, CountOfProduct, UOM FROM MEALS_STRUCTURE WHERE IdMeal = '" + id_meal + "'";
        Cursor find_crs = db.rawQuery(find_in_db_str, null);
        while (find_crs.moveToNext()){
            Integer id_product = Integer.valueOf((find_crs.getString(find_crs.getColumnIndexOrThrow ("IdProduct"))));
            String find_name_db_str = "SELECT ProductName FROM PRODUCTS2 WHERE _idProduct = '" + id_product + "'";
            Cursor find_name_crs = db.rawQuery(find_name_db_str, null);
            while (find_name_crs.moveToNext()){
                String add_str = (find_name_crs.getString(find_name_crs.getColumnIndexOrThrow ("ProductName"))) + ", " +
                        Math.round(Integer.valueOf(find_crs.getString(find_crs.getColumnIndexOrThrow ("CountOfProduct")))*coef*100.00)/100.00 + " " +
                        (find_crs.getString(find_crs.getColumnIndexOrThrow ("UOM")));
                structure.add(add_str);
            }
        }
        for (int i=0; i<structure.size(); i++){
            structure_str = structure_str + structure.get(i) + "\n";
        }
        return structure_str;
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
    //возвращаем калорийность блюда
    public Double GetCaloriesOfMeal(Integer id_meal){
        Double calories = null;
        String select_calories = "SELECT Calories FROM MEALS_MAIN WHERE _idMeal = '" + id_meal + "'";
        Cursor calories_crs = db.rawQuery(select_calories, null);
        while(calories_crs.moveToNext()){
            calories  = Double.valueOf(calories_crs.getString(calories_crs.getColumnIndexOrThrow ("Calories")));
        }
        return calories;
    }
    //считаем вес блюда в граммах
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
        //gramms = gramms*100;
        Double rounded_gramms = Math.round(gramms * 100.00)/100.00;
        return rounded_gramms;
    }

    //вернуть название блюда по айди
    public String GetMealName(Integer id_meal) {
        String select_calories = "SELECT NameMeal FROM MEALS_MAIN WHERE _idMeal = '" + id_meal + "'";
        Cursor calories_crs = db.rawQuery(select_calories, null);
        String meal_name = null;
        while (calories_crs.moveToNext()) {
            meal_name = calories_crs.getString(calories_crs.getColumnIndexOrThrow("NameMeal"));
        }
        return meal_name;
    }

    public String GetMealDescription(Integer id_meal){
        String description = null;

        String select_des = "SELECT DescriptionMeal FROM MEALS_MAIN WHERE _idMeal = '" + id_meal + "'";
        Cursor select_des_crs = db.rawQuery(select_des, null);
        while (select_des_crs.moveToNext()) {
            description = select_des_crs.getString(select_des_crs.getColumnIndexOrThrow("DescriptionMeal"));
        }

        return description;
    }

    //подсчет БЖУ
    public Double PFCofMeal(Integer id_meal, String PFC){
        Double PFCofMeal = null;
        String select_calories = "SELECT " + PFC + " FROM MEALS_MAIN WHERE _idMeal = '" + id_meal + "'";
        Cursor PFC_crs = db.rawQuery(select_calories, null);
        while(PFC_crs.moveToNext()){
            PFCofMeal = Double.valueOf(PFC_crs.getString(PFC_crs.getColumnIndexOrThrow(PFC)));
        }
        return PFCofMeal;
    }
}