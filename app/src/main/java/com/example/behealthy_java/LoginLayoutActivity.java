package com.example.behealthy_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

public class LoginLayoutActivity extends AppCompatActivity {

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    Button log_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        log_button = findViewById(R.id.login_data);
        TextInputEditText username_test = findViewById(R.id.usernameid);
        TextInputEditText password_test = findViewById(R.id.passwordid);
        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.open();
        try {
            sqlHelper.updateDataBase();
        } catch (IOException e) {
            throw new Error("UnableToUpdateDatabase");
        }
        try {
            db = sqlHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }

        db = sqlHelper.open();


        log_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username_text = username_test.getText().toString();
                String password_text = String.valueOf(password_test.getText());
                String check_string = "SELECT * FROM USERS WHERE Name='" + username_text + "' AND Password = '"+ password_text +"'";
                Cursor c = db.rawQuery(check_string, null);
                if (c.getCount()==0){
                    Toast toast = Toast.makeText(LoginLayoutActivity.this, "Такого пользователя не существует!", Toast.LENGTH_LONG);
                    toast.show();
                }
                else {
                    Toast toast = Toast.makeText(LoginLayoutActivity.this, "Кабинет пользователя в процессе разработки!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

}