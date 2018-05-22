package com.adans.app_10;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.Random;

import android.graphics.Color;

import com.opencsv.CSVWriter;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class PruebaAct extends AppCompatActivity implements LocationListener,GpsStatus.Listener{


    //Var Consumo Prom Km/l
    int ConsProm=20;
    //Var Emis Prom gr/l
    int EmisProm=400;


    //Vars DB
    float VAX,VAY,VAZ,VGX,VGY,VGZ;
    String TS;Double ALT;
    float AGX,AGY,AGZ;
    int NOSts;

    Double mLastMslAltitude;
    int LocAltitude;

    //Var Velocida
    Float Speed;

    Button btnMostrar;

    static final String TAG = PruebaAct.class.getSimpleName();

    private Handler mHandler = new Handler();

    //Delay en sensores y Handler
    public Double dly = 0.1;

    //Var Boolean estado del GPS
    boolean EDOGPSBoo = false;

    //tv y imageview Indicador;
    TextView tvPerfil,tvConsumo,tvEmisiones;
    Button btnIndic;
    int Veld = 40;

    //////
    //ImageView ivCel;
    TextView tvNot1, tvNot2;
    TextView tvEdoGPS;


    Button Start, Stop, Mostrar;

    LocationManager locationManager;

    //////

    float LOG, LAT;

    int CdP = 1;

    //No. de satelites
    int NoSats;
    TextView tvNoSats,tvSpeed;
    //Boolean del Bind
    boolean sBound = false;
    //Instancia GPS App
    GpsDataService gpsapp;
    //Instancia Sensores Servicio
    SensorsService sensorserv;
    //Tiempo
    Date currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);
        EDOGPSBoo = false;

        currentTime = Calendar.getInstance().getTime();

        //InInstancia de GPSApli
        gpsapp = new GpsDataService();
        sensorserv=new SensorsService();

        //Nombre del ususario
        String Nombre;

        //Textview de los indicadores
        tvPerfil=(TextView) findViewById(R.id.tvPerfil);
        tvConsumo=(TextView) findViewById(R.id.tvConsumo);
        tvEmisiones=(TextView) findViewById(R.id.tvEmisiones);


        Start = (Button) findViewById(R.id.btnStart);

        Stop = (Button) findViewById(R.id.btnStop);
        Stop.setVisibility(View.GONE);

        Mostrar = (Button) findViewById(R.id.btnMostrar);
        Mostrar.setVisibility(View.GONE);

        tvEdoGPS = (TextView) findViewById(R.id.tvEdoGPS);


        btnMostrar = (Button) findViewById(R.id.btnMostrar);



        //Recibe nombre Bundle
        Bundle XBundle = PruebaAct.this.getIntent().getExtras();

        if (XBundle != null) {
            Nombre = XBundle.getString("nomb");
        }

        /*
        //User Permission [Write SD]
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }*/

        btnMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mostrarCursos = new Intent(getApplicationContext(), Cursos.class);
                startActivity(mostrarCursos);
            }
        });

        startGPSService();
        starBinder();
        getLocation();

    }


    private void startGPSService() {
        Intent serintent= new Intent(getApplicationContext(),SensorsService.class);
        getApplicationContext().startService(serintent);
        Intent gpsintent=new Intent(getApplicationContext(),GpsDataService.class);
        getApplicationContext().startService(gpsintent);
    }

    private void starBinder() {
        Intent intent = new Intent(getApplicationContext(),SensorsService.class);
        getApplicationContext().bindService(intent, snsServerConn, Context.BIND_AUTO_CREATE);
        Intent sintent = new Intent(getApplicationContext(),GpsDataService.class);
        getApplicationContext().bindService(sintent,gServerConn, Context.BIND_AUTO_CREATE);
    }




    //////
    void getLocation() {

        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) (dly * 1000), 1, this);

        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }
    ///////



    ///BtnStar
    public void startRepeating(View v) {

        if (EDOGPSBoo == true) {
            //mHandler.postDelayed(mToastRunnable, 5000);
            mToastRunnable.run();
            Toast.makeText(this, "Guardando Datos, Cada " + dly + " Segundos", (int) (dly * 1000)).show();

            Stop.setVisibility(View.VISIBLE);

            tvEdoGPS.setText("");
            Start.setVisibility(View.GONE);

        } else {
            Toast.makeText(getApplicationContext(), "Espera la conexión del GPS", Toast.LENGTH_LONG).show();
        }
    }

    //BtnStop
    public void stopRepeating(View v) {
        mHandler.removeCallbacks(mToastRunnable);
        exportDatabse("BDDSensors");
        Intent intentE = new Intent(getApplicationContext(), GasYEmisAct.class);
        startActivity(intentE);
        stopGPSService();
    }

    private void stopGPSService() {
        Intent serintent= new Intent(getApplicationContext(),GpsDataService.class);
        getApplicationContext().stopService(serintent);
        Intent sensintent=new Intent(getApplicationContext(),SensorsService.class);
        getApplicationContext().stopService(sensintent);
        if(sBound) {
            getApplicationContext().unbindService(snsServerConn);
            getApplicationContext().unbindService(gServerConn);
            sBound=false;};}

    protected ServiceConnection snsServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SensorsService.SensBinder binder = (SensorsService.SensBinder) service;
            sensorserv = binder.getService();
            sBound = true;
            Log.d(TAG, "onSensorsServiceConnected");}
        @Override
        public void onServiceDisconnected(ComponentName name) {
            sBound = false;
            Log.d(TAG, "onSensorsServiceDisconnected"); }};

    protected ServiceConnection gServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GpsDataService.GPSBinder gpsbinder = (GpsDataService.GPSBinder) service;
            gpsapp = gpsbinder.getService();
            sBound = true;
            Log.d(TAG, "onGPSServiceConnected");}
        @Override
        public void onServiceDisconnected(ComponentName name) {
            sBound = false;
            Log.d(TAG, "onGPSServiceDisconnected"); }};



    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {
            /*
            final MantBDD mantBDD = new MantBDD(getApplicationContext());
            TS =sensorserv.getTs();
            VAX=sensorserv.getVAX();
            VAY=sensorserv.getVAY();
            VAZ=sensorserv.getVAZ();
            VGX=sensorserv.getVGX();
            VGY=sensorserv.getVGY();
            VGZ=sensorserv.getVGZ();
            AGX=sensorserv.getAX();
            AGY=sensorserv.getAY();
            AGZ=sensorserv.getAZ();
            ALT=gpsapp.getNMEAAlt();
            NOSts=gpsapp.getNoSats();


            //mantBDD.agregarCurso(TS,VAX, VAY, VAZ, VGX, VGY, VGZ, AGX, AGY, AGZ, LAT, LOG, ALT, NOSts);
            float AX=sensorserv.getAX(); float AY=sensorserv.getAY(); float AZ=sensorserv.getAZ();

            tvPerfil.setText(String.valueOf(AX)); tvConsumo.setText(String.valueOf(AY));tvEmisiones.setText(String.valueOf(AZ));

            //ActLabels();

            Double dlyto = 0.1;//Segundos
            mHandler.postDelayed(this, (long) (dlyto * 1000));
        */
        }
    };

    private void ActLabels() {
        //Valor random
            final int Min1 = 1;
            final int Max1 = 3;
            final int Vrandom1 = new Random().nextInt((Max1 - Min1) + 1) + Min1;
            switch (Vrandom1) {
                case 1:
                    tvPerfil.setText("  Agresivo");
                    tvPerfil.setBackgroundColor(Color.RED);
                    break;
                case 2:
                    tvPerfil.setText("  Moderado");
                    tvPerfil.setBackgroundColor(Color.GREEN);
                    break;
                case 3:
                    tvPerfil.setText("  Ecológico");
                    tvPerfil.setBackgroundColor(Color.BLUE);
                    break;
            }

        final int Min2 = ConsProm-8;
        final int Max2 = ConsProm+8;
        final int Vrandom2 = new Random().nextInt((Max2 - Min2) + 1) + Min2;
        tvConsumo.setText("  "+Vrandom2+" Km/lto");
        if(Vrandom2<ConsProm-3){
            tvConsumo.setBackgroundColor(Color.RED);
        }
        if(Vrandom2>=ConsProm-3&&Vrandom2<ConsProm+3){
            tvConsumo.setBackgroundColor(Color.GREEN);
        }
        if (Vrandom2>ConsProm+3){
            tvConsumo.setBackgroundColor(Color.GREEN);
        }

        final int Min3 = (int) (EmisProm-100);
        final int Max3 = (int) (EmisProm+100);
        final int Vrandom3 = new Random().nextInt((Max3 - Min3) + 1) + Min3;
        tvEmisiones.setText("  "+Vrandom3+" grCO2/km");
        if(Vrandom3<EmisProm-25){
            tvEmisiones.setBackgroundColor(Color.RED);
        }
        if(Vrandom3>=EmisProm-25&&Vrandom3<EmisProm+25){
            tvEmisiones.setBackgroundColor(Color.GREEN);
        }
        if (Vrandom3>EmisProm+25){
            tvEmisiones.setBackgroundColor(Color.BLUE);
        }
    }


    @Override
    public void onLocationChanged(Location location) {

        LAT = (float) location.getLatitude();
        LOG = (float) location.getLongitude();

        LocAltitude=gpsapp.getNoSats();

        tvEdoGPS.setText("Conexión con GPS Disponible");
        EDOGPSBoo = true;

        Speed= location.getSpeed();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //Sql-Memory
    public void exportDatabse(String BDDSensors) {

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            File sdDow = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + getPackageName() + "//databases//" + BDDSensors + "";
                String backupDBPath = "backupBDD" + CdP + ".db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sdDow, backupDBPath);
                Toast.makeText(this, "Guardando BDD" + "C:" + CdP, Toast.LENGTH_SHORT).show();

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
            CdP = CdP + 1;
        } catch (Exception e) {
        }
    }


    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                // TODO: get here the status of the GPS, and save into a GpsStatus to be used for satellites visualization;
                // Use GpsStatus getGpsStatus (GpsStatus status)
                // https://developer.android.com/reference/android/location/LocationManager.html#getGpsStatus(android.location.GpsStatus)
                break;
        }
    }
    public void exportCSV() throws IOException {
        CSVWriter writer = null;
        try
        {
            writer = new CSVWriter(new FileWriter("/sdcard/FileTry.csv"), ',');
            String[] entries = "first#second#third".split("#"); // array of your values
            writer.writeNext(entries);
            writer.close();
        }
        catch (IOException e)
        {
            //error
        }


    }
}


