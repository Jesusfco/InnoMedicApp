package com.example.innomedicapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothSelectActivity extends AppCompatActivity {

    private ListView contactList;
    private List<String> dispositivos = new ArrayList<>();
    private List<String> addresses = new ArrayList<>();
    private TextView deviceSelectedView;

    BluetoothAdapter bluetoothAdapter;
    Intent btEnablingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_bluetooth_select );

        this.deviceSelectedView = (TextView) findViewById( R.id.deviceSelect );

        SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        String dev = preferences.getString("device", "");
        if(dev.length() != 0) {
            this.deviceSelectedView.setText( "Dispositivo: " + dev );
        } else {
            this.deviceSelectedView.setText( "Seleccione un dispositivo ");
        }

        this.contactList = (ListView) findViewById(R.id.list);

        this.contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("device",  dispositivos.get( position ) );
                editor.commit();

                finish();

            }

        });

        this.startBluetooth();

    }

    public void startBluetooth() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btEnablingIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );

        if(bluetoothAdapter == null){
            Toast.makeText( this, "Tu dispositivo no soporta la utilizacion del Bluetooth", Toast.LENGTH_LONG ).show();
        } else {

            if(!bluetoothAdapter.isEnabled()) {

                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult( enableBluetoothIntent, 1 );

            }

            if(bluetoothAdapter.isEnabled()) {
                this.getListBluetoothPaired();
            }

        }
    }

    public void getListBluetoothPaired() { //LIST PAIRED

        Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
        String[] devices = new String[bt.size()];
        int index = 0;

        if(bt.size() > 0) {

            this.dispositivos = new ArrayList<>();

            for(BluetoothDevice device: bt) {

                this.dispositivos.add( device. getName());
                this.addresses.add( device.getAddress() );

            }

            this.setContactList();

        }

    }

    @SuppressLint("WrongConstant")
    public void setContactList(){

        if(this.dispositivos.isEmpty()) {
            this.contactList.setVisibility( 0 );
            return;
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(  this, R.layout.list_devices_bluetooth, this.dispositivos);
            this.contactList.setAdapter( adapter );
        }

    }

}
