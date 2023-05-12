package com.example.behealthy_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.Serializable;

public class UserRoomActivity extends AppCompatActivity {

    BottomNavigationView menu;
    HomeFragmentActivity home_fragment = new HomeFragmentActivity();
    MenuFragmentActivity menu_fragment = new MenuFragmentActivity();
    DiaryFragmentActivity diary_fragment = new DiaryFragmentActivity();
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle arguments = getIntent().getExtras();
        User user = (User) arguments.get("user");

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.open();

        Bundle user_bundle = new Bundle();
        user_bundle.putSerializable("user", user);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_room);


        menu = findViewById(R.id.menu);
        home_fragment.setArguments(user_bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.containerForMenu, home_fragment).commit();
        menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_page:
                        home_fragment.setArguments(user_bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.containerForMenu, home_fragment).commit();
                        return true;
                    case R.id.menu_page:
                        menu_fragment.setArguments(user_bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.containerForMenu, menu_fragment).commit();
                        return true;
                    case R.id.diary_page:
                        diary_fragment.setArguments(user_bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.containerForMenu, diary_fragment).commit();
                        return true;
                }
                return false;
            }
        });

    }
}