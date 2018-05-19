package com.adans.app_10.Cowtech54;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.adans.app_10.GasYEmisAct;
import com.adans.app_10.GpsDataService;
import com.adans.app_10.MantBDD;
import com.adans.app_10.R;
import com.adans.app_10.SensorsService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CowTabFragment1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CowTabFragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CowTabFragment1 extends Fragment implements View.OnClickListener, LocationListener{

    private final String TAG = CowTabFragment1.class.getSimpleName();

    private static final String DISCONNECTED = "Disconnected";

    //Tv Gps not available
    TextView tvEdoGps;

    //Vars DB
    float VAX,VAY,VAZ,VGX,VGY,VGZ;
    float LAT;
    float LOG;
    String TS;Double ALT;
    float AGX,AGY,AGZ;
    int NOSts;
    Float Speed;

    //Delay [Sensores and Handler]
    public Double dly = 0.1;
    //Var Boolean est GPS Provider
    boolean EDOGPSBoo;

    private Handler mHandler = new Handler();

    //Instance Sensores Servicio
    SensorsService sensorserv;

    //Instance GPS App
    GpsDataService gpsapp;

    //Service and Observer RxJava
    CowService cowService;
    CowService.CowBinder cowBinder;
//    private ContentTestBinding binding; //See https://developer.android.com/topic/libraries/data-binding/
    Disposable disposable; //stackoverflow.com/questions/14695537/android-update-activity-ui-from-service
    Disposable disposable2;
    //   IBinder cowBinder;
    boolean sBound = false;

    // GUI Components
    CheckBox btRadio, sdRadio, lvRadio, obdRadio, gpsCheckbox;

    private TextView sStatusTxt;
    private TextView sDeviceNameTxt;
    private TextView sDeviceMacTxt;
    private TextView sFileTxt;
    private TextView sLatestLocationTxt;
    private TextView sLatTxt;
    private TextView sLonTxt;
    private TextView sSatTxt;

    private Button sStartBtn;
    private Button sStopBtn;
    private Button sBindBtn;
    private Button sUnbindBtn;
    private Button sUpdateBtn;

    private TextView sPairedDeviceTxt;


    //Preferences
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private OnFragmentInteractionListener mListener;

    public CowTabFragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CowTabFragment1.
     */

    public static CowTabFragment1 newInstance() {
        CowTabFragment1 fragment = new CowTabFragment1();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();

        gpsapp = new GpsDataService();
        sensorserv=new SensorsService();

        EDOGPSBoo = false;

        startGPSService();
        starBinder();

    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cow_tab1, container, false);
        //Mode radio buttons
        btRadio = (CheckBox) view.findViewById(R.id.modeBtRadio);
        sdRadio = (CheckBox) view.findViewById(R.id.modeSDRadio);
        lvRadio = (CheckBox) view.findViewById(R.id.modeLabviewRadio);
        obdRadio = (CheckBox) view.findViewById(R.id.modeOBDRadio);
        //Gps checkbox
        gpsCheckbox = (CheckBox) view.findViewById(R.id.checkboxGpsTab1);

        //TextViews
        sStatusTxt = (TextView) view.findViewById(R.id.txtStatusTab1);
        sDeviceNameTxt  = (TextView) view.findViewById(R.id.txtDeviceNameTab1);
        sDeviceMacTxt = (TextView) view.findViewById(R.id.txtDeviceMacTab1);
        sFileTxt = (TextView) view.findViewById(R.id.txtFileTab1);
        sLatestLocationTxt  = (TextView) view.findViewById(R.id.txtLatestGPSTab1);
        sLatTxt = (TextView) view.findViewById(R.id.txtLatTab1);
        sLonTxt = (TextView) view.findViewById(R.id.txtLonTab1);
        sSatTxt = (TextView) view.findViewById(R.id.txtSatTab1);
        tvEdoGps= (TextView) view.findViewById(R.id.tvEdoGPSFrac);


        sStartBtn = (Button) view.findViewById(R.id.cowTab1StartBtn);
        sStopBtn = (Button) view.findViewById(R.id.cowTab1StopBtn);
        sBindBtn = (Button) view.findViewById(R.id.cowTab1BindBtn);
        sUnbindBtn = (Button) view.findViewById(R.id.cowTab1UnbindBtn);

        sUpdateBtn = (Button) view.findViewById(R.id.cowTab1UpdateBtn);


        LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) (dly * 1000), 1, (android.location.LocationListener)this);



        //DEVICE
        //Mode radio buttons--------

        btRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioMode(view, "BT");
            }
        });
        sdRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioMode(view, "SD");
            }
        });
        lvRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioMode(view, "LV");
            }
        });
        obdRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioMode(view, "OBD");
            }
        });
        gpsCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioMode(view,"GPS");
            }
        });

        //Mode end radio buttons


        sStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (EDOGPSBoo == true) {
                    mToastRunnable.run();
                    //Toast.makeText(getApplicationContext(), "Guardando Datos, Cada " + dly + " Segundos", (int) (dly * 1000)).show();
                Intent intent = new Intent(getActivity(),CowService.class);
                getActivity().startService(intent);
                Log.d(TAG, "START BTN CLICKED");
                } else {
                    Toast.makeText(getApplicationContext(), "Espera la conexión del GPS", Toast.LENGTH_LONG).show();
                }
            }
        });
        sStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),CowService.class);
                getActivity().stopService(intent);
            }
        });
        sBindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(sBound) {
                    getActivity().unbindService(sServerConn);
                    //disposable.clear(); // do not send event after activity has been destroyed
                    disposable.dispose();
                    sStatusTxt.setText(DISCONNECTED);
                    sBound=false;
                }*/
                if (EDOGPSBoo == true) {
                    Intent intent = new Intent(getActivity(), CowService.class);
                    getActivity().bindService(intent, sServerConn, Context.BIND_AUTO_CREATE);
                    Intent gpsintent = new Intent(getActivity(), GpsDataService.class);
                    getActivity().bindService(gpsintent, sServerConn, Context.BIND_AUTO_CREATE);
                    Intent sensintent = new Intent(getActivity(), SensorsService.class);
                    getActivity().bindService(sensintent, sServerConn, Context.BIND_AUTO_CREATE);
                } else {
                Toast.makeText(getApplicationContext(), "Espera la conexión del GPS", Toast.LENGTH_LONG).show(); }
            }
        });
        sUnbindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CowService.class);
                if(sBound) {
                    getActivity().unbindService(sServerConn);
                    //disposable.clear(); // do not send event after activity has been destroyed
                    disposable.dispose();
                    sStatusTxt.setText(DISCONNECTED);
                    sBound=false;
                }
            }
        });
        sUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sBound){
                    String filePathStr;
                    filePathStr = cowService.getFileCSV().getFilePathStr();
                    String byteFirst = cowService.btMessageManager.MatIDs.get(2222).getByte1().getFirst().toString();
                    Toast.makeText(getActivity().getApplicationContext(), byteFirst, Toast.LENGTH_LONG).show();
                }
            }
        });

        //Update the paired device textview
        String pairedDeviceMac = prefs.getString("cow_paired_mac", "Not synced");


        return view;
    }

    //Radio buttons ===
    public void radioMode(View view, String mode){
        boolean checked = ((CheckBox) view).isChecked();
        if(cowService!=null) {
            if (checked)
                cowService.modeSend(mode, true);
            else
                cowService.modeSend(mode, false);
        }

        //If there is not connection Do not change the status of the Radio button
        isRadioConnected(view, checked);
    }
    void isRadioConnected(View view, boolean checked){
        if(cowService!=null) {
            if(!cowService.isDeviceConnected()){
                ((CheckBox) view).setChecked(!checked);
                Toast.makeText(getApplicationContext(), "Device not connected", Toast.LENGTH_SHORT).show();
            }
        }else{
            ((CheckBox) view).setChecked(!checked);
            String message = "Service not yet started";
            Log.d(TAG, message);
            Toast.makeText(getApplicationContext(), "Service not started", Toast.LENGTH_SHORT).show();
        }
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((CheckBox) view).isChecked();


        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.modeBtRadio:
                if(checked)
                    cowService.modeSend("BT",true);
                else
                    cowService.modeSend("BT",false);
                //If there is not connection Do not change the status of the Radio button
                isRadioConnected(view, checked);

                break;
            case R.id.modeSDRadio:
                if(checked)
                    cowService.modeSend("SD",true);
                else
                    cowService.modeSend("SD",false);
                isRadioConnected(view, checked);

                break;
            case R.id.modeLabviewRadio:
                if(checked)
                    cowService.modeSend("LV",true);
                else
                    cowService.modeSend("LV",false);
                isRadioConnected(view, checked);

                break;
            case R.id.modeOBDRadio:
                if(checked)
                    cowService.modeSend("OBD",true);
                else
                    cowService.modeSend("OBD",false);
                isRadioConnected(view, checked);

                break;
        }
    }


    protected ServiceConnection sServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            CowService.CowBinder binder = (CowService.CowBinder) service;
            cowService = binder.getService();

            //cowService.set
            sBound = true;
            Log.d(TAG, "onServiceConnected");
            //Disposable for the subscriber Called from CowService to update the UI
            disposable = cowService.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> sStatusTxt.setText(string[0]));
            disposable = cowService.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> sFileTxt.setText(string[1]));
            disposable = cowService.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> sLatTxt.setText(string[2]));
            disposable = cowService.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> sLonTxt.setText(string[3]));
            disposable = cowService.observeString()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> sSatTxt.setText(string[4]));

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            sBound = false;
            Log.d(TAG, "onServiceDisconnected");
        }
    };


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Update the paired device textview
        String pairedDeviceMac = prefs.getString("cow_paired_mac", "Not synced");
        //sPairedDeviceTxt.setText("Device: "+ pairedDeviceMac);
    }


    @Override
    public void onClick(View view) {

    }

    @Override
    public void onLocationChanged(Location location) {

        LAT = (float) location.getLatitude();
        LOG = (float) location.getLongitude();
        Speed = location.getSpeed();
        tvEdoGps.setText("Gps Available");
        EDOGPSBoo = true;

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        tvEdoGps.setText("Gps Available");
        EDOGPSBoo = true;

    }

    @Override
    public void onProviderEnabled(String provider) {
        tvEdoGps.setText("Gps Available");
        EDOGPSBoo = true;
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {
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

            mantBDD.agregarCurso(TS,VAX, VAY, VAZ, VGX, VGY, VGZ, AGX, AGY, AGZ, LAT, LOG, ALT, NOSts);

            //float AX=sensorserv.getAX(); float AY=sensorserv.getAY(); float AZ=sensorserv.getAZ();
            //tvPerfil.setText(String.valueOf(AX)); tvConsumo.setText(String.valueOf(AY));tvEmisiones.setText(String.valueOf(AZ));
            //ActLabels();

            Double dlyto = 0.1;//Segundos
            mHandler.postDelayed(this, (long) (dlyto * 1000));
        }
    };

    public void stopRepeating(View v) {
        mHandler.removeCallbacks(mToastRunnable);
        exportDatabse("BDDSensors");
        Intent intentE = new Intent(getApplicationContext(), GasYEmisAct.class);
        startActivity(intentE);
    }

    //Sql-Memory
    public void exportDatabse(String BDDSensors) {
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            File sdDow = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + getActivity().getPackageName() + "//databases//" + BDDSensors + "";
                String backupDBPath = ts + "backupBDD" + ".db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sdDow, backupDBPath);
                Toast.makeText(getApplicationContext(), "Guardando BDD", Toast.LENGTH_SHORT).show();

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
        }
    }

    private void starBinder() {
        Intent intent = new Intent(getApplicationContext(),SensorsService.class);
        getApplicationContext().bindService(intent, snsServerConn, Context.BIND_AUTO_CREATE);
        Intent sintent = new Intent(getApplicationContext(),GpsDataService.class);
        getApplicationContext().bindService(sintent,gServerConn, Context.BIND_AUTO_CREATE);
    }

    private void startGPSService() {
        Intent serintent= new Intent(getApplicationContext(),SensorsService.class);
        getApplicationContext().startService(serintent);
        Intent gpsintent=new Intent(getApplicationContext(),GpsDataService.class);
        getApplicationContext().startService(gpsintent);
    }

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
            Log.d(TAG, "onGPSServiceDisconnected"); }
    };
    protected ServiceConnection snsServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SensorsService.SensBinder snsbinder = (SensorsService.SensBinder) service;
            sensorserv = snsbinder.getService();
            sBound = true;
            Log.d(TAG, "onSensorsServiceConnected");}
        @Override
        public void onServiceDisconnected(ComponentName name) {
            sBound = false;
            Log.d(TAG, "onSensorsServiceDisconnected"); }
    };


}
