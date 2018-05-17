package com.adans.app_10;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;


public class GpsDataService extends Service implements LocationListener,GpsStatus.Listener {

    private final IBinder cBinder = new GPSBinder();

    ///VARS GPSAplic
    private static final int NOT_AVAILABLE = -100000;

    //LocationManager
    private LocationManager mlocManager = null;
    private int _NumberOfSatellites = 0;
    private int _NumberOfSatellitesUsedInFix = 0;

    //NmaLine
    String NmaLine;

    Double AltitudeN;

    int NoSats;

    String UTCDate;
    String NMEALat;
    String NMEALog;

    //Locatio Listener del NMEA
    LocationManager mLocationManager;


    @Override
    public void onCreate () {
        super.onCreate();

        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        mLocationManager.addNmeaListener(mNmeaListener);

    }

    private GpsStatus.NmeaListener mNmeaListener = new GpsStatus.NmeaListener() {
        @Override
        public void onNmeaReceived(long timestamp, String nmea) {
            parseNmeaString(nmea);
        }
    };


    private void parseNmeaString(String line) {
        NmaLine = line;
        if (line.startsWith("$")) {
            String[] tokens = line.split(",");
            String type = tokens[0];

            // Parse altitude above sea level, Detailed description of NMEA string here http://aprs.gids.nl/nmea/#gga
            if (type.startsWith("$GPGGA")) {
                if (!tokens[1].isEmpty()) {
                    UTCDate = (tokens[1]);
                }
                if (!tokens[2].isEmpty()) {
                    NMEALat = (tokens[2]);
                }
                if (!tokens[4].isEmpty()) {
                    NMEALog = (tokens[4]);
                }
                if (!tokens[7].isEmpty()) {
                    NoSats = Integer.parseInt(tokens[7]);
                }
                if (!tokens[9].isEmpty()) {
                    AltitudeN = Double.parseDouble(tokens[9]);
                }
            }
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {
        //updateSats();
    }


    public class GPSBinder extends Binder{
        GpsDataService getService(){
            return GpsDataService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return cBinder;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /*public void updateSats() {
        try {
            if ((mlocManager != null) && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                GpsStatus gs = mlocManager.getGpsStatus(null);
                int sats_inview = 0;    // Satellites in view;
                int sats_used = 0;      // Satellites used in fix;

                if (gs != null) {
                    Iterable<GpsSatellite> sats = gs.getSatellites();
                    for (GpsSatellite sat : sats) {
                        sats_inview++;
                        if (sat.usedInFix()) sats_used++;
                        //Log.w("myApp", "[#] GPSApplication.java - updateSats: i=" + i);
                    }
                    _NumberOfSatellites = sats_inview;
                    _NumberOfSatellitesUsedInFix = sats_used;
                } else {
                    _NumberOfSatellites = NOT_AVAILABLE;
                    _NumberOfSatellitesUsedInFix = NOT_AVAILABLE;
                }
            } else {
                _NumberOfSatellites = NOT_AVAILABLE;
                _NumberOfSatellitesUsedInFix = NOT_AVAILABLE;
            }
        } catch (NullPointerException e) {
            _NumberOfSatellites = NOT_AVAILABLE;
            _NumberOfSatellitesUsedInFix = NOT_AVAILABLE;
            //Log.w("myApp", "[#] GPSApplication.java - updateSats: Caught NullPointerException: " + e);
        }
    }*/

    public String getNmaLine() {
        return NmaLine;
    }

    public int getNoSats() {
        return NoSats;
    }



    

}
