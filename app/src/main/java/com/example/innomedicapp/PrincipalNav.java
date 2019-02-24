package com.example.innomedicapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.innomedicapp.model.AuthUser;
import com.example.innomedicapp.thread.GPSTrackerThread;

public class PrincipalNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AuthUser authUser;

    private TextView userName, userEmail, userType, ctext;

    LocationListener locationListener;
    LocationManager locationManager;
    GPSTrackerThread gpsthread;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        this.authUser = new AuthUser(this);

        View headerView = navigationView.getHeaderView(0);

        this.userName = (TextView)headerView.findViewById(R.id.userName);
        this.userEmail = (TextView)headerView.findViewById(R.id.userEmail);
        this.userType = (TextView)headerView.findViewById(R.id.userType);
        this.ctext = (TextView)findViewById(R.id.coordenates);

        this.ctext.setText("pinche chus");

        this.userName.setText(this.authUser.getName().toString());
        this.userEmail.setText(this.authUser.getEmail());
        this.userType.setText(this.authUser.userTypeName());

        setTitle("Infantes Monitoreados");

        if(this.authUser.getUser_type() == 1)
            this.manageLocalitationLogic();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_assosiations) {
            // Handle the camera action
        } else if (id == R.id.nav_messages) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_perfil) {

        } else if (id == R.id.nav_close_session)
            this.closeSession();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void closeSession() {

        SharedPreferences pref = getSharedPreferences("data", Context.MODE_PRIVATE);
        pref.edit().remove("token").commit();

        Intent newActivity = new Intent(this, LoginActivity.class);
        startActivity(newActivity);
        finish();

    }

    public void manageLocalitationLogic() {

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

        }

        //this.startLocationManager();
        this.startGPSThread();

    }

    public void startGPSThread() {
        this.gpsthread = new GPSTrackerThread();
        this.gpsthread.context = this;
        new Thread(this.gpsthread).start();
    }

    public void startLocationManager() {
        // Acquire a reference to the system Location Manager
        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        this.locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {

                PrincipalNav.this.ctext.setText("" + location.getAltitude() + " " + location.getLatitude());
                locationManager.removeUpdates(locationListener);

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }


}
