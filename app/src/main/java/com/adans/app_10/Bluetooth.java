package com.adans.app_10;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

//import com.example.luisguzmn.healthcare40.Helo.HeloConnection;

import java.util.ArrayList;
import java.util.Set;

public class Bluetooth extends AppCompatActivity {

    //VARIABLES
    BluetoothClass bluetoothClass;
    public String textoPosicion;
    Button b_on, b_off, b_disc, b_paired, b_discover;
    ListView list;
    BluetoothAdapter bluetoothAdapter;
    private static final int REQUEST_ENABLED = 0;
    private static final int REQUEST_DISCOVERABLE = 0;
    SharedPreferences infoBluetooth;
    SharedPreferences spLogin;
    Button buttonHelo, btnJabra, btnCow;
    private String name;
    private String type;
    private String macAdress;

    ArrayAdapter<String> BTarrayAdapter;
    //
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        //SHARED PREFERENCES
        infoBluetooth = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editorBluetooth = infoBluetooth.edit();
        spLogin=PreferenceManager.getDefaultSharedPreferences(this);
        //
        //CAST
        b_on = (Button) findViewById(R.id.on);
        b_off = (Button) findViewById(R.id.off);
        b_paired = (Button) findViewById(R.id.btnPaired);
        b_discover = (Button) findViewById(R.id.btnDiscover);
        list = (ListView) findViewById(R.id.viewlist);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        buttonHelo = (Button)findViewById(R.id.buttonHelo);
        btnJabra = (Button)findViewById(R.id.btnJabra);
        btnCow = (Button)findViewById(R.id.btnCow);

        //

        if (bluetoothAdapter == null) {

            Toast.makeText(this, "Bluetooth no disponible", Toast.LENGTH_LONG).show();
        }
        //BLUETOOTH START
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        ArrayList<String> devices = new ArrayList<String>();

        for (BluetoothDevice bt : pairedDevices) {
            BluetoothClass bluetoothClass = bt.getBluetoothClass();
            int tipoDeDispositivo = bluetoothClass.getDeviceClass();
            devices.add("Nombre: " + bt.getName() + "\nClass: " + tipoDeDispositivo + "\nDIRECCIÓN MAC: " + bt.getAddress());
        }
        BTarrayAdapter = new ArrayAdapter(Bluetooth.this, android.R.layout.simple_list_item_1, devices);
        list.setAdapter(BTarrayAdapter);
        ///
        ////////////////////
        /////BUTTONS
        b_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLED);
            }
        });
        b_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothAdapter.disable();
            }
        });

        b_paired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

               // ArrayList<String> devices = new ArrayList<String>();
                BTarrayAdapter.clear();

                for (BluetoothDevice bt : pairedDevices) {
                    bluetoothClass = bt.getBluetoothClass();
                    int tipoDeDispositivo = bluetoothClass.getDeviceClass();
                  //  devices.add("Nombre: " + bt.getName() + "\n Clase: " + tipoDeDispositivo + "\n Dirección MAC: " + bt.getAddress());
                    BTarrayAdapter.add("Nombre: " + bt.getName() + "\nClase: " + tipoDeDispositivo + "\nDirección MAC: " + bt.getAddress());

                }
            }
        });
        b_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discover(view);
            }
        });
        btnJabra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        buttonHelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(spLogin.getBoolean("success",false)==false) {
                    Intent intent = new Intent(Bluetooth.this, crearCuentaHelo.class);
                    startActivity(intent);
                }else {
                    startActivity(new Intent(Bluetooth.this, HeloConnection.class));
                }*/
            }
        });
        btnCow.setOnClickListener(new View.OnClickListener() {
          @Override
              public void onClick(View view) {
              Toast.makeText(getApplicationContext(), "COW mac updated:" + macAdress, Toast.LENGTH_LONG).show();

              editorBluetooth.putString("cow_paired_name",name);
              editorBluetooth.putString("cow_paired_mac",macAdress);
              editorBluetooth.apply();
              }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String textoPosicion = String.valueOf(adapterView.getItemAtPosition(position));
                String device[] = textoPosicion.split("\n", 5);
                name = device[0].substring(8);
                type = device[1].substring(7);
                macAdress = device[2].substring(15);
                editorBluetooth.putString("name", name);
                editorBluetooth.putString("type", type);
                editorBluetooth.putString("macAdress", macAdress);
                editorBluetooth.apply();
                ShowDialog();
            }
        });

        name = infoBluetooth.getString("name", "No name found");
        type = infoBluetooth.getString("type", "No type found");
        macAdress = infoBluetooth.getString("macAdress", "No MAC ADRESS found");

    }

    private void discover(View view){


        // Check if the device is already discovering
        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(bluetoothAdapter.isEnabled()) {
                BTarrayAdapter.clear(); // clear items
                bluetoothAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();

                // Register for broadcasts when a device is discovered.
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

                Log.d("BT", "Dev found f2");
//                popupDevices();
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.d("BT", "Broadcast Receiver init");
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                Log.d("BT", "Dev found f1");

                BluetoothClass bluetoothClass = device.getBluetoothClass();
                int deviceType = bluetoothClass.getDeviceClass();

                BTarrayAdapter.add("Nombre: " + device.getName() + "\nClase: " + deviceType + "\nDirección MAC: " + device.getAddress());
                BTarrayAdapter.notifyDataSetChanged();

            }
        }
    };

    public void ShowDialog() {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("DISPOSITIVO");
        builder.setMessage("Seleccionaste: " + infoBluetooth.getString("name", "No name found") + "\n ¿Quiéres continuar?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(infoBluetooth.getString("name","No name found").equalsIgnoreCase("HeloLx")) {
                    Intent intent = new Intent(Bluetooth.this, PrincipalDashboard.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(Bluetooth.this, "No HELOLX", Toast.LENGTH_LONG).show();
                }
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //finish();
                    }
                });
        builder.create().show();*/

    }

    public void onPause(){
        super.onPause();
        finish();
    }
}