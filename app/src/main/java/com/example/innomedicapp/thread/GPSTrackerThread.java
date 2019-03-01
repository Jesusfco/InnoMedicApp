package com.example.innomedicapp.thread;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.innomedicapp.PrincipalNav;
import com.example.innomedicapp.model.AuthUser;
import com.example.innomedicapp.util.GPSTracker;
import com.example.innomedicapp.util.ServerUrl;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GPSTrackerThread extends Thread implements Runnable {

    public boolean background = true;

    public Context context;

    public LocationManager locationManager;

    public LocationListener locationListener = null;

    private Handler handler = new Handler();

    public GPSTrackerThread() {

        this.locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {


                saveLocation(location);

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
    }

    @Override
    public void run() {

        while(this.background) {

            try {

                this.locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);

                final Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                criteria.setAltitudeRequired(false);
                criteria.setBearingRequired(false);
                criteria.setSpeedRequired(false);
                criteria.setCostAllowed(true);
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

                try {

                    this.handler.post(new Runnable() {
                        @Override
                        public void run() {

                            int permissionCheck = ContextCompat.checkSelfPermission(context,
                                    Manifest.permission.ACCESS_FINE_LOCATION);
                            locationManager.requestSingleUpdate(criteria, locationListener, null);

                        }
                    });



                }catch (Exception e) {
                    System.out.println(e.getMessage());
                }


                Thread.sleep(60000);

            } catch (InterruptedException e) {

                e.printStackTrace();

            }

        }

    }

    private void saveLocation(final Location location) {

        final AuthUser auth = new AuthUser(this.context);

        final String url = ServerUrl.getUrlApi() + "location/store";
        RequestQueue queue = Volley.newRequestQueue(this.context);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
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
                params.put("latitude", Double.toString(location.getLatitude()));
                params.put("longitude", Double.toString(location.getLongitude()));

                return params;
            }
        };

        queue.add(postRequest);

    }

}
