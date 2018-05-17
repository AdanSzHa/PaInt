package com.adans.app_10;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class GPSAplic extends Application implements GpsStatus.Listener, LocationListener {

    private static final int NOT_AVAILABLE = -100000;

    //LocationManager
    private LocationManager mlocManager = null;
    private int _NumberOfSatellites = 0;
    private int _NumberOfSatellitesUsedInFix = 0;

    public void updateSats() {
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
            Log.w("myApp", "[#] GPSApplication.java - updateSats: Caught NullPointerException: " + e);
        }
    }




    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                // TODO: get here the status of the GPS, and save into a GpsStatus to be used for satellites visualization;
                // Use GpsStatus getGpsStatus (GpsStatus status)
                // https://developer.android.com/reference/android/location/LocationManager.html#getGpsStatus(android.location.GpsStatus)
                updateSats();
                break;
        }

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

}
