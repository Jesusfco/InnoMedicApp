package com.example.innomedicapp;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.innomedicapp.util.BluetoothSerial;

import org.w3c.dom.Text;

import java.io.IOException;

public class BluetoothConfigActivity extends Activity {

    String bluetoothName;
    BluetoothSerial bluetoothSerial;
    private TextView statusText, bluetoothView, dataView;

    private BluetoothConnectReceiver bluetoothConnectReceiver = new BluetoothConnectReceiver();
    private BluetoothDisconnectReceiver bluetoothDisconnectReceiver = new BluetoothDisconnectReceiver();

    private BluetoothAdapter bluetoothAdapter;
    private Intent btEnablingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_bluetooth_config );

        this.bluetoothView = (TextView)findViewById( R.id.textView );
        this.statusText = (TextView)findViewById( R.id.textView2 );
        this.dataView = (TextView)findViewById( R.id.textView3 );

        SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        String dev = preferences.getString("device", "");
        if(dev.length() != 0) {
            bluetoothName = dev;
            this.bluetoothView.setText(  dev );
        } else {
            this.bluetoothView.setText( "Seleccione un dispositivo");
        }

        this.bluetoothView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBluetoothConection();
            }
        });

        this.startBluetooth();
    }

    public void setBluetoothConection() {

        if(bluetoothName == null)return;

        this.statusText.setText( "Estado: Conectando..." );
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

    public class BluetoothConnectReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            statusText.setText( "Estado: Conectado" );
        }
    }

    public class BluetoothDisconnectReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            statusText.setText( "Estado: Desconectado" );
        }
    }

    public int  doRead(int bufferSize, byte[] buffer){

        String mes = new String( buffer, 0, bufferSize );
        this.dataView.setText( mes );
        return bufferSize;
    }

    public void alto(View v){
        this.sendThroughtBluetooth( 1 );
    }

    public void regresa(View v){
        this.sendThroughtBluetooth( 2 );
    }

    public void come(View v){
        this.sendThroughtBluetooth( 3 );
    }

    public void pastilla(View v){
        this.sendThroughtBluetooth( 4 );
    }

    public void reportate(View v){
        this.sendThroughtBluetooth( 5 );
    }

    public void vibracion(View v){
        this.sendThroughtBluetooth( 5 );
    }

    private void sendThroughtBluetooth(Integer i) {


        try {
            if(!bluetoothSerial.write( i.toString().getBytes() ) ) {
                Toast.makeText( this, "No existe ninguna conexión a algun dispositivo", Toast.LENGTH_LONG ).show();
            } else {
                ThreadBluetooth thread = new ThreadBluetooth( bluetoothSerial, "0", 1500 );
                thread.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void startBluetooth() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btEnablingIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );

        if(bluetoothAdapter == null){
            Toast.makeText( this, "Tu dispositivo no soporta la utilizacion del Bluetooth", Toast.LENGTH_LONG ).show();
        } else {

            if(!bluetoothAdapter.isEnabled()) {

                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult( enableBluetoothIntent, 1 );

            } else {
                this.setBluetoothConection();
            }

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

        if(bluetoothSerial != null)
            bluetoothSerial.onResume();

        SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        String dev = preferences.getString("device", "");

        if(dev.length() > 0 && !dev.equals( bluetoothName )) {

            bluetoothName = dev;
            this.statusText.setText( "Estado: Buscando..." );
            if(bluetoothSerial != null) {

                this.bluetoothSerial.close();
                this.bluetoothSerial = null;

            }

            this.setBluetoothConection();

            this.bluetoothView.setText(  dev );

        }

    }

    protected void onPause() {
        super.onPause();

        if(bluetoothSerial != null)
            bluetoothSerial.onPause();

    }

    public void selectDevice(View view) {

        Intent monitoring = new Intent(this, BluetoothSelectActivity.class);
        startActivity(monitoring);

    }

    public class ThreadBluetooth extends Thread {

        private BluetoothSerial bluetoothSerial;
        private String data;
        private Integer time;


        public ThreadBluetooth(BluetoothSerial bluetoothSerial, String data, Integer integer) {
            this.bluetoothSerial = bluetoothSerial;
            this.data = data;
            this.time = integer;
        }

        @Override
        public void run() {

            try {

                Thread.sleep(this.time);


            } catch (InterruptedException e) {

                e.printStackTrace();

            }

            try {

                this.bluetoothSerial.write( "1".getBytes() );

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


}
