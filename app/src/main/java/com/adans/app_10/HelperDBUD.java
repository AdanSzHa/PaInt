package com.adans.app_10;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HelperDBUD extends SQLiteOpenHelper {
    //Var Name and Version DB
    private static String NOMBRE_BD= "DBUD";
    private static int VERSION_BD= 1;

    private String TABLA_DU="CREATE TABLE DATOSU(TS TEXT, AX TEXT, AY TEXT, AZ TEXT , AGY TEXT, LOG TEXT, LAT TEXT, ALT TEXT, SPEED TEXT, NOSATS TEXT)";


    public HelperDBUD(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase utdb) {
        utdb.execSQL(TABLA_DU);
    }
    @Override
    public void onUpgrade(SQLiteDatabase utdb, int oldVersion, int newVersion) {
        utdb.execSQL("DROP TABLE IF EXISTS DATOSU"+TABLA_DU);
        utdb.execSQL(TABLA_DU);
    }
    public void AddData(String TsT, float AcX, float AcY, float AcZ, float GyX, float GyY, float GyZ,float AgX, float AgY, float AgZ, double LaT, double LoG, double AlT, float SpD, int NoS){
        SQLiteDatabase dbut=getWritableDatabase();
        if(dbut!=null){
            dbut.execSQL("INSERT INTO DATOSU VALUES('"+TsT+"'+'"+AcX+"'+'"+AcY+"'+'"+AcZ+"'+'"+GyX+"'+'"+GyY+"'+'"+GyZ+"'+'"+AgX+"'+'"+AgY+"'+'"+AgZ+"'+'"+LaT+"'+'"+LoG+"'+'"+AlT+"'+'"+SpD+"'+'"+NoS+"')");
            dbut.close();
        }

    }
}
