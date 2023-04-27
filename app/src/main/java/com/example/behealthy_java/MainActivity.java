package com.example.behealthy_java;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.http.conn.ssl.StrictHostnameVerifier;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    Button register_button, signin_button;
    List test;
    TextInputLayout username_tv, password_tv, checkpass_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        register_button = findViewById(R.id.signup_button);
        signin_button = findViewById(R.id.login_button);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        // создаем базу данных
        databaseHelper.create_db();
    }
    public void onResume() {
        super.onResume();
        // открываем подключение
        //db = databaseHelper.open();
        //получаем данные из бд в виде курсора
        //userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE, null);
        // определяем, какие столбцы из курсора будут выводиться в ListView
        //String[] headers = new String[]{DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_PASS};
        // создаем адаптер, передаем в него курсор
        //userAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
        //        userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        //String pass_check_text = String.valueOf(userAdapter);
    }
    public void register(View v) {
        Intent intent = new Intent(this, RegisterLayoutActivity.class);
        startActivity(intent);
    }
    public void login(View v) {
        Intent intent = new Intent(this, LoginLayoutActivity.class);
        startActivity(intent);
    }
}