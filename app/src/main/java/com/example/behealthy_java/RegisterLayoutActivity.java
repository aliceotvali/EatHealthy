package com.example.behealthy_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class RegisterLayoutActivity extends AppCompatActivity {

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    Button reg_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        reg_button = findViewById(R.id.insert_reg_data);
        TextInputEditText username_test = findViewById(R.id.usernameid);
        TextInputEditText password_test = findViewById(R.id.passwordid);
        TextInputEditText checkpass_test = findViewById(R.id.checkpasswordid);

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.open();

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username_text = username_test.getText().toString();
                String password_text = String.valueOf(password_test.getText());
                String checkpass = String.valueOf(checkpass_test.getText());
                System.out.println("username_text:");
                System.out.println(username_text);

                String insert_string = "INSERT INTO USERS (Name, Password) VALUES ('" + username_text + "','" + password_text + "')";

                try {
                    if (password_text.equals(checkpass)) {
                        db.execSQL(insert_string);
                        //Toast toast = Toast.makeText(RegisterLayoutActivity.this, "Пользователь создан!", Toast.LENGTH_LONG);
                        //toast.show();
                        register(v);
                    }
                    else {
                        Toast toast = Toast.makeText(RegisterLayoutActivity.this, "Проверьте пароль!", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
                catch (SQLiteConstraintException e){
                    Toast toast = Toast.makeText(RegisterLayoutActivity.this, "Такое имя уже есть!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    public void register(View v) {
        Intent intent = new Intent(this, RegistrationAnketaActivity.class);
        startActivity(intent);
    }

}