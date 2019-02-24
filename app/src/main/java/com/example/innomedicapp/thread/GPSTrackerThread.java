package com.example.innomedicapp.thread;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.example.innomedicapp.PrincipalNav;

public class GPSTrackerThread extends Thread implements Runnable {

    public boolean background = true;

    public Context context;
    public LocationManager locationManager;
    public LocationListener locationListener;
    @Override
    public void run() {

        while(this.background) {

            try {

                this.locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);


                this.locationListener = new LocationListener() {

                    public void onLocationChanged(Location location) {

                        System.out.println("" + location.getAltitude() + " " + location.getLatitude());

                        GPSTrackerThread.this.locationManager.removeUpdates(locationListener);
                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                    public void onProviderEnabled(String provider) {}

                    public void onProviderDisabled(String provider) {}
                };

                // Register the listener with the Location Manager to receive location updates
                int permissionCheck = ContextCompat.checkSelfPermission(this.context,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                try {

                    this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.locationListener);
                }catch (Exception e) {
                    System.out.println(e.getMessage());
                }


                Thread.sleep(10000);

            } catch (InterruptedException e) {

                e.printStackTrace();

            }

        }

    }
}
