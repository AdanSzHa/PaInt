package com.adans.app_10.JavaTest;
import com.adans.app_10.Cowtech54.Util;
import com.adans.app_10.Dif;

/**
 * Created by Wily on 17/05/2018.
 */

public class TestInterpolation {

    public static void main (String[] args){
        double[] vel = {0,0,0,11,11,12,11,17,24,39,39,40,41,41,41,38,9,33,34,43,45,48,48,46,45,39,39,14,9,11,19,21,38,39,39,42,44,44,45,46,45,45,42,38,35,31,6,21,31,39,40,43,42,28,7,12,15,16,14,11,3};
        double[] RPM = {645,790,1525.5,1781.5,1267.25,1232.25,995.75,907.25,800.25,744.5,1688,1910.25,1914.5,1951.75,2100.25,1777.25,1718,1105,1040.25,1003.75,929,912.75,806.25,773.75,1455.75,1950.75,2061.75,1951.25,1879.5,2020.75,2274,2257.25,2330.25,2366.75,2380.25,2394.5,2433.25,2507.25,2508,2458.5,2338.75,2360,1961.25,1936.5,1728.25,1558.5,1344,2039.75,2032.25,2054.25,2097.5,2199,2379.5,2485,2564.5,2496,2441.25,1474.75,998.5,955.5,1476.75,1491.25,1106,1205.5,635.75,932.5,932,810.75,781.5,782,693};
        double[] timeVel = {3010956,3013023,3014210,3018012,3018425,3018838,3019322,3022696,3024089,3029773,3030173,3032026,3032598,3032918,3034029,3036538,3042324,3048734,3049136,3053224,3055332,3055986,3058435,3060531,3061673,3065100,3065418,3071064,3074557,3076665,3079171,3080051,3090543,3091255,3091662,3094102,3094896,3097757,3100247,3102400,3102700,3103570,3105769,3106657,3107143,3107628,3110809,3115272,3119063,3123460,3124252,3133275,3135385,3148708,3156154,3158326,3159221,3162106,3164397,3167321,3170301};
        double[] timeRPM = {3015000,3015783,3023118,3025904,3030973,3032358,3038395,3039270,3040090,3041369,3045505,3047270,3047754,3048256,3049544,3050444,3053882,3056856,3059638,3061265,3063680,3064622,3070575,3072698,3076265,3078027,3078198,3081225,3081924,3082319,3084156,3085067,3086713,3088316,3089025,3089984,3090779,3092884,3093371,3095216,3095605,3096005,3096883,3098471,3102242,3104877,3106174,3113889,3116982,3117473,3117874,3119392,3122970,3125401,3130183,3134472,3134972,3136691,3138946,3139346,3147238,3147736,3150744,3152280,3154306,3156958,3157452,3165404,3167710,3168967,3171587};
        double[] Acel;

        double[] velInterp, rpmInterp;
        velInterp = Util.interpLinear(timeVel,vel,timeRPM);
        rpmInterp = Util.interpLinear(timeRPM,RPM,timeVel);

        //DS Pros
        double[] gear=new double[velInterp.length];
        for (int c=0;c<=velInterp.length-11;c++){
            gear[c]=velInterp[c]/rpmInterp[c];
        }
        int[] Gears=new int[75];
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


        Acel= Dif.Deltas(velInterp,timeRPM);
        //AclInterp = Util.interpLinear(timeVel,Acel,timeRPM);

        double[] FuleC;
        FuleC=Dif.fuleC(velInterp,Acel);
        double FuleAcum=0;
        int CdFA=0;
        double[] FpromAry=new double[14];
        for(int ct=0;ct<=velInterp.length-1;ct++) {
            FuleAcum = FuleAcum + FuleC[ct];
            if (ct % 5 == 0 && FuleC[ct] != 0) {
                FpromAry[CdFA] = (FuleAcum + FuleC[ct])/5;
                CdFA++;
                FuleAcum = 0;
            }
        }
        //FuleProm=100/(FuleAcum/(velInterp.length));


        double FuleAcumMuestra=0;
        for(int ct=0;ct<=39;ct++){
            FuleAcumMuestra = FuleAcum+FuleC[ct];
        }
        double FulePromMuestra=100/(FuleAcumMuestra/(40));


        int CdDP=0;
        double DistAcum = 0;
        double[] Distprom = new double[14];
        double[] DistTotal=Dif.Kmetros(velInterp,timeRPM);
        for (int c=0;c<=DistTotal.length-1;c++){
            DistAcum=DistAcum+(DistTotal[c]/1000000);
            if(c%5==0&&FuleC[c]!=0){
                Distprom[CdDP]=(DistAcum+FuleC[c]/10000)/5;
                CdDP++;
                DistAcum=0;
            }
        }



        System.out.println("Size vel: " + vel.length);
        System.out.println("Size RPM: " + RPM.length);
        System.out.println("Size VelIntRPM: " + velInterp.length);
        System.out.println("Size RPMIntVel" + rpmInterp.length);
        System.out.println("Size TimeRPM" + timeRPM.length);
        System.out.println("Fule " + FulePromMuestra);
        System.out.println("Vel Interp: ");
        for(int i = 0; i<velInterp.length; i++){
            System.out.print(velInterp[i]+", ");
        }
        System.out.println("");
        System.out.println("Rpm Interp: ");
        for(int i = 0; i<rpmInterp.length; i++){
            System.out.print(rpmInterp[i]+", ");
        }
        System.out.println("");
        System.out.println("Dist prom: ");
        System.out.println("");
        for(int u = 0; u<Distprom.length; u++){
            System.out.print(Distprom[u]+", ");
        }
        System.out.println("");
        System.out.println("Fprom: ");
        System.out.println("");
        for(int u = 0; u<FpromAry.length; u++){
            System.out.print(FpromAry[u]+", ");
        }
    }
}
