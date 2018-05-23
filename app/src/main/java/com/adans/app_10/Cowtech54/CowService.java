package com.adans.app_10.Cowtech54;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.adans.app_10.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public class CowService extends Service{
    private final String TAG = CowService.class.getSimpleName();
    //Strings to register to create intent filter for registering the recivers
    private static final String ACTION_STRING_SERVICE = "ToService";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";

    private static final String DISCONNECTED = "Disconnected";
    private static final String CONNECTED = "Connected";

    //CREATE Broadcast Receiver
    //

    // Binder given to clients
    private final IBinder cBinder = new CowBinder();

    private static Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread cConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private BluetoothAdapter mBTAdapter;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    //BtMessageManager
    public BtMessageManager btMessageManager;

    //Initilizing CSV File
    public BtMessageFiles fileCSV;

    //Preferences
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    //Device
    private String deviceMac;
    private String deviceName;
    private boolean deviceConnected = false;
    private String deviceConnectedStr = DISCONNECTED;

    //Service Errors
    private String error;

    //Observers
    private ObservableEmitter<String[]> stringObserver;
    private Observable<String[]> stringObservable;
    private String[] myEmitter = new String[10];

    public CowService() {
    }

    public boolean isDeviceConnected() {
        return deviceConnected;
    }

    public BtMessageFiles getFileCSV() {
        return fileCSV;
    }

    public void setFileCSV(BtMessageFiles fileCSV) {
        this.fileCSV = fileCSV;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getError() {
        return error;
    }

    //Counter of repetings
    public int CountReps=0;
    //Array Data [30]
    public double[] ArrTsVel= new double[45];;
    public double[] ArrVel= new double[45];;
    public double[] ArrTsSens= new double[45];;
    public double[] ArrInc= new double[45];

    double[] VelInterp, IncInterp;

    CowTabFragment1 cowf1= new CowTabFragment1();


    @Override
    public void onCreate() {
        super.onCreate();


        Log.d(TAG, "Cow Service started");
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        //Preferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                //this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        //Create BtMessageManager object
        btMessageManager = new BtMessageManager();

        //Init filcsv
        initBtMessageFiles();

        //Update the paired device textview
        deviceMac = sharedPref.getString("cow_paired_mac", "Not synced");
        deviceName = sharedPref.getString("cow_paired_name", "Not synced");

        //Load device and connect
        bluetoothConnectThread(deviceMac,  deviceName);
//        bluetoothConnectThread("98:D3:32:20:E2:08",  "COW");

        //Intent filter for BlueTooth status receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);

        mHandler = new Handler(){
            public void handleMessage(Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    //TODO string. substring( string.length()+1)   if equals ] then it is ok
                    btMessageManager.addNewMessage(readMessage);

                    //Using Copy to avoid conflict while saving in file
                    if(fileCSV!=null)
                        fileCSV.writeInFile(btMessageManager.getMessagePurgedCopy());
                    //mReadBuffer.setText(readMessage);

                    //Aquí
                    if(CountReps<=44)
                    {
                        ArrTsVel[CountReps]= (Double) btMessageManager.MatIDs.get(2037).getByte1().getLast();
                        ArrVel[CountReps]= (Double) btMessageManager.MatIDs.get(2037).getByte2().getLast();
                        ArrTsSens[CountReps]= Double.valueOf(cowf1.getTS());
                        ArrInc[CountReps]= Double.valueOf(cowf1.getAGY());
                    }
                    else {
                        Interp(ArrTsVel,ArrVel,ArrTsSens,ArrInc);
                    }
                    CountReps++;



                    //Send to tue UI activity through Observer
                    if(stringObserver!=null){
                        String fileStr = "";
                        if(fileCSV!=null)
                            fileStr = fileCSV.getFilePathStr();
                        String lat = btMessageManager.MatIDs.get(2222).getByte5().getLast().toString();
                        String lon = btMessageManager.MatIDs.get(2222).getByte6().getLast().toString();
                        String sat = btMessageManager.MatIDs.get(2222).getByte1().getLast().toString();
                        myEmitter[0] = deviceConnectedStr;
                        myEmitter[1] = fileStr;
                        myEmitter[2] = lat;
                        myEmitter[3] = lon;
                        myEmitter[4] = sat;

                        stringObserver.onNext(myEmitter);
                    }
                }

                if(msg.what == CONNECTING_STATUS){
                    if(msg.arg1 == 1)
                        Log.d(TAG,"Connected to device: " + (String)(msg.obj));
                        // mBluetoothStatus.setText("Connected to Device: " + (String)(msg.obj));
                    else {
                        Log.d(TAG, "Connection Fialed");
                        Toast.makeText(getBaseContext(), "Connection failed", Toast.LENGTH_SHORT).show();
                        if(cConnectedThread!=null) //If there is a Connected Thread initilized
                            cConnectedThread.cancel();
                        fileCSV.stopFiles();
                        mHandler.removeCallbacks(cConnectedThread); //Remove possible conflicts
                    }
                        //mBluetoothStatus.setText("Connection Failed");
                }
            }
        };
    }

    private double[] Interp(double[] arrTsVel, double[] arrVel, double[] arrTsSens, double[] arrInc) {
        VelInterp=Util.interpLinear(ArrTsVel,ArrVel,ArrTsSens);
        IncInterp=Util.interpLinear(ArrTsSens,ArrInc,ArrTsVel);
        return VelInterp;
    }

    //RxJava Observable
    public Observable<String[]> observeString(){
        if(stringObservable== null) {
            stringObservable = Observable.create(emitter -> stringObserver = emitter);
            stringObservable = stringObservable.share();
        }
        return stringObservable;
    }

    //send broadcast from activity to all receivers listening to the action "ACTION_STRING_ACTIVITY"
    private void sendBroadcast() {
        Intent new_intent = new Intent();
        new_intent.setAction(ACTION_STRING_ACTIVITY);
        sendBroadcast(new_intent);
    }
    public void initBtMessageFiles(){
        //Initilizing CSV File with stored preferences
        String extStore = System.getenv("EXTERNAL_STORAGE");
        File f_exts = new File(extStore);

        String dirStr = f_exts.getAbsolutePath() + File.separator + getResources().getString(R.string.app_name);

        /*String defaultString = getResources().getString(R.string.folder_string_default);
        String folderStr = sharedPref.getString(getString(R.string.folder_string), defaultString);

        defaultString = getResources().getString(R.string.prefix_string_default);
        String prefixStr = sharedPref.getString(getString(R.string.prefix_string), defaultString);

        int defaultInt = 5; //5 min of def
        int timeFileGenerator = sharedPref.getInt(getString(R.string.update_time_file), defaultInt);
       */

        String prefixStr;
        String folderStr;
        int timeFileGenerator;

        prefixStr = sharedPref.getString("cow_file_prefix", "WPQ");
        folderStr = sharedPref.getString("cow_folder","CTLogsWPQ");
        timeFileGenerator = Integer.valueOf(sharedPref.getString("cow_period_files", "3" ));

        fileCSV = new BtMessageFiles(prefixStr, folderStr);
        fileCSV.setParentDirStr(dirStr);
        //fileCSV.setFilePathTextView(filePathTxt);
        fileCSV.initTimerGenerator(timeFileGenerator);

        //Upd UI
        /*newTimeStatusTxt.setText(Integer.toString(timeFileGenerator));
        updateTimeTxt.setText(Integer.toString(timeFileGenerator));
        folderTxt.setText(folderStr);
        prefixTxt.setText(prefixStr);*/
    }

    //The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            //Device found
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            //Device is now connected
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
           //Done searching
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
            //Device is about to disconnect
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                deviceConnectedStr = "Device has disconnected";
                Log.d(TAG, deviceConnectedStr);
                if(cConnectedThread!=null)
                    cConnectedThread.cancel();
                //Send back to activity trhoug obsever the disconnected message
                myEmitter[0] = deviceConnectedStr;
                stringObserver.onNext(myEmitter);
            //Device has disconnected
            }
        }
    };

    public void bluetoothConnectThread(final String address, final String name){
        if(mBTAdapter.isEnabled()) {
            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    Log.d("BluetoothW", "Spawn threading");

                    try {
                        mBTSocket = createBluetoothSocket(device);

                        Log.d("BluetoothW", "bt socket created");
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        Log.d("BluetoothW", "bt socket TRYING TO connect!");
                        mBTSocket.connect();
                        Log.d("BluetoothW", "bt socket intitilized!");
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();

                            Log.d("BluetoothW", "bt socket TRYed, now Closed");
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(!fail) {
                        cConnectedThread = new ConnectedThread(mBTSocket);
                        cConnectedThread.start();

                        //Send mode messages
                        //modeSend("BT", true);

                        try{
                            Thread.sleep(2000); //Wait to send the 2nd message
                        }catch(InterruptedException e){

                        }
                        //modeSend("SD", true);

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer; // = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            deviceConnected = true;
            deviceConnectedStr = CONNECTED;
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        buffer = new byte[51200]; //supossing a 300Hz freq of IDs //byte[1024];
                        SystemClock.sleep(1000); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    deviceConnected = false;
                    deviceConnectedStr = DISCONNECTED;
                    break;
                }
            }

        }

        //    Call this from the main activity to send data to the remote device
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
                Thread.sleep(500);
                Log.d("BluetoothW", "Sending String as Byte");
            } catch (IOException | InterruptedException ie) { }
        }

        // Call this from the main activity to shutdown the connection
        public void cancel() {
            try {
                deviceConnected = false;
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public void modeSend(String subMode, Boolean state){

        if(cConnectedThread != null) //First check to make sure thread created
        {
            String sendFrame = "";
            Log.d("COW_MODE", "changed");
            if(state) {
                sendFrame = subMode+" on";
                Log.d("COW_MODE",subMode + " ON");
            }
            else{
                sendFrame = subMode+" off";
                Log.d("COW_MODE",subMode + " Off");
            }
            cConnectedThread.write(sendFrame);

        }



    }



    @Override
    public void onDestroy() {
        Log.d(TAG, "Cow Service Destroyed and STOPPED");
        deviceConnectedStr = DISCONNECTED;
        if(cConnectedThread!=null) //If there is a Connected Thread initilized
            cConnectedThread.cancel();
        fileCSV.stopFiles();
        mHandler.removeCallbacks(cConnectedThread); //Remove possible conflicts
        super.onDestroy();
        //deviceConnected = false; //make sure it is false
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class CowBinder extends Binder {
        CowService getService() {
            // Return this instance of LocalService so clients can call public methods
            return CowService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return cBinder;
    }

    public double[] getVelInterp() {
        return VelInterp;
    }
}
