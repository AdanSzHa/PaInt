package com.adans.app_10.Cowtech54;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adans.app_10.R;
import com.adans.app_10.SensorsService;

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

    CowService cowService2;

    CowTabFragment1 cowfrac1;

    TextView tvPerfil,tvConsumo,tvEmis;



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
        view = inflater.inflate(R.layout.fragment_cow_tab2, container, false);
        tvPerfil=(TextView) view.findViewById(R.id.tvPerfilFrac);
        UpdLabels();
        return null;
    }

    private void UpdLabels() {

        nToastRunnable.run();

    }

    private Runnable nToastRunnable = new Runnable() {
        @Override
        public void run() {

            if(cowfrac1.isEDOGPSBoo()) {
                tvPerfil.setText(String.valueOf(cowService2.getFuleprom()) + " lt/100km");
            }

            double dlyto = 10;//Segundos
            nHandler.postDelayed(this, (long) (dlyto * 1000));
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
}
