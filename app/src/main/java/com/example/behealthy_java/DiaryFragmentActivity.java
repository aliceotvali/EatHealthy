package com.example.behealthy_java;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.List;

public class DiaryFragmentActivity extends Fragment{

    View v;
    TextView calories_left, calories_eaten, breakfast_list_view, lunch_list_view, dinner_list_view, PFC_view;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    LinearLayout breakfast_layout, lunch_layout, dinner_layout;
    Button b_select, l_select, d_select;
    WeightOfProductFragment editWeightDialogFragment;
    SmartMaterialSpinner b_spinner, l_spinner, d_spinner;
    User user;
    PieChart pieChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_diary_activity, container, false);
        Bundle arguments = getArguments();
        user = (User) arguments.get("user");
        sqlHelper = new DatabaseHelper(getActivity());
        db = sqlHelper.open();

        calories_left = v.findViewById(R.id.calories_left);
        calories_eaten = v.findViewById(R.id.calories_eaten);
        b_spinner = v.findViewById(R.id.b_spinner);
        l_spinner = v.findViewById(R.id.l_spinner);
        d_spinner = v.findViewById(R.id.d_spinner);
        breakfast_layout = v.findViewById(R.id.breakfast_layout);
        lunch_layout = v.findViewById(R.id.lunch_layout);
        dinner_layout = v.findViewById(R.id.dinner_layout);
        b_select = v.findViewById(R.id.b_eaten_btn);
        l_select = v.findViewById(R.id.l_eaten_btn);
        d_select = v.findViewById(R.id.d_eaten_btn);
        breakfast_list_view = v.findViewById(R.id.breakfast_list_view);
        lunch_list_view= v.findViewById(R.id.lunch_list_view);
        dinner_list_view = v.findViewById(R.id.dinner_list_view);
        PFC_view = v.findViewById(R.id.PFC_view);
        pieChart = v.findViewById(R.id.piechart);

        SelectProductsMealsFromDb(b_spinner);
        SelectProductsMealsFromDb(l_spinner);
        SelectProductsMealsFromDb(d_spinner);


        calories_left.setText("Осталось калорий: " + Math.round(user.calories_left * 100.00)/100.00);
        calories_eaten.setText("Употреблено калорий: " + Math.round(user.calories_eaten * 100.00)/100.00);
        PFC_view.setText("Б/Ж/У: " + user.proteins_eaten + "/" + user.fats_eaten + "/" + user.carbo_eaten);
        breakfast_list_view.setTextColor(Color.parseColor("#476dd6"));
        breakfast_list_view.setTextSize(14);
        lunch_list_view.setTextColor(Color.parseColor("#476dd6"));
        lunch_list_view.setTextSize(14);
        dinner_list_view.setTextColor(Color.parseColor("#476dd6"));
        dinner_list_view.setTextSize(14);

        if (user.breakfast_list!=null){
            breakfast_list_view.setText(user.breakfast_str_view);
        }
        if (user.lunch_list!=null){
            lunch_list_view.setText(user.lunch_str_view);
        }
        if (user.dinner_list!=null){
            dinner_list_view.setText(user.dinner_str_view);
        }

        setDataToDiagram(user.proteins_eaten, user.fats_eaten, user.carbo_eaten);

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

    //заполняем спиннеры наименованиями из PRODUCTS2 и MEALS_MAIN
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

    //тут все действия, связаные с выбором продукта из спиннера
    public void SpinnerFunc(List array, @NonNull SmartMaterialSpinner spinner, LinearLayout layout){
        if (spinner.getSelectedItem()!=null) {
            String selected_product = spinner.getSelectedItem().toString();
            array.add(selected_product);
            System.out.println(array);
            TextView product_textview = new TextView(getActivity());
            product_textview.setTextColor(Color.parseColor("#476dd6"));
            product_textview.setTextSize(14);

            //вызов диалога с весом
            FragmentManager fm = getFragmentManager();
            editWeightDialogFragment = WeightOfProductFragment.newInstance("Some Title");
            editWeightDialogFragment.show(fm, "fragment_edit_name");
            fm.executePendingTransactions();


            //сохраняем вес, введенный пользователем, в класс User и вызываем ShowCountingCalories
            editWeightDialogFragment.accept.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    EditText text = editWeightDialogFragment.editText;
                    if (String.valueOf(text.getText())!=null && !(String.valueOf(text.getText()).equals("")) && !(String.valueOf(text.getText()).matches("0+"))){
                        String string_for_array_in_User = selected_product + ", " + text.getText() + " гр\n";
                        product_textview.setText(selected_product + ", " + text.getText() + " гр\n");
                        layout.addView(product_textview);
                        if (spinner.equals(b_spinner)){
                            ShowCountingCalories(Integer.valueOf(String.valueOf(text.getText())), selected_product);
                            user.breakfast_str_view = user.breakfast_str_view + string_for_array_in_User;
                            user.breakfast_list.put(Integer.valueOf(String.valueOf(text.getText())), selected_product);
                        } else if (spinner.equals(l_spinner)) {
                            ShowCountingCalories(Integer.valueOf(String.valueOf(text.getText())), selected_product);
                            user.lunch_str_view = user.lunch_str_view + string_for_array_in_User;
                            user.lunch_list.put(Integer.valueOf(String.valueOf(text.getText())), selected_product);
                        } else if (spinner.equals(d_spinner)) {
                            ShowCountingCalories(Integer.valueOf(String.valueOf(text.getText())), selected_product);
                            user.dinner_str_view = user.dinner_str_view + string_for_array_in_User;
                            user.dinner_list.put(Integer.valueOf(String.valueOf(text.getText())), selected_product);
                        }
                    }
                    editWeightDialogFragment.dismiss();
                }
            });

            product_textview.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    System.out.println("Ты трогаешь текствью");
                }
            });
        }
    }

    //отображаем на экране и в диаграмме, что и сколько съел пользователь
    private void ShowCountingCalories(Integer weight, String product){
        Double ccal = Double.valueOf(0), proteins = Double.valueOf(0), fats = Double.valueOf(0), carbo = Double.valueOf(0);
        String find_in_product = "SELECT Calories, Proteins, Fats, Carbo FROM PRODUCTS2 WHERE ProductName = '" + product + "'";
        Cursor find_product_crs = db.rawQuery(find_in_product, null);
        if (find_product_crs.getCount()!=0){ //сначала ищем в таблице продуктов
            while (find_product_crs.moveToNext()){
                ccal = find_product_crs.getDouble(find_product_crs.getColumnIndexOrThrow("Calories"));
                proteins = find_product_crs.getDouble(find_product_crs.getColumnIndexOrThrow("Proteins"));
                fats = find_product_crs.getDouble(find_product_crs.getColumnIndexOrThrow("Fats"));
                carbo = find_product_crs.getDouble(find_product_crs.getColumnIndexOrThrow("Carbo"));
            }
        }
        else { //если в таблице продуктов такого наименования нет, ищем в в блюдах
            String find_in_meal = "SELECT Calories, Proteins, Fats, Carbo FROM MEALS_MAIN WHERE NameMeal = '" + product + "'";
            Cursor find_meal_crs = db.rawQuery(find_in_meal, null);
            while (find_meal_crs.moveToNext()){
                ccal = find_meal_crs.getDouble(find_meal_crs.getColumnIndexOrThrow("Calories"));
                proteins = find_meal_crs.getDouble(find_meal_crs.getColumnIndexOrThrow("Proteins"));
                fats = find_meal_crs.getDouble(find_meal_crs.getColumnIndexOrThrow("Fats"));
                carbo = find_meal_crs.getDouble(find_meal_crs.getColumnIndexOrThrow("Carbo"));
            }
        }

        //запоминаем КБЖУ в класс User
        user.calories_eaten = Math.round((user.calories_eaten + (ccal*weight/100))*100.00)/100.00;
        user.calories_left = Math.round((user.calories_left - (ccal*weight/100))*100.00)/100.00;
        user.proteins_eaten = Math.round((user.proteins_eaten + (proteins*weight/100))*100.00)/100.00;
        user.fats_eaten = Math.round((user.fats_eaten + (fats*weight/100))*100.00)/100.00;
        user.carbo_eaten = Math.round((user.carbo_eaten + (carbo*weight/100))*100.00)/100.00;

        //вывод на экран
        calories_eaten.setText(String.valueOf("Употреблено калорий: " + user.calories_eaten));
        calories_left.setText(String.valueOf("Осталось калорий: " + user.calories_left));
        PFC_view.setText("Б/Ж/У: " + user.proteins_eaten + "/" + user.fats_eaten + "/" + user.carbo_eaten);
        //очищаем диаграмму, чтобы не добавлялись лишние поля круга
        pieChart.clearChart();
        //заполняем диаграмму обновленными значениями
        setDataToDiagram(user.proteins_eaten, user.fats_eaten, user.carbo_eaten);
    }

    public void setDataToDiagram(Double proteins, Double fats, Double carbo){
        pieChart.addPieSlice(
                new PieModel(
                        "Белки",
                        Float.valueOf(String.valueOf(proteins)),
                        Color.parseColor("#f3da0b")));
        pieChart.addPieSlice(
                new PieModel(
                        "Жиры",
                        Float.valueOf(String.valueOf(fats)),
                        Color.parseColor("#ff4e33")));
        pieChart.addPieSlice(
                new PieModel(
                        "Углеводы",
                        Float.valueOf(String.valueOf(carbo)),
                        Color.parseColor("#75c1ff")));

        pieChart.startAnimation(); //анимация для красоты :)
    }

}