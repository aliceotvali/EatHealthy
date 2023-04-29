package com.example.behealthy_java;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class RegistrationAnketaActivity extends AppCompatActivity {

    Spinner gender_spinner, activity_spinner, purpose_spinner;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_anketa_layout);

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.open();
        activity_spinner = findViewById(R.id.spinner_activity);
        purpose_spinner = findViewById(R.id.spinner_purpose);

        Cursor crs = db.rawQuery("SELECT Description FROM ACTIVITY", null);
        spinnerWrite(crs, activity_spinner);

        Cursor crs2 = db.rawQuery("SELECT Description FROM PURPOSE", null);
        spinnerWrite(crs2, purpose_spinner);
    }
    private void spinnerWrite(Cursor c, Spinner spinner){    //Функция записи данных из БД в спиннер
        List<String> array = new ArrayList<String>();
        while(c.moveToNext()){
            String description  = c.getString(c.getColumnIndexOrThrow ("Description"));
            array.add(description);
        }

        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        array); //selected item will look like a spinner set from XML
        spinnerArrayAdapter2.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter2);
    }

    private void UserStuff(){
        User user;
    }


}