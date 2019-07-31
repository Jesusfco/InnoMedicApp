package com.example.innomedicapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.innomedicapp.fragments.AssosiationsFragment;
import com.example.innomedicapp.fragments.PerfilFragment;
import com.example.innomedicapp.model.AuthUser;
import com.example.innomedicapp.thread.GPSTrackerThread;
import com.example.innomedicapp.util.BluetoothSerial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class PrincipalNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AuthUser authUser;

    private TextView userName, userEmail, userType, bluetoothConnectionText, battery, heart;

    GPSTrackerThread gpsthread;

    private Handler handler = new Handler();

    int testCounter = 1;

    //BLUETOOTH
    String bluetoothName;

    BluetoothAdapter bluetoothAdapter;
    Intent btEnablingIntent;
    ArrayList<String > stringArraList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;


    int REQUEST_ENABLE_BLUETOOTH = 1;

    BluetoothSerial bluetoothSerial;
    private BluetoothConnectReceiver bluetoothConnectReceiver = new BluetoothConnectReceiver();
    private BluetoothDisconnectReceiver bluetoothDisconnectReceiver = new BluetoothDisconnectReceiver();

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
        this.bluetoothConnectionText = (TextView)headerView.findViewById(R.id.bluetoothConnection);
        this.heart = (TextView)headerView.findViewById(R.id.heart);
        this.battery = (TextView)headerView.findViewById(R.id.battery);

        this.userName.setText(this.authUser.getName().toString());
        this.userEmail.setText(this.authUser.getEmail());
        this.userType.setText(this.authUser.userTypeName());

        //SEND LOCALIZATION CONSTANTLY
        if(this.authUser.getUser_type() == 1) {
            //this.startBluetooth();
            this.manageLocalitationLogic();
        }


        setTitle("Mis Contactos");
        getSupportFragmentManager().beginTransaction().replace(R.id.includeLayout,
                new AssosiationsFragment()).commit();


    }

    public void startBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btEnablingIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );

        if(bluetoothAdapter == null){
            Toast.makeText( this, "Tu dispositivo no soporta la utilizacion del Bluetooth", Toast.LENGTH_LONG ).show();
        } else {

            if(!bluetoothAdapter.isEnabled()) {

                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //REQUEST_ENABLE_BT = 1
                startActivityForResult( enableBluetoothIntent, 1 );

            } else  {
                this.setBluetoothConection();
            }

        }
    }

    public void setBluetoothConection() {
        if(bluetoothName == null)return;

        bluetoothConnectionText.setText( "Conectando..." );
        bluetoothSerial = new BluetoothSerial(this, new BluetoothSerial.MessageHandler() {
            @Override
            public int read(int bufferSize, byte[] buffer) {
                return doRead(bufferSize, buffer);
            }
        }, bluetoothName);

        //Fired when connection is established and also fired when onResume is called if a connection is already established.
        LocalBroadcastManager.getInstance(this).registerReceiver(bluetoothConnectReceiver, new IntentFilter(BluetoothSerial.BLUETOOTH_CONNECTED));
        //Fired when the connection is lost
        LocalBroadcastManager.getInstance(this).registerReceiver(bluetoothDisconnectReceiver, new IntentFilter(BluetoothSerial.BLUETOOTH_DISCONNECTED));
        //Fired when connection can not be established, after 30 attempts.
        LocalBroadcastManager.getInstance(this).registerReceiver(bluetoothDisconnectReceiver, new IntentFilter(BluetoothSerial.BLUETOOTH_FAILED));

    }

    public int  doRead(int bufferSize, byte[] buffer){

        String data = new String( buffer, 0, bufferSize );
        System.out.println(data);
        try {
            String[] parts = data.split(":");
            if(parts[0].toString().equals( "CORAZON" )) {
                this.heart.setText( parts[1] + " ppm" );
            } else if(parts[0].toString().equals( "BATERIA" )) {
                this.battery.setText( "Brazalete " + Math.round(Double.valueOf(parts[1])) + " % de carga" );
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        return bufferSize;
    }

    public class BluetoothConnectReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            bluetoothConnectionText.setText( "Pulsera Conectada" );
        }
    }

    public class BluetoothDisconnectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            bluetoothConnectionText.setText( "Sin Conexion" );
        }

    }

    //BLUETOOTH QUESTION USER IF WE CAN ENGINE BLUETOOTH ----------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1) {

            if(resultCode == RESULT_OK) { //BLUETOOTH IS ENABLED

                Toast.makeText( this, "Bluetooth habilitado", Toast.LENGTH_SHORT ).show();
                this.setBluetoothConection();

            } else if(resultCode == RESULT_CANCELED){ //BLUETOOT ENABLING IS CANCELED

                Toast.makeText( this, "El bluetooth no se encuentra habilitado es necesario habilitarlo para establecer conexion con la pulsera", Toast.LENGTH_LONG ).show();

            }

            else {

                Toast.makeText( this, "Bluetooth", Toast.LENGTH_SHORT ).show();
            }
        }
    }



    protected void onResume() {
        super.onResume();

        //onResume calls connect, it is safe
        //to call connect even when already connected
        if(this.authUser.getUser_type() == 1 && bluetoothSerial != null)
            bluetoothSerial.onResume();


        SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        String dev = preferences.getString("device", "");

        if(this.authUser.getUser_type() == 1 && dev.length() > 0 && !dev.equals( bluetoothName )) {

            bluetoothName = dev;

            if(bluetoothSerial != null) {
                this.bluetoothSerial.close();
                this.bluetoothSerial = null;
            }

            this.setBluetoothConection();

        }

    }

    protected void onPause() {

        super.onPause();
        if(this.authUser.getUser_type() == 1 && bluetoothSerial != null) {
            bluetoothSerial.onPause();
        }

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent monitoring = new Intent(this, BluetoothConfigActivity.class);
            startActivity(monitoring);
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
            setTitle("Mis Contactos");
            getSupportFragmentManager().beginTransaction().replace(R.id.includeLayout,
                    new AssosiationsFragment()).commit();

        /*} else if (id == R.id.nav_messages) {
            setTitle("Mensajes");
            getSupportFragmentManager().beginTransaction().replace(R.id.includeLayout,
                    new PerfilFragment()).commit();

        } else if (id == R.id.nav_manage) { */

        } else if (id == R.id.nav_perfil) {

            setTitle("Mi Perfil");
            getSupportFragmentManager().beginTransaction().replace(R.id.includeLayout,
                    new PerfilFragment()).commit();
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

        } else {
            this.startGPSThread();
        }



    }

    public void startGPSThread() {
        this.gpsthread = new GPSTrackerThread();
        this.gpsthread.context = this;
        new Thread(this.gpsthread).start();
    }

}
