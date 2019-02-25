package com.example.innomedicapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.innomedicapp.R;
import com.example.innomedicapp.model.AuthUser;

public class PerfilFragment extends Fragment {

    public TextView nameView, emailView, phoneView, userTypeView;

    public ViewGroup container;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        this.nameView = (TextView)view.findViewById(R.id.perfilName);
        this.emailView = (TextView)view.findViewById(R.id.perfilEmail);
        this.phoneView = (TextView)view.findViewById(R.id.perfilPhone);
        this.userTypeView = (TextView)view.findViewById(R.id.perfilUserType);

        return view;

    }

    @Override
    public void onStart() {

        AuthUser authUser = new AuthUser(getActivity());

        this.nameView.setText("Nombre: " + authUser.getName());
        this.emailView.setText("Correo: " + authUser.getEmail());
        this.phoneView.setText("Télefono: " + authUser.getPhone());
        this.userTypeView.setText("Tipo de Usuario: " + authUser.userTypeName());

        super.onStart();
    }
}
