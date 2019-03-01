package com.example.innomedicapp.thread;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.innomedicapp.model.AuthUser;
import com.example.innomedicapp.util.ServerUrl;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class LastLocationChildThread extends Observable implements Runnable {

    public boolean background = true;

    Context context;
    Integer user_id;
    AuthUser auth;

    public LastLocationChildThread(Context c, Integer id) {
        context = c;
        user_id = id;
        auth = new AuthUser(context);
    }

    @Override
    public void run() {

        while(this.background) {

            try {

                final String url = ServerUrl.getUrlApi() + "location/lastLocation";
                RequestQueue queue = Volley.newRequestQueue(this.context);

                StringRequest postRequest = new StringRequest( Request.Method.POST, url,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                LastLocationChildThread.super.setChanged();
                                LastLocationChildThread.super.notifyObservers(response);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("Error.Response: " + error.getMessage());
                            }

                        }
                ) {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("token", auth.getToken());
                        params.put("user_id", user_id.toString());


                        return params;
                    }
                };

                queue.add(postRequest);

                Thread.sleep( 60000 );

            }catch (Exception e) {

                System.out.println(e);

            }

        }

    }
}
