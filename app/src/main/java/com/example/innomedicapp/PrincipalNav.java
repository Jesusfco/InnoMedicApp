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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class PrincipalNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AuthUser authUser;

    private TextView userName, userEmail, userType, bluetoothConnectionText;

    GPSTrackerThread gpsthread;

    private Handler handler = new Handler();


    //BLUETOOTH
    String bluetoothName = "H-C-2010-06-01";
    BluetoothDevice pulsera;
    BluetoothAdapter bluetoothAdapter;
    Intent btEnablingIntent;
    ArrayList<String > stringArraList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;

    int REQUEST_ENABLE_BLUETOOTH = 1;

    private static  final String APP_NAME = "INNOMEDIC";
    private static  final UUID MY_UUID =  UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public SendReceive sendReceive;

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

        this.userName.setText(this.authUser.getName().toString());
        this.userEmail.setText(this.authUser.getEmail());
        this.userType.setText(this.authUser.userTypeName());


        if(this.authUser.getUser_type() == 1)
            this.manageLocalitationLogic();

        setTitle("Mis Contactos");
        getSupportFragmentManager().beginTransaction().replace(R.id.includeLayout,
                new AssosiationsFragment()).commit();

        //BLUETOOTH ----------------------------------------------------------------------------------------------
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

                this.searchBracelet();
                //BluetoothStopThread blueStop = new BluetoothStopThread();
                //new Thread( blueStop ).start();

            }

        }



    }

private class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public  SendReceive(BluetoothSocket socket) {

            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {

                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();


            }catch (IOException e){
                System.out.println(e.getMessage());
            }

            inputStream=tempIn;
            outputStream =tempOut;


        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while(true) {

                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes, -1, buffer).sendToTarget();
                    Thread.sleep( 500 );
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

        public  void write(byte[] bytes) {
            try {
                outputStream.write( bytes );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}

    private class ClientClass extends Thread {

       private BluetoothDevice device;
       private BluetoothSocket socket;

       public ClientClass(BluetoothDevice device){

           this.device = device;
           try {
               this.socket = device.createRfcommSocketToServiceRecord( MY_UUID);
               //this.socket = device.createRfcommSocketToServiceRecord( MY_UUID);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }

       public void run(){
           try{
               socket.connect();
               Message message = Message.obtain();
               message.what = STATE_CONNECTED;
               handlerBluetooth.sendMessage( message );

               sendReceive = new SendReceive( socket );
               sendReceive.start();

           } catch (IOException e) {
               System.out.println(e.getMessage());
               e.printStackTrace();
               Message message = Message.obtain();
               message.what = STATE_CONNECTION_FAILED;
               handlerBluetooth.sendMessage( message );

           }
       }



    }

    private class  ServerClass extends Thread {
        private BluetoothServerSocket serverSocket;

        public ServerClass() {
            try {
                serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord( APP_NAME, MY_UUID );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public  void run() {
            BluetoothSocket socket = null;

            while(socket==null) {

                 try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                     handlerBluetooth.sendMessage( message );

                    socket = serverSocket.accept();
                 }catch (IOException e) {
                     e.printStackTrace();
                     Message message = Message.obtain();
                     message.what = STATE_CONNECTION_FAILED;
                     handlerBluetooth.sendMessage( message );
                     Toast.makeText( PrincipalNav.this, "DESCONECTADO DE LA PULSERA", Toast.LENGTH_SHORT ).show();
                 }

                 if(socket!= null) {
                     Message message = Message.obtain();
                     message.what = STATE_CONNECTED;
                     handlerBluetooth.sendMessage( message );
                     sendReceive = new SendReceive( socket );
                     sendReceive.start();
                    break;
                 }

            }
        }
    }

    //ESTADOS DE CONEXION BLUETOOTH-------------------------------- //ESTADOS DE CONEXION BLUETOOTH-------------------------------------
    Handler handlerBluetooth = new Handler( new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case STATE_LISTENING:
                    bluetoothConnectionText.setText("Buscando Pulsera");
                    break;
                case STATE_CONNECTING:
                    bluetoothConnectionText.setText("Conectando");
                    break;
                case STATE_CONNECTED:
                    bluetoothConnectionText.setText("Pulsera Conectada");
                    break;
                case STATE_CONNECTION_FAILED:
                    bluetoothConnectionText.setText("Sin Conexión a la pulsera");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff = (byte[]) message.obj;
                    String tempMsg = new String(readBuff, 0, message.arg1);
                    System.out.println(tempMsg);
                    break;
            }
            return true;
        }
    } );

    //BLUETOOTH QUESTION USER IF WE CAN ENGINE BLUETOOTH ----------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1) {

            if(resultCode == RESULT_OK) { //BLUETOOTH IS ENABLED

                Toast.makeText( this, "Bluetooth habilitado", Toast.LENGTH_SHORT ).show();
                this.searchBracelet();
                BluetoothStopThread blueStop = new BluetoothStopThread();
                new Thread( blueStop ).start();

            } else if(resultCode == RESULT_CANCELED){ //BLUETOOT ENABLING IS CANCELED

                Toast.makeText( this, "El bluetooth no se encuentra habilitado es necesario habilitarlo para establecer conexion con la pulsera", Toast.LENGTH_LONG ).show();

            }

            else {

                Toast.makeText( this, "Bluetooth", Toast.LENGTH_SHORT ).show();
            }
        }
    }

    public void searchBracelet() { //DISCOVER BLUETOOTH DEVICES
        bluetoothAdapter.startDiscovery();

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver( myReceiver, intentFilter );

    }

    public class BluetoothStopThread extends Thread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep( 30000 );
                bluetoothAdapter.cancelDiscovery();
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }

    BroadcastReceiver myReceiver = new BroadcastReceiver() { //THREAD SCAN BLUETOOTH FOR DEVICES
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals( action )) {
                BluetoothDevice device = intent.getParcelableExtra( BluetoothDevice.EXTRA_DEVICE );
                stringArraList.add(device.getName());
                System.out.println(device.getName());
                if(device.getName().equals( bluetoothName )) {
                    try {

                        ClientClass clientClass = new ClientClass( device );
                        clientClass.start();
                        bluetoothConnectionText.setText("Conectando");

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    bluetoothAdapter.cancelDiscovery();
                    Toast.makeText( PrincipalNav.this, "Conectado con la pulsera", Toast.LENGTH_SHORT ).show();

                }
                //arrayAdapter.notifyDataSetChanged();
            }

        }

    };

    public void getListBluetoothPaired() { //LIST PAIRED

        Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
        String[] devices = new String[bt.size()];
        int index = 0;

        if(bt.size() > 0) {

            for(BluetoothDevice device: bt) {

                devices[index] = device.getName();
                index++;

            }

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
            setTitle("Mis Contactos");
            getSupportFragmentManager().beginTransaction().replace(R.id.includeLayout,
                    new AssosiationsFragment()).commit();

        } else if (id == R.id.nav_messages) {
            setTitle("Mensajes");
            getSupportFragmentManager().beginTransaction().replace(R.id.includeLayout,
                    new PerfilFragment()).commit();

        } else if (id == R.id.nav_manage) {

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

        }

        this.startGPSThread();

    }

    public void startGPSThread() {
        this.gpsthread = new GPSTrackerThread();
        this.gpsthread.context = this;
        new Thread(this.gpsthread).start();
    }

}
