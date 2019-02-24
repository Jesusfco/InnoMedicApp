package com.example.innomedicapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Toast.makeText(this, "HOLAAAA", Toast.LENGTH_SHORT).show();
    }

    public void showToast(View view) {
        Toast.makeText(this, "HOLAAAA", Toast.LENGTH_SHORT).show();
    }
}
