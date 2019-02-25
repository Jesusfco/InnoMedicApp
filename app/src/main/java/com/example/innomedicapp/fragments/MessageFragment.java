package com.example.innomedicapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.innomedicapp.R;
import com.example.innomedicapp.model.AuthUser;

public class MessageFragment extends Fragment {

    public TextView nameView, emailView, phoneView, userTypeView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_messages, container, false);

        //return super.onCreateView(inflater, container, savedInstanceState);


    }

    @Override
    public void onStart() {



        super.onStart();
    }
}
