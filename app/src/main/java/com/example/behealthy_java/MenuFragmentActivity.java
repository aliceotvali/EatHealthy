package com.example.behealthy_java;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class MenuFragmentActivity extends Fragment {
    View rootView;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menu_activity, container, false);
        SmartMaterialSpinner product_spinner = rootView.findViewById(R.id.product_spinner);
        Button select_product_button = rootView.findViewById(R.id.select_product_btn);
        Button create_menu_button = rootView.findViewById(R.id.create_menu_btn);

        Bundle bundle = getArguments();
        user = (User) bundle.get("user");

        sqlHelper = new DatabaseHelper(getActivity());
        db = sqlHelper.open();

        Cursor crs = db.rawQuery("SELECT ProductName FROM PRODUCTS2", null);
        List<String> array = new ArrayList<String>();
        while(crs.moveToNext()){
            String description  = crs.getString(crs.getColumnIndexOrThrow ("ProductName"));
            if (description!=null) {array.add(description);}
        }
        product_spinner.setItem(array);

        List<String> user_products_array = new ArrayList<>();
        LinearLayout linearLayout = rootView.findViewById(R.id.choice_product_view);
        select_product_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product_spinner.getSelectedItem() != null) {
                    String selected_product = product_spinner.getSelectedItem().toString();
                    if (!user_products_array.contains(selected_product)) {
                        user_products_array.add(selected_product);
                        System.out.println(user_products_array);
                        TextView product_textview = new TextView(getActivity());
                        product_textview.setTextColor(Color.parseColor("#705DC5"));
                        product_textview.setText(selected_product);
                        product_textview.setTextSize(16);
                        linearLayout.addView(product_textview);
                    }
                }
            }
        });

        create_menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_products_array.isEmpty()){
                    Toast toast = Toast.makeText(getActivity(), "Пожалуйста, выберите продукты!", Toast.LENGTH_LONG);
                    toast.show();
                }
                else {
                    List all_meals_array = SelectMealFromDBUpdated(user_products_array);
                    List breakfast_meals_array = ListOfMealsByGroup(1, all_meals_array);
                    List lunch_meals_array = ListOfMealsByGroup(2, all_meals_array);
                    List dinner_meals_array = ListOfMealsByGroup(3, all_meals_array);

                    if (breakfast_meals_array.isEmpty() || lunch_meals_array.isEmpty() || dinner_meals_array.isEmpty()){
                        Toast toast = Toast.makeText(getActivity(), "Пожалуйста, выберите больше продуктов!", Toast.LENGTH_LONG);
                        toast.show();
                    }
                    else {
                        Intent intent = new Intent(getActivity(), MenuRoomActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("products_array", (ArrayList<String>) user_products_array);
                        startActivity(intent);
                    }

                }
            }
        });

        return rootView;
    }

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
}