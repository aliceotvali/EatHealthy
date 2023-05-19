package com.example.behealthy_java;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;

public class HomeFragmentActivity extends Fragment {
    Button change_btn;
    TextView age_view;
    TextView height_view;
    TextView weight_view;
    TextView activity_view;
    TextView purpose_view, ccal_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home_activity, container, false);
        Bundle arguments = getArguments();
        User user = (User) arguments.get("user");

        change_btn = rootView.findViewById(R.id.change_anketa);
        age_view = rootView.findViewById(R.id.age_view);
        height_view = rootView.findViewById(R.id.height_view);
        weight_view = rootView.findViewById(R.id.weight_view);
        activity_view = rootView.findViewById(R.id.activity_view);
        purpose_view = rootView.findViewById(R.id.purpose_view);
        ccal_view = rootView.findViewById(R.id.ccal_view);

        age_view.setText("Ваш возраст: " + user.getAge());
        height_view.setText("Ваш рост (см): " + user.getHeight());
        weight_view.setText("Ваш вес (кг): " + user.getWeight());
        activity_view.setText("Ваш образ жизни: " + user.getActivity());
        purpose_view.setText("Ваша цель: " + user.getPurpose());
        ccal_view.setText("Суточная норма ккал: " + user.CaloriesNorm);

        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change(view, user);
            }
        });

        return rootView;
    }

    public void change(View v, User user) {
        Intent intent = new Intent(getActivity(), RegistrationAnketaActivity.class);
        intent.putExtra("user", (Serializable) user);
        startActivity(intent);
    }

}