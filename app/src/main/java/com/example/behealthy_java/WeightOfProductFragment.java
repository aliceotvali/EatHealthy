package com.example.behealthy_java;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class WeightOfProductFragment extends DialogFragment {

    View v;
    Button accept, skip_weight_btn;
    EditText editText;
    public Integer weight;
    Boolean check_is_accepted;
    public WeightOfProductFragment() {

    }

    public static WeightOfProductFragment newInstance(String param1) {
        WeightOfProductFragment fragment = new WeightOfProductFragment();
        Bundle args = new Bundle();
        //args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        v = inflater.inflate(R.layout.fragment_weight_of_product, container, false);
        accept = v.findViewById(R.id.accept_weight_btn);
        editText = v.findViewById(R.id.weight_edit);
        skip_weight_btn = v.findViewById(R.id.skip_weight_btn);
        check_is_accepted = false;

        getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // your code here
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //code
            }
        });

        skip_weight_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });

        return v;
    }
}