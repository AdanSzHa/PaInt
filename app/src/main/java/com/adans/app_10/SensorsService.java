package com.adans.app_10;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;

public class SensorsService extends Service implements SensorEventListener{


    private final IBinder cBinder = new SensBinder();


    float VAX, VAY, VAZ, VGX, VGY, VGZ, AX, AY, AZ;

    //
    float Rot[]=null; //for gravity rotational data¿
    float I[]=null; //for magnetic rotational data
    float accels[]=new float[3];
    float mags[]=new float[3];
    float[] values = new float[3];

    float azimuth;
    float pitch;
    float roll;

    //Timestamp
    String ts;


    @Override
    public void onCreate (){
        super.onCreate();


        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        List<Sensor> listaSensores = sensorManager.getSensorList(Sensor.TYPE_ALL);

        listaSensores = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        if (!listaSensores.isEmpty()) {
            Sensor acelerometerSensor = listaSensores.get(0);
            sensorManager.registerListener(this, acelerometerSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        listaSensores = sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);

        if (!listaSensores.isEmpty()) {
            Sensor giroscopioSensor = listaSensores.get(0);
            sensorManager.registerListener(this, giroscopioSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        listaSensores = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);

        if (!listaSensores.isEmpty()) {
            Sensor MFSensor = listaSensores.get(0);
            sensorManager.registerListener(this, MFSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

    }
    public class SensBinder extends Binder {
        public SensorsService getService() {return SensorsService.this;}
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return cBinder;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            switch (event.sensor.getType()) {

                case Sensor.TYPE_ACCELEROMETER:
                    VAX = event.values[0];
                    VAY = event.values[1];
                    VAZ = event.values[2];
                    accels = event.values.clone();

                    break;

                case Sensor.TYPE_GYROSCOPE:
                    VGX = event.values[0];
                    VGY = event.values[1];
                    VGZ = event.values[2];

                    break;

                case Sensor.TYPE_MAGNETIC_FIELD:
                    mags = event.values.clone();
                    break;
            }
            Long tsLong = System.currentTimeMillis()/1000;
            ts = tsLong.toString();
        }
        if (mags != null && accels != null) {
            Rot = new float[9];
            I= new float[9];
            SensorManager.getRotationMatrix(Rot, I, accels, mags);
            // Correct if screen is in Landscape

            float[] outR = new float[9];
            SensorManager.remapCoordinateSystem(Rot, SensorManager.AXIS_X,SensorManager.AXIS_Z, outR);
            SensorManager.getOrientation(outR, values);

            //azimuth
            AX = values[0] * 57.2957795f; //looks like we don't need this one
            //pitch
            AY = values[1] * 57.2957795f;
            //roll
            AZ = values[2] * 57.2957795f;
            mags = null; //retrigger the loop when things are repopulated
            accels = null; ////retrigger the loop when things are repopulated
        }

    }

    public float getVAX() {
        return VAX;
    }

    public float getVAY() {
        return VAY;
    }

    public float getVAZ() {
        return VAZ;
    }

    public float getVGX() {
        return VGX;
    }

    public float getVGY() {
        return VGY;
    }

    public float getVGZ() {
        return VGZ;
    }

    public float getAX() { return AX; }

    public float getAY() { return AY; }

    public float getAZ() { return AZ; }

    public String getTs() { return ts;}

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
