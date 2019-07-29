package com.example.innomedicapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.innomedicapp.fragments.PerfilFragment;
import com.example.innomedicapp.model.User;
import com.example.innomedicapp.model.UserAssosiation;
import com.example.innomedicapp.thread.LastLocationChildThread;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;

public class MonitoringActivity extends FragmentActivity implements OnMapReadyCallback  {

    private LastLocationChildThread threadLocalitation;
    private static Thread localizationThread;
    private User user;
    View mView;
    MapView mMapView;
    GoogleMap mMap;

    TextView lastCoordanates, ppm, ppmTimeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_monitoring );

        try {
            user = (User) getIntent().getSerializableExtra( "user" );

            if(user.getUser_type() == 1)
                this.mapProcess();
            else  {
                mMapView = (MapView)findViewById(  R.id.map);
                mMapView.setVisibility( View.INVISIBLE );
            }
        }catch (Exception e) {
            System.out.println(e);
        }

        this.lastCoordanates = (TextView) findViewById( R.id.lastCoordanates );
        //this.ppm = (TextView)findViewById( R.id.ppm );
        //this.ppmTimeStamp = (TextView)findViewById( R.id.ppmTimeStamp );

    }

    public void mapProcess(){
        mMapView = (MapView)findViewById(  R.id.map);

        if(mMapView != null) {
            mMapView.onCreate( null );
            mMapView.onResume();
            mMapView.getMapAsync( this );
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

                    lastCoordanates.setText( json.getString( "created_at" ) );

                } catch (Exception e) {
                    System.out.println(e
                    );
                }

            }

        });

        localizationThread = new Thread(this.threadLocalitation);
        localizationThread.start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize( this );
        mMap = googleMap;
    }

    public void startNeckaceActivity(View view){
        Intent newActivity = new Intent(this, InteractNecklaceActivity.class);
        startActivity(newActivity);

    }


}
