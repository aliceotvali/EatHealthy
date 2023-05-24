package com.example.behealthy_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.io.Serializable;

public class LoginLayoutActivity extends AppCompatActivity {

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Button log_button;
    String UserID;

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
                if (!username_text.equals("") && !password_text.equals("")) {
                    String check_string = "SELECT * FROM USERS WHERE Name='" + username_text + "' AND Password = '" + password_text + "'";
                    Cursor c = db.rawQuery(check_string, null);
                    if (c.getCount() == 0) {
                        Toast toast = Toast.makeText(LoginLayoutActivity.this, "Такого пользователя не существует!", Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        String select_string = "SELECT _id FROM USERS WHERE Name='" + username_text + "' AND Password = '" + password_text + "'";
                        Cursor c2 = db.rawQuery(select_string, null);
                        while (c2.moveToNext()) {
                            UserID = c2.getString(c2.getColumnIndexOrThrow("_id"));
                        }
                        String check_akk_string = "SELECT * FROM USERS_INFO WHERE _id = '" + UserID + "'";
                        Cursor c3 = db.rawQuery(check_akk_string, null);
                        if (c3.getCount() == 0) {
                            register(v, UserID, username_text, password_text);
                        } else {
                            user_room(v, FindUser(UserID, username_text, password_text));
                        }
                        ;
                    }
                }
                else {
                    Toast toast = Toast.makeText(LoginLayoutActivity.this, "Поля не могут быть пустыми!", Toast.LENGTH_LONG);
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

    public User FindUser (String UserID, String username, String password) {
        User user = new User();
        user.name = username;
        user.password = password;
        user.UserID = Integer.valueOf(UserID);
        user.db = db;
        user.setGender();
        user.setAge();
        user.setHeight();
        user.setWeight();
        user.setActivity();
        user.setPurpose();
        user.setActivityName();
        user.setPurposeName();
        user.updateBMR();
        user.setBMR();
        user.setCFPC();

        return (user);
    }

    private void user_room(View v, User user){
        Intent intent = new Intent(this, UserRoomActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}