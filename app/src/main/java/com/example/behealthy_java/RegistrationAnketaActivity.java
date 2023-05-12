package com.example.behealthy_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RegistrationAnketaActivity extends AppCompatActivity {

    Spinner gender_spinner, activity_spinner, purpose_spinner;
    EditText age_input, weight_input, height_input;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Button input_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle arguments = getIntent().getExtras();
        User user = (User) arguments.get("user");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_anketa_layout);

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.open();
        gender_spinner = findViewById(R.id.spinner);
        activity_spinner = findViewById(R.id.spinner_activity);
        purpose_spinner = findViewById(R.id.spinner_purpose);
        input_data = findViewById(R.id.input_user_data_btn);
        age_input = findViewById(R.id.age_input);
        weight_input = findViewById(R.id.weight_input);
        height_input = findViewById(R.id.height_input);

        Cursor crs = db.rawQuery("SELECT Description FROM ACTIVITY", null);
        spinnerWrite(crs, activity_spinner);

        Cursor crs2 = db.rawQuery("SELECT Description FROM PURPOSE", null);
        spinnerWrite(crs2, purpose_spinner);

        input_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    user.gender = (String) gender_spinner.getSelectedItem();

                    Cursor activityNum = db.rawQuery("SELECT _idActivity FROM ACTIVITY WHERE Description = '"+ activity_spinner.getSelectedItem().toString() + "'", null);
                    while(activityNum.moveToNext()){
                        user.ActivityNum = Integer.valueOf(activityNum.getString(activityNum.getColumnIndexOrThrow ("_IdActivity")));
                    }

                    Cursor purposeNum = db.rawQuery("SELECT _idPurpose FROM PURPOSE WHERE Description = '"+ purpose_spinner.getSelectedItem().toString() + "'", null);
                    while(purposeNum.moveToNext()){
                        user.PurposeNum = Integer.valueOf(purposeNum.getString(purposeNum.getColumnIndexOrThrow ("_idPurpose")));
                    }
                    user.age = Integer.valueOf(age_input.getText().toString());
                    user.height = Integer.valueOf(height_input.getText().toString());
                    user.weight = Integer.valueOf(weight_input.getText().toString());

                    //user.CreateUser(); //заполнение USER_INFO
                    String insert_string2 = "INSERT INTO USERS_INFO (_id, Gender, ActivityNum, Purpose2, Age, Weight, Height) " +
                            "VALUES ('" + user.UserID + "', '" + user.gender + "', '" + user.ActivityNum + "', '" + user.PurposeNum+ "', " +
                            "'" + user.age + "', '" + user.weight + "', '" + user.height +"')";
                    db.execSQL(insert_string2);

                    user.setActivityName();
                    user.setPurposeName();

                    user.setCFPC();

                    user_room(v, user);
                }
                catch (NumberFormatException e){
                    Toast toast = Toast.makeText(RegistrationAnketaActivity.this, "Заполните все поля!", Toast.LENGTH_LONG);
                    toast.show();
                }
                catch (SQLiteConstraintException e){
                    user.db = db;
                    user.gender = (String) gender_spinner.getSelectedItem();
                    user.updateGender();

                    Cursor activityNum = db.rawQuery("SELECT _idActivity FROM ACTIVITY WHERE Description = '"+ activity_spinner.getSelectedItem().toString() + "'", null);
                    while(activityNum.moveToNext()){
                        user.ActivityNum = Integer.valueOf(activityNum.getString(activityNum.getColumnIndexOrThrow ("_IdActivity")));
                    }
                    user.updateActivity();

                    Cursor purposeNum = db.rawQuery("SELECT _idPurpose FROM PURPOSE WHERE Description = '"+ purpose_spinner.getSelectedItem().toString() + "'", null);
                    while(purposeNum.moveToNext()){
                        user.PurposeNum = Integer.valueOf(purposeNum.getString(purposeNum.getColumnIndexOrThrow ("_idPurpose")));
                    }

                    user.updatePurpose();
                    user.age = Integer.valueOf(age_input.getText().toString());
                    user.updateAge();
                    user.height = Integer.valueOf(height_input.getText().toString());
                    user.updateHeight();
                    user.weight = Integer.valueOf(weight_input.getText().toString());
                    user.updateWeight();
                    user.setActivityName();
                    user.setPurposeName();

                    user.setCFPC();

                    user_room(v, user);
                }
            }
        });

    }
    private String spinnerWrite(Cursor c, Spinner spinner){    //Функция записи данных из БД в спиннер
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
        String spinner_result = spinner.getSelectedItem().toString();


        return spinner_result;
    }

    private void user_room(View v, User user){
        Intent intent = new Intent(this, UserRoomActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

}