package com.adans.app_10;

import static java.lang.StrictMath.abs;

public class Dif {


    public static double[] Deltas(double[] velInterp, double[] timeRPM) {
        double[] AcelInter=new double[velInterp.length];
        for (int i=0;i<=velInterp.length-5;i++){
            AcelInter[i]=((velInterp[i+1]-velInterp[i])/(timeRPM[i+1]-timeRPM[i]));

        }
        return AcelInter;
    }
    //Array of FC
    public static double[] fuleC(double[] velInterp, double[] acel) {
        double[] FuleC=new double[velInterp.length];
        for(int c=0;c<= velInterp.length-2;c++){
            //Inc Inicial
            double IncI=0.075;
            FuleC[c]=10.12+1.098*IncI+5.901*acel[c]-0.844*velInterp[c]+0.0354*Math.pow(velInterp[c],2)-0.0003*Math.pow(velInterp[c],3);
        }
        double FuleCAcum=0;
        for(int ct=0;ct<=velInterp.length-1;ct++){
            FuleCAcum = FuleCAcum+FuleC[ct];
        }
        return FuleC;
    }

    public static double[] Kmetros(double[] velInterp, double[] timeRPM) {

        double[] DistArry=new double[velInterp.length];
        for(int i=0;i<=velInterp.length-5;i++) {
            DistArry[i]=((abs(velInterp[i+1]-velInterp[i]))*(timeRPM[i+1]-timeRPM[i]));
        }
        return DistArry;
    }
}
