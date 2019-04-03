package com.example.innomedicapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.innomedicapp.util.ServerUrl;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {

    String token = new String();
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.engine);

        SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        token = preferences.getString("token", "");
        context = this;

        if(token.length() < 10) {
            Intent newActivity = new Intent(context, LoginActivity.class);
            startActivity(newActivity);
            finish();
        } else
            this.checkAuth();
    }

    private void checkAuth() {

        String url = ServerUrl.getUrlApi() + "checkAuth";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        Intent principal = new Intent(context, PrincipalNav.class);
                        startActivity(principal);
                        finish();

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        System.out.println("Error.Response: " + error.getMessage());
                        SharedPreferences pref = getSharedPreferences("data", Context.MODE_PRIVATE);
                        pref.edit().remove("token").commit();
                        Intent login = new Intent(context, LoginActivity.class);
                        startActivity(login);
                        finish();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("token", token);
                return params;
            }
        };

        queue.add(postRequest);

    }

}
