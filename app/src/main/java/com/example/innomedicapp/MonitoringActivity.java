package com.example.innomedicapp;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.innomedicapp.model.User;
import com.example.innomedicapp.model.UserAssosiation;
import com.example.innomedicapp.thread.LastLocationChildThread;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

public class MonitoringActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LastLocationChildThread threadLocalitation;
    private static Thread localizationThread;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_monitoring );

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );

        mapFragment.getMapAsync( this );


        try {
            user = (User) getIntent().getSerializableExtra( "user" );
        }catch (Exception e) {
            System.out.println(e);
        }

        this.threadLocalitation = new LastLocationChildThread( this, this.user.getId());
        this.threadLocalitation.addObserver( new Observer() {

            @Override
            public void update(Observable obs, Object obj) {

                String string = (String) obj;

                try {
                    JSONObject json = new JSONObject( string );
                    if(json.isNull( "id" )) {

                        return;
                    }

                    if(mMap == null) return;

                    LatLng coordenates = new LatLng( json.getDouble( "latitude" ), json.getDouble( "longitude" ) );
                    mMap.addMarker( new MarkerOptions().position( coordenates ).title( "Localizacion de " + user.getName()) ).showInfoWindow();
                    mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( coordenates, 14.5f ) );


                } catch (Exception e) {
                    System.out.println(e
                    );
                }

            }

        });

        localizationThread = new Thread(this.threadLocalitation);
        localizationThread.start();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
    }
}
