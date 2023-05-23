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

import java.io.Serializable;

public class RegisterLayoutActivity extends AppCompatActivity {

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    Button reg_button;
    String UserID;


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

                if (!username_text.equals("") && !password_text.equals("")) {

                    String insert_string = "INSERT INTO USERS (Name, Password) VALUES ('" + username_text + "','" + password_text + "')";

                    try {
                        if (password_text.equals(checkpass)) {
                            db.execSQL(insert_string);
                            String select_string = "SELECT _id FROM USERS WHERE Name='" + username_text + "' AND Password = '" + password_text + "'";
                            Cursor c = db.rawQuery(select_string, null);
                            while (c.moveToNext()) {
                                UserID = c.getString(c.getColumnIndexOrThrow("_id"));
                            }
                            register(v, UserID, username_text, password_text);
                        } else {
                            Toast toast = Toast.makeText(RegisterLayoutActivity.this, "Проверьте пароль!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    } catch (SQLiteConstraintException e) {
                        Toast toast = Toast.makeText(RegisterLayoutActivity.this, "Такое имя уже есть!", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
                else {
                    Toast toast = Toast.makeText(RegisterLayoutActivity.this, "Поля не могут быть пустыми!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    public void register(View v, String UserID, String username, String password) {
        User user = new User();
        user.name = username;
        user.password = password;
        user.UserID = Integer.valueOf(UserID);
        Intent intent = new Intent(this, RegistrationAnketaActivity.class);
        intent.putExtra("user", (Serializable) user);
        startActivity(intent);
    }

}