package com.adans.app_10.Cowtech54;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.adans.app_10.R;
import com.adans.app_10.Dif;
import com.adans.app_10.SensorsService;

import java.text.DecimalFormat;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CowTabFragment2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CowTabFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CowTabFragment2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Decimal Fotmat.
    DecimalFormat df = new DecimalFormat("#.00");

    //Var Emisissions
    double Emissions;
    double FuleAcum;
    double deltaAcum;
    double deltaprom;

    //Inst of Cow Service
    CowService cowService2;

    CowTabFragment1 cowfrac1;

    double[] DistTotal;
    int[] Gears;

    TextView tvPerfil,tvConsumo,tvEmis;
    //Button btnStartUpd;
    int C=0;


    //Arrays FuleC
    double[] VelInterp,RPMInterp,AclInterp;
    double Fuleprom;
    double FuleC;

    int CTimer=0;

    double DistAcum;

    double[]FpromAry;
    double[]Distprom;


    double[] vel = {0,0,0,11,11,12,11,17,24,39,39,40,41,41,41,38,9,33,34,43,45,48,48,46,45,39,39,14,9,11,19,21,38,39,39,42,44,44,45,46,45,45,42,38,35,31,6,21,31,39,40,43,42,28,7,12,15,16,14,11,3};
    double[] RPM = {645,790,1525.5,1781.5,1267.25,1232.25,995.75,907.25,800.25,744.5,1688,1910.25,1914.5,1951.75,2100.25,1777.25,1718,1105,1040.25,1003.75,929,912.75,806.25,773.75,1455.75,1950.75,2061.75,1951.25,1879.5,2020.75,2274,2257.25,2330.25,2366.75,2380.25,2394.5,2433.25,2507.25,2508,2458.5,2338.75,2360,1961.25,1936.5,1728.25,1558.5,1344,2039.75,2032.25,2054.25,2097.5,2199,2379.5,2485,2564.5,2496,2441.25,1474.75,998.5,955.5,1476.75,1491.25,1106,1205.5,635.75,932.5,932,810.75,781.5,782,693};
    double[] timeVel = {3010956,3013023,3014210,3018012,3018425,3018838,3019322,3022696,3024089,3029773,3030173,3032026,3032598,3032918,3034029,3036538,3042324,3048734,3049136,3053224,3055332,3055986,3058435,3060531,3061673,3065100,3065418,3071064,3074557,3076665,3079171,3080051,3090543,3091255,3091662,3094102,3094896,3097757,3100247,3102400,3102700,3103570,3105769,3106657,3107143,3107628,3110809,3115272,3119063,3123460,3124252,3133275,3135385,3148708,3156154,3158326,3159221,3162106,3164397,3167321,3170301};
    double[] timeRPM = {3015000,3015783,3023118,3025904,3030973,3032358,3038395,3039270,3040090,3041369,3045505,3047270,3047754,3048256,3049544,3050444,3053882,3056856,3059638,3061265,3063680,3064622,3070575,3072698,3076265,3078027,3078198,3081225,3081924,3082319,3084156,3085067,3086713,3088316,3089025,3089984,3090779,3092884,3093371,3095216,3095605,3096005,3096883,3098471,3102242,3104877,3106174,3113889,3116982,3117473,3117874,3119392,3122970,3125401,3130183,3134472,3134972,3136691,3138946,3139346,3147238,3147736,3150744,3152280,3154306,3156958,3157452,3165404,3167710,3168967,3171587};
    double[] Acel;
    double[] velInterp, rpmInterp;
    double FuleProm;
    double[] EmisAry;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Handler nHandler = new Handler();

    private OnFragmentInteractionListener mListener;

    public CowTabFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CowTabFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static CowTabFragment2 newInstance(String param1, String param2) {
        CowTabFragment2 fragment = new CowTabFragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            Intent cowintent = new Intent(getApplicationContext(),CowService.class);
            getApplicationContext().bindService(cowintent, CowServerConn, Context.BIND_AUTO_CREATE);
        }

        cowfrac1=new CowTabFragment1();

    }

    protected ServiceConnection CowServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            CowService.CowBinder binder2 = (CowService.CowBinder) service;
            cowService2 = binder2.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onBindingDied(ComponentName name) {

        }

    };

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_cow_tab2, container, false);

        tvPerfil=(TextView)view.findViewById(R.id.tvPerfilFrac);
        tvConsumo=(TextView)view.findViewById(R.id.tvConsumoFrac);
        tvEmis=(TextView)view.findViewById(R.id.tvEmisFrac);
        //btnStartUpd=(Button)view.findViewById(R.id.btnSUpd);

        velInterp = Util.interpLinear(timeVel,vel,timeRPM);
        rpmInterp = Util.interpLinear(timeRPM,RPM,timeVel);

        Interp();
        UpdLabels();

        return view;
    }



    private void UpdLabels() {

        nToastRunnable.run();

    }

    private Runnable nToastRunnable = new Runnable() {

        @Override
        public void run() {
            if(Gears[CTimer]<=1){
                tvPerfil.setText("Calm");
                tvPerfil.setBackgroundColor(Color.BLUE);
            }
            if(Gears[CTimer]>1&&Gears[CTimer]<=3){
                tvPerfil.setText("Normal");
                tvPerfil.setBackgroundColor(Color.GREEN);
            }
            if(Gears[CTimer]>3&&Gears[CTimer]<=5){
                tvPerfil.setText("Sporty");
                tvPerfil.setBackgroundColor(Color.RED);
            }


            tvConsumo.setText(String.valueOf(df.format(FpromAry[CTimer])+" km/lt"));

            if(FpromAry[CTimer]<=4){
                tvConsumo.setBackgroundColor(Color.BLUE);
            }
            if(FpromAry[CTimer]>4&&FpromAry[CTimer]<=12){
                tvConsumo.setBackgroundColor(Color.GREEN);
            }
            if(FpromAry[CTimer]>12&&FpromAry[CTimer]<=20){
                tvConsumo.setBackgroundColor(Color.RED);
            }


            tvEmis.setText(String.valueOf(df.format(EmisAry[CTimer])+" gCO2"));

            if(EmisAry[CTimer]<=2){
                tvEmis.setBackgroundColor(Color.BLUE);
            }
            if(EmisAry[CTimer]>2&&EmisAry[CTimer]<=4){
                tvEmis.setBackgroundColor(Color.GREEN);
            }
            if(EmisAry[CTimer]>4&&EmisAry[CTimer]<=5){
                tvEmis.setBackgroundColor(Color.RED);
            }


            double dlyto = 5;//Segundos
            nHandler.postDelayed(this, (long) (dlyto * 1000));
            CTimer++;
            if(CTimer>=14){
                CTimer=0;
            }
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

    private void Interp() {
        //private double[]
        //double[] arrTsVel, double[] arrVel, double[] arrTsRPM, double[] arrRPM, double tmax, double tmin

        /*double Tlaps=tmax-tmin;
        int numCas= (int) (Tlaps/100);
        double[] TimeV= new double[numCas];
        for (int cv=0;cv<numCas;cv++) {
            TimeV[cv]=tmin+100;
        }
        VelInterp=Util.interpLinear(arrTsVel,arrVel,TimeV);
        IncInterp=Util.interpLinear(arrTsRPM,arrRPM,TimeV);*/

        Acel= Dif.Deltas(velInterp,timeRPM);
        double[] FuleC;
        FuleC=Dif.fuleC(velInterp,Acel);
        FuleAcum=0;
        FpromAry=new double[14];
        int CdFA=0;
        for(int ct=0;ct<=velInterp.length-1;ct++){
            FuleAcum = FuleAcum+FuleC[ct];
            if(ct%5==0&&FuleC[ct]!=0){
                FpromAry[CdFA]=(FuleAcum+FuleC[ct])/5;
                CdFA++;
                FuleAcum=0;
            }
        }
        //FuleProm=100/(FuleAcum/(velInterp.length));


        //Distan
        int CdDP=0;
        DistAcum = 0;
        Distprom = new double[14];
        EmisAry = new double[14];
        DistTotal=Dif.Kmetros(velInterp,timeRPM);
        for (int c=0;c<=DistTotal.length-1;c++){
            DistAcum=DistAcum+(DistTotal[c]/1000000);
            if(c%5==0&&FuleC[c]!=0){
                Distprom[CdDP]=(DistAcum+FuleC[c])/5;
                EmisAry[CdDP]=(1/((FpromAry[CdDP]*100)/(Distprom[CdDP])))*(8887/3.7854);
                CdDP++;
                DistAcum=0;
            }
        }

        /*deltaAcum=0;
        for (int ct=0;ct<=timeVel.length-2;ct++) {

            deltaAcum=deltaAcum+(timeVel[ct+1]-timeVel[ct]);
        }
        deltaprom=(deltaAcum)/timeVel.length;*/

        double ltsTotales;
        ltsTotales=1/((FuleProm)/(DistAcum));
        Emissions=ltsTotales*(8887/3.7854);

        //DS Pros
        double[] gear=new double[velInterp.length];
        for (int c=0;c<=velInterp.length-11;c++){
            gear[c]=velInterp[c]/rpmInterp[c];
        }
        Gears=new int[75];
        for (int c=0;c<=70;c++)
        {
            if (gear[c]<=0.01) { Gears[c]=1;}
            if (gear[c]>0.01&&gear[c]<=0.02) { Gears[c]=2;}
            if (gear[c]>0.02&&gear[c]<=0.03) { Gears[c]=3;}
            if (gear[c]>0.03&&gear[c]<=0.04) { Gears[c]=4;}
            if (gear[c]>0.04&&gear[c]<=0.05) { Gears[c]=5;}
        }
        int Cd=0;
        int Cdp=0;
        int[] CdC=new int[14];
        for(int c=0;c<70;c++){
            if (Gears[c+1]!=Gears[c]){Cd++;}
            //0 excep
            if(c%5==0&&Gears[c]!=0){
                CdC[Cdp]=Cd;
                Cdp++;
                Cd=0;
            }
        }

    }

}
