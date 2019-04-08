package com.codineasy.wip.directionhelpers;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import com.codineasy.wip.MapsActivity;
import com.google.android.gms.maps.model.LatLng;

public class LocationBuilder extends Service {

    DistanceTravelBinder mDistanceTravelBinder = new DistanceTravelBinder();
    static double distanceInMetres;
    static float bearing;
    static Location lastLocation = null;

    public LocationBuilder() {
    }

    @Override
    public void onCreate() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                MapsActivity.mDeviceLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if (lastLocation == null) {
                    lastLocation = location;
                    bearing = location.getBearing();
                }
                distanceInMetres += location.distanceTo(lastLocation);
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
        };

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                1,
                locationListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mDistanceTravelBinder;
    }

    public class DistanceTravelBinder extends Binder {
        LocationBuilder getBinder(){
            return LocationBuilder.this;
        }
    }

    public double getDistanceTraveled(){
        return distanceInMetres;
    }

    public float getBearing(){
        return bearing;
    }
}
