package com.codineasy.wip;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Address;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private EditText mSearchText;

    private GoogleMap mMap;
    private Boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static final String TAG = "MapsActivity";
    private static final float DEFAULT_ZOOM = 15f;

    static final int GOOGLE_PLAY_SERVICE_UPDATED_ERROR_REQUEST = 0;
    static final int LOCATION_PERMISSION_REQUEST = 1;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isGoogleServicesUpdated()) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
            mSearchText = findViewById(R.id.input_search);

            getLocationPermission();

            init();
        }
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute search method
                    geoLocate();
                }

                return false;
            }
        });
    }

    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        } catch(IOException e){
            Log.e(TAG, "geoLoacate: IOException, " + e.getMessage());
        }
        if(list.size() > 0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found location: " + address.toString());
            Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: get device location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        try {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: location found");
                                Location currentLocation = (Location) task.getResult();

                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM);
                            } else {
                                Log.d(TAG, "onComplete: location null");
                                Toast.makeText(MapsActivity.this, "location not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NullPointerException e) {
                            Log.d(TAG, "getDeviceLocation: NullPointerException: " + e.getMessage());
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }

    }

    public void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public boolean isGoogleServicesUpdated() {
        Log.d("Update", "isGoogleServicesUpdated: checking Google Play Services");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //Google Play Services is up to date
            Log.d("Update", "isGoogleServicesUpdated: Google Play Services updated");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //An error occurred or Google Play Services is not up to date
            Log.d("Update", "isGoogleServicesUpdated: error");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available,
                    GOOGLE_PLAY_SERVICE_UPDATED_ERROR_REQUEST);
            dialog.show();
            return false;
        } else {
            Toast.makeText(this, "unable to make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                    //initialize map
                    initMap();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

    }
}
