package com.example.behealthy_java;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;

import java.util.ArrayList;
import java.util.List;

public class DiaryFragmentActivity extends Fragment {

    View v;
    TextView calories_left, calories_eaten;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    LinearLayout breakfast_layout, lunch_layout, dinner_layout;
    Button b_select, l_select, d_select;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_diary_activity, container, false);
        Bundle arguments = getArguments();
        User user = (User) arguments.get("user");
        sqlHelper = new DatabaseHelper(getActivity());
        db = sqlHelper.open();
        calories_left = v.findViewById(R.id.calories_left);
        calories_eaten = v.findViewById(R.id.calories_eaten);
        SmartMaterialSpinner b_spinner = v.findViewById(R.id.b_spinner);
        SmartMaterialSpinner l_spinner = v.findViewById(R.id.l_spinner);
        SmartMaterialSpinner d_spinner = v.findViewById(R.id.d_spinner);
        breakfast_layout = v.findViewById(R.id.breakfast_layout);
        lunch_layout = v.findViewById(R.id.lunch_layout);
        dinner_layout = v.findViewById(R.id.dinner_layout);
        b_select = v.findViewById(R.id.b_eaten_btn);
        l_select = v.findViewById(R.id.l_eaten_btn);
        d_select = v.findViewById(R.id.d_eaten_btn);

        SelectProductsMealsFromDb(b_spinner);
        SelectProductsMealsFromDb(l_spinner);
        SelectProductsMealsFromDb(d_spinner);


        calories_left.setText("Осталось калорий: " + String.valueOf(user.CaloriesNorm));
        calories_eaten.setText("Употреблено калорий: " + String.valueOf(0));

        List<String> user_products_array = new ArrayList<>();
        b_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpinnerFunc(user_products_array, b_spinner, breakfast_layout);
            }
        });

        l_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpinnerFunc(user_products_array, l_spinner, lunch_layout);
            }
        });

        d_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpinnerFunc(user_products_array, d_spinner, dinner_layout);
            }
        });

        return v;
    }

    public void SelectProductsMealsFromDb(SmartMaterialSpinner spinner){
        Cursor crs = db.rawQuery("SELECT ProductName FROM PRODUCTS2", null);
        List<String> array = new ArrayList<String>();
        while(crs.moveToNext()){
            String description  = crs.getString(crs.getColumnIndexOrThrow ("ProductName"));
            if (description!=null) {array.add(description);}
        }
        Cursor crs2 = db.rawQuery("SELECT NameMeal FROM MEALS_MAIN", null);
        while(crs2.moveToNext()){
            String description  = crs2.getString(crs2.getColumnIndexOrThrow ("NameMeal"));
            if (description!=null) {array.add(description);}
        }
        spinner.setItem(array);
    }

    public void SpinnerFunc(List array, @NonNull SmartMaterialSpinner spinner, LinearLayout layout){
        if (spinner.getSelectedItem()!=null) {
            String selected_product = spinner.getSelectedItem().toString();
            array.add(selected_product);
            System.out.println(array);
            TextView product_textview = new TextView(getActivity());
            product_textview.setTextColor(Color.parseColor("#705DC5"));
            product_textview.setText(selected_product);
            product_textview.setTextSize(14);

            //тут вызвать диалог с весом

            layout.addView(product_textview);
        }
    }
}