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

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
                String selected_product = product_spinner.getSelectedItem().toString();
                if (user_products_array.contains(selected_product)==false){
                    user_products_array.add(selected_product);
                    System.out.println(user_products_array);
                    TextView product_textview = new TextView(getActivity());
                    product_textview.setTextColor(Color.parseColor("#705DC5"));
                    product_textview.setText(selected_product);
                    product_textview.setTextSize(14);
                    linearLayout.addView(product_textview);
                };
            }
        });

        create_menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MenuRoomActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("products_array", (ArrayList<String>) user_products_array);
                startActivity(intent);
            }
        });

        return rootView;
    }
}