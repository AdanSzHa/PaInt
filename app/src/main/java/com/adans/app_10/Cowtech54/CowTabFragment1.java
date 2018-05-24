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
import com.opencsv.CSVWriter;

import org.apache.poi.ss.formula.eval.StringValueEval;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

    //TextVier EdoGps
    TextView tvEdoGpsFrac;
    //Vat Boolean EdoGPS
    boolean EDOGPSBoo;
    //GPS Vars
    String LAT,LOG,Speed;
    //Var Delay
    Double dly=0.1;
    //Location Manager
    LocationManager lm;
    //Instancia GPS App
    GpsDataService gpsapp;
    //Instancia Sensores Servicio
    SensorsService sensorserv;
    //Handler
    private Handler mHandler = new Handler();
    //Vars DB
    String TS,VAX,VAY,VAZ,VGX,VGY,VGZ,ALT,AGX,AGY,AGZ,NOSts;
    String FC;

    //Timestamp
    String ts;
    public static String tss;
    //Data line
    String dataline;

    private String dateString2; //format yyMMdd_hhmm_ss_SSS

    //Preferences
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private OnFragmentInteractionListener mListener;

    //Data Wirter CSV
    CSVWriter DBCSVwriter;
    FileWriter mFileWriter;

    //Timestamp
    Long tsLong;

    //Var hour
    int hr;

    String fecha;

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

        EDOGPSBoo = false;

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();

        gpsapp=new GpsDataService();
        sensorserv=new SensorsService();

        tsLong = System.currentTimeMillis()/1000;
        ts = String.valueOf(tsLong);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 24);
        hr = cal.get(Calendar.HOUR);

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
        tvEdoGpsFrac= (TextView) view.findViewById(R.id.tvEdoGPSFrac);

        sStartBtn = (Button) view.findViewById(R.id.cowTab1StartBtn);
        sStopBtn = (Button) view.findViewById(R.id.cowTab1StopBtn);
        sBindBtn = (Button) view.findViewById(R.id.cowTab1BindBtn);
        sUnbindBtn = (Button) view.findViewById(R.id.cowTab1UnbindBtn);
        sUpdateBtn = (Button) view.findViewById(R.id.cowTab1UpdateBtn);

        //Loc MAnager
        lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
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
                if(EDOGPSBoo) {
                    Intent intent = new Intent(getActivity(), CowService.class);
                    getActivity().startService(intent);
                    Log.d(TAG, "START BTN CLICKED");
                    //Services Methods
                    startGPSService();
                    starBinder();
                    //DB Saver
                    startRepeating();
                }else {
                    Toast.makeText(getApplicationContext(), "Espera la conexión del GPS", Toast.LENGTH_LONG).show(); }
            }
        });
        sStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),CowService.class);
                getActivity().stopService(intent);
                stopRepeating();
            }
        });
        sBindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sBound) {
                    getActivity().unbindService(sServerConn);
                    //disposable.clear(); // do not send event after activity has been destroyed
                    disposable.dispose();
                    sStatusTxt.setText(DISCONNECTED);
                    sBound=false;

                }
                if(EDOGPSBoo) {
                    Intent intent = new Intent(getActivity(), CowService.class);
                    getActivity().bindService(intent, sServerConn, Context.BIND_AUTO_CREATE);

                    //Services Methods
                    startGPSService();
                    starBinder();
                    //DB Saver
                    startRepeating();
                    tss=ts;

                    String pairedDeviceMac = prefs.getString("cow_paired_mac", "Not synced");
                    String pairedDevice = prefs.getString("cow_paired_name", "COW_UNSYNCED");
                    sDeviceMacTxt.setText(pairedDeviceMac);
                    sDeviceNameTxt.setText(pairedDevice);
                }else {
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
                    stopRepeating();
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
        LAT = String.valueOf(location.getLatitude());
        LOG = String.valueOf(location.getLongitude());
        Speed = String.valueOf(location.getSpeed());
        tvEdoGpsFrac.setText("Gps Available");
        EDOGPSBoo = true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        tvEdoGpsFrac.setText("Gps Available");
        EDOGPSBoo = true;
    }

    @Override
    public void onProviderEnabled(String provider) {
        tvEdoGpsFrac.setText("Gps Available");
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

    public void startRepeating() {

            mToastRunnable.run();
            Toast.makeText(getApplicationContext(), "Guardando Datos, Cada " + dly + " Segundos", (int) (dly * 1000)).show();
        /*
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date();
            fecha = dateFormat.format(date);*/

    }

    private Runnable mToastRunnable = new Runnable() {

        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName =(getDateString()+"_DBPrueba"+".csv");
        String filePath = baseDir + File.separator + fileName;
        File f = new File(filePath );
        @Override
        public void run() {
            final MantBDD mantBDD = new MantBDD(getApplicationContext());
            TS =sensorserv.getTs();
            VAX=String.valueOf(sensorserv.getVAX());
            VAY=String.valueOf(sensorserv.getVAY());
            VAZ=String.valueOf(sensorserv.getVAZ());
            VGX=String.valueOf(sensorserv.getVGX());
            VGY=String.valueOf(sensorserv.getVGY());
            VGZ=String.valueOf(sensorserv.getVGZ());
            AGX=String.valueOf(sensorserv.getAX());
            AGY=String.valueOf(sensorserv.getAY());
            AGZ=String.valueOf(sensorserv.getAZ());
            ALT=String.valueOf(gpsapp.getNMEAAlt());
            NOSts=String.valueOf(gpsapp.getNoSats());
            //FC=String.valueOf(cowService.getFuleprom());

            mantBDD.agregarCurso(TS,VAX, VAY, VAZ, VGX, VGY, VGZ, AGX, AGY, Speed, LAT, LOG, ALT, NOSts);
            //float AX=sensorserv.getAX(); float AY=sensorserv.getAY(); float AZ=sensorserv.getAZ();

            try {
                // IF File exist
                if (f.exists() && !f.isDirectory()) {
                    mFileWriter = new FileWriter(filePath, true);
                    DBCSVwriter = new CSVWriter(mFileWriter);
                } else {
                    DBCSVwriter = new CSVWriter(new FileWriter(filePath));
                }
                //String[] data = {"Ship Name","Scientist Name", "...",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").formatter.format(date)});

                String[] entries = new String[15];// array of your values
                entries[0] = TS;entries[1] = VAX;entries[2] = VAY;entries[3] = VAZ;entries[4] = VGX;
                entries[5] = VGY;entries[6] = VGZ;entries[7] = AGX;entries[8] = AGY;entries[9] = AGZ;
                entries[10] = LAT;entries[11] = LOG;entries[12] = ALT;entries[13] = NOSts;entries[14] = Speed;
                //entries[15] = FC;

                String[] speedprm= new String[300];

                DBCSVwriter.writeNext(entries);

                DBCSVwriter.close();
            }catch (IOException e)
            {
                //error
            }

            Double dlyto = 0.1;//Segundos
            mHandler.postDelayed(this, (long) (dlyto * 1000));
        }

    };

    public void stopRepeating() {
        mHandler.removeCallbacks(mToastRunnable);
        exportDatabse("BDDSensors");
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
            sBound=false;};
    }
    //Sql-Memory
    public void exportDatabse(String BDDSensors) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            File sdDow = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + getActivity().getPackageName() + "//databases//" + BDDSensors + "";
                String backupDBPath = getDateString() + " backupBDD" + ".db";
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

    public String getDataline() {
        return dataline;
    }
    public String getDateString() {
        long tsLong;
        tsLong = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmm_ss_SSS");//"MMM dd,yyyy HH:mm:ss");
        Date resultdate = new Date(tsLong);
        String rsltDate = sdf.format(resultdate);
        dateString2 = rsltDate;
        return dateString2;
    }

    public String getTS() {
        return TS;
    }

    public String getAGY() {
        return AGY;
    }

    public boolean isEDOGPSBoo() {
        return EDOGPSBoo;
    }
}
