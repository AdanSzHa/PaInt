package com.adans.app_10;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AdanS on 28/03/2018.
 */

public class MantBDD extends SQLiteOpenHelper {

    //Variables para el nombre y Versión de la BDD
    private static String NOMBRE_BDD= "BDDSensors";
    //// Recuerda .db
    private static int VERSION_BDD= 1;

    //Var para crear la tabla para guardar los datos del form

    private String TABLA_CURSOS="CREATE TABLE CURSOS(AX TEXT, AY TEXT, AZ TEXT, GX TEXT, GY TEXT, GZ TEXT,LOG TEXT,LAT TEXT)";

    //Generamos el constructor de la clase
    //Dejas solo (Context,context)

    public MantBDD(Context context) {
        super(context, NOMBRE_BDD, null, VERSION_BDD);
    }

    //Generas el OnCreate para las tablas

    @Override
    public void onCreate(SQLiteDatabase sqldb) {
        //Metodo para ejecutar comandos SQL
        //Es la var del metoso onCreate (db)
        sqldb.execSQL(TABLA_CURSOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqldb, int oldVersion, int newVersion) {
        sqldb.execSQL("DROP TABLE IF EXISTS CURSOS"+TABLA_CURSOS);
        sqldb.execSQL(TABLA_CURSOS);
    }
    public void agregarCurso(float ax, float ay, float az, float gx, float gy, float gz, float log, float lat){

        SQLiteDatabase db=getWritableDatabase();
        if(db!=null){
            db.execSQL("INSERT INTO CURSOS VALUES('"+ax+"','"+ay+"','"+az+"','"+gx+"','"+gy+"','"+gz+"','"+log+"','"+lat+"')");
            db.close();
        }

    }

    //Metodo lista para poder consultar los datos agregados.
    public List<PerfModelo> mostrarCursos(){
        SQLiteDatabase bd=getReadableDatabase();

        //Clase cursor permite recuperar datos por consulta selecta
        Cursor cursor= bd.rawQuery("SELECT * FROM CURSOS",null);
        List<PerfModelo> cursos=new ArrayList<>();
        if (cursor.moveToFirst()){
            do{
                cursos.add(new PerfModelo(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7)));
            }while (cursor.moveToNext());
        }
        return cursos;
    }

}
