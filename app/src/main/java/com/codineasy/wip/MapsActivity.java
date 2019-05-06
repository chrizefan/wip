package com.codineasy.wip;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codineasy.wip.directionhelpers.DataParser;
import com.codineasy.wip.directionhelpers.FetchURL;
import com.codineasy.wip.directionhelpers.LocationBuilder;
import com.codineasy.wip.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;

import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import android.view.Gravity;
import org.json.JSONObject;
import android.support.v4.view.GravityCompat;
import android.widget.ViewFlipper;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.codineasy.wip.GlobalApplication.getAppContext;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback, GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener {

    private ImageView mGps;
    private RelativeLayout mDestinationInfoBox;
    private RelativeLayout mRouteInfoBox;
    private TextView mAddress1;
    private TextView mAddress2;
    private TextView mDuration;
    private TextView mDistance;
    private Button mDirections;
    private Button mStart;
    private GoogleMap mMap;
    private Boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesAutocompleteTextView mAutocomplete;
    private Marker mMarker;
    public static LatLng mDeviceLocation;

    private LocationManager mLocationManager;
    private LocationListener mGPSListener;
    private LocationListener mNetListener;

    private WifiManager mWifiManager;

    private Polyline[] mPolyline;
    public static List<List<HashMap<HashMap<String, String>, HashMap<String, String>>>> mStepsData;
    public static List<List<HashMap<HashMap<String, String>, HashMap<String, String>>>> mRoutesData;

    private Button slideBttn;
    private Button switchBttn;
    private boolean isup;

    public static JSONObject jDirections;
    private static final String TAG = "MapsActivity";
    private static final float DEFAULT_ZOOM = 15f;
    private static final int GOOGLE_PLAY_SERVICE_UPDATED_ERROR_REQUEST = 0;
    private static final int LOCATION_PERMISSION_REQUEST = 1;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    private RecyclerViewAdapter adapter;

    private SlidingUpPanelLayout slidePanel;
    private ViewFlipper viewFlipper;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (isGoogleServicesUpdated()) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_drawer);
            mGps = findViewById(R.id.ic_gps);
            mAutocomplete = findViewById(R.id.autocomplete);
            mDirections = findViewById(R.id.get_directions);
            mDestinationInfoBox = findViewById(R.id.destination_info_box);
            mAddress1 = findViewById(R.id.info_box_address_line1);
            mAddress2 = findViewById(R.id.info_box_address_line2);
            mRouteInfoBox = findViewById(R.id.route_info_box);
            mDuration = findViewById(R.id.info_box_duration);
            mDistance = findViewById(R.id.info_box_distance);
            mStart = findViewById(R.id.start);
            slideBttn = findViewById(R.id.slideUp);
            switchBttn = findViewById(R.id.switcher);
            slidePanel = findViewById(R.id.sliding_layout);
            getLocationPermission();
            init();

        }

        viewFlipper= findViewById(R.id.view_flipper);



        slidePanel.setEnabled(false);

        initImageBitmaps();

        DrawerLayout navDrawer = findViewById(R.id.drawer_layout);
        navDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        Button menubttn = findViewById(R.id.bttn_menu);

        menubttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout navDrawer = findViewById(R.id.drawer_layout);
                // If the navigation drawer is not open then open it, if its already open then close it.
                if(!navDrawer.isDrawerOpen(GravityCompat.START)) navDrawer.openDrawer(Gravity.START);
                else navDrawer.closeDrawer(Gravity.END);


            }
        });







        LinearLayout slideView = findViewById(R.id.bottom_view);
        slideView.setVisibility(View.INVISIBLE);

        isup = false;


        slideBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isup) {
                    slideDown(slideView);
                    v.setBackgroundResource(R.drawable.ic_up);


                } else {
                    slideUp(slideView);


                }
                isup = !isup;

                slidePanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

            }
        });

        mLocationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        mGPSListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mDeviceLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mLocationManager.removeUpdates(this);

                    moveCamera(new LatLng(location.getLatitude(), location.getLongitude()),
                            DEFAULT_ZOOM, "My Location");
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override
                public void onProviderEnabled(String provider) {}
                @Override
                public void onProviderDisabled(String provider) {
                    Toast.makeText(getApplicationContext(), "GPS location retrieval failed", Toast.LENGTH_SHORT);
                }
        };
        mNetListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (mDeviceLocation == null) {
                    mDeviceLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mLocationManager.removeUpdates(this);

                    moveCamera(new LatLng(location.getLatitude(), location.getLongitude()),
                            DEFAULT_ZOOM, "My Location");
                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, provider + " Provider disabled");
            }
        };

        mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    private void initImageBitmaps(){

       mImageUrls.add("https://upload.wikimedia.org/wikipedia/commons/thumb/d/d9/Flag_of_Canada_%28Pantone%29.svg/1920px-Flag_of_Canada_%28Pantone%29.svg.png");
        mNames.add("Description 1");


        mImageUrls.add("https://upload.wikimedia.org/wikipedia/commons/thumb/d/d9/Flag_of_Canada_%28Pantone%29.svg/1920px-Flag_of_Canada_%28Pantone%29.svg.png");
        mNames.add("Description 2");

        mImageUrls.add("https://upload.wikimedia.org/wikipedia/commons/thumb/d/d9/Flag_of_Canada_%28Pantone%29.svg/1920px-Flag_of_Canada_%28Pantone%29.svg.png");
        mNames.add("Description 3");

        mImageUrls.add("https://upload.wikimedia.org/wikipedia/commons/thumb/d/d9/Flag_of_Canada_%28Pantone%29.svg/1920px-Flag_of_Canada_%28Pantone%29.svg.png");
        mNames.add("Description 4");

        mImageUrls.add("https://upload.wikimedia.org/wikipedia/commons/thumb/d/d9/Flag_of_Canada_%28Pantone%29.svg/1920px-Flag_of_Canada_%28Pantone%29.svg.png");
        mNames.add("Description 5");

        initRecyclerView();
    }

    private void initRecyclerView()
    {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new RecyclerViewAdapter(this, mNames, mImageUrls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        mAutocomplete.setHistoryManager(null);
        mAutocomplete.showClearButton(true);
        mAutocomplete.setOnPlaceSelectedListener(
                place -> geoLocate());
        mAutocomplete.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || event.getAction() == KeyEvent.ACTION_DOWN
                            || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                        geoLocate();
                    }
                    return false;
                });

        mGps.setOnClickListener(v -> {
            Log.d(TAG, "onClick: gps icon clicked");
            getDeviceLocation();
        });
        hideSoftKeyboard();
    }


    private void getRoute() {
        if (mMarker != null && mPolyline != null) {
            for (Polyline aMPolyline : mPolyline) {
                aMPolyline.remove();
            }
        }
        mDirections.setVisibility(View.VISIBLE);
        mDirections.setOnClickListener(v -> {
            if(mDeviceLocation == null) {
                Toast.makeText(this, "Location must be retrieved first", Toast.LENGTH_SHORT).show();
            } else {
                if(!mWifiManager.isWifiEnabled()) {
                    Toast.makeText(MapsActivity.this, "Please enable wifi", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                } else {
                    new FetchURL(MapsActivity.this).execute(getUrl(mDeviceLocation, new LatLng(mMarker.getPosition().latitude, mMarker.getPosition().longitude), "driving"), "driving");
                }
            }
        });
    }

    private void startNavigation() {
        if(mDeviceLocation == null) {
            Toast.makeText(this, "Location must be retrieved first", Toast.LENGTH_SHORT).show();
        } else {
            LocationBuilder locationBuilder = new LocationBuilder();
            mStart.setOnClickListener(v -> {

                CameraPosition camPos = new CameraPosition.Builder()
                        .target(mDeviceLocation)
                        .tilt(45)
                        .zoom(20f)
                        .bearing(locationBuilder.getBearing())
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
            });
        }   
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode ;
        // Output format
        String output = "json";
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&alternatives=true"+ "&units=metric" + "&key=" + getString(R.string.google_maps_key2);
    }

    @Override
    public void onTaskDone(Object... values) {
        mPolyline = new Polyline[values.length];
        for (int i = 0; i < mPolyline.length; i++) {
            Log.d(TAG, "onTaskDone: adding polyline " + i);
            mPolyline[i] = mMap.addPolyline((PolylineOptions) values[i]);
            focusRoute(mPolyline[0].getPoints());
        }
        displayRouteInfoBox();

        WipGlobals.detailsIndex.set(0);
    }

    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mAutocomplete.getText().toString();

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
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),
                    DEFAULT_ZOOM, address.getAddressLine(0));
        }
        else {
            if (mWifiManager.isWifiEnabled()){
                Toast.makeText(MapsActivity.this, "Address not found... Try again?", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MapsActivity.this, "Please enable wifi", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
            }
        }
        hideSoftKeyboard();
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        hideSoftKeyboard();
    }



    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: get device location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    try {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: location found");
                            Location currentLocation = (Location) task.getResult();

                            if(currentLocation == null) {
                                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                    Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                } else {
                                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mGPSListener);
                                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mNetListener);
                                    Toast.makeText(this, "Retrieving location may take a few minutes", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mDeviceLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM, "My Location");
                            }

                        } else {
                            Log.d(TAG, "onComplete: location null");
                            Toast.makeText(MapsActivity.this, "Address not found... Try again?", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NullPointerException e) {
                        Log.d(TAG, "getDeviceLocation: NullPointerException: " + e.getMessage());
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }

    }

    public void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        setMarker(latLng, title);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        hideSoftKeyboard();
    }

    public void setMarker(LatLng latLng, String title) {
        if(!title.equals("My Location") && mMarker == null){
            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(title));
                    displayDestinationInfoBox();
        } else if(!title.equals("My Location")){
            mMarker.hideInfoWindow();
            mMarker.setPosition(latLng);
            mMarker.setTitle(title);
            displayDestinationInfoBox();
        }
    }

    public void displayRouteInfoBox() {
        mDestinationInfoBox.setVisibility(View.INVISIBLE);
        slideBttn.setVisibility(View.VISIBLE);
        slidePanel.setEnabled(true);




        for (int i = 0; i < mPolyline.length; i++) {
            if (mPolyline[i].getZIndex() == 1) {
                int duration = new DataParser().parseTotalDuration(jDirections)[i];
                int seconds = duration % 60;
                int totalMinutes = duration / 60;
                int minutes = totalMinutes % 60;
                int hours = totalMinutes / 60;
                if (seconds >= 30) minutes += 1;
                if (hours == 0) mDuration.setText(minutes + "min");
                else mDuration.setText(hours + "h" + minutes + "min");

                double distance = new DataParser().parseTotalDistance(jDirections)[i];
                distance = distance/1000.0;
                if (distance < 100) {
                    DecimalFormat df = new DecimalFormat("#.#");
                    mDistance.setText(df.format(distance) + "km");
                } else {
                    distance = Math.round(distance);
                    mDistance.setText( Long.toString(((Double)distance).longValue()) + "km");
                }
            }
        }
        mRouteInfoBox.setVisibility(View.VISIBLE);
        startNavigation();
    }

    public void displayDestinationInfoBox() {


        try {
            mRouteInfoBox.setVisibility(View.INVISIBLE);
            String[] address = mMarker.getTitle().split(", ");
            mAddress1.setText(address[0]);
            StringBuilder address2 = new StringBuilder(address[1]);
            for (int i = 2; i < address.length; i++) {
                address2.append(", ").append(address[i]);
            }
            mAddress2.setText(address2.toString());
            mDestinationInfoBox.setVisibility(View.VISIBLE);
            getRoute();
        } catch (ArrayIndexOutOfBoundsException e) {
            Toast.makeText(this, "address not available", Toast.LENGTH_SHORT).show();
        }
    }

    public void focusRoute(List<LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 200;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
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

    private void hideSoftKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void onBackPressed() {

    }

    @SuppressLint("NewApi")
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

        mMap.setOnMapLongClickListener(latLng -> {
            Geocoder geocoder =
                    new Geocoder(MapsActivity.this);
            List<Address> list;
            try {
                list = geocoder.getFromLocation(latLng.latitude,
                        latLng.longitude, 1);
            } catch (IOException e) {
                return;
            }
            String title = list.get(0).getAddressLine(0);
            setMarker(latLng, title);
            mAutocomplete.setText(title);
        });
        mMap.setOnPolylineClickListener(polyline -> {
            int index = 0;
            for (Polyline aMPolyline : mPolyline) {
                if (polyline.getId().equals(aMPolyline.getId())) {
                    aMPolyline.setColor(ContextCompat.getColor(getAppContext(), R.color.colorBlue));
                    aMPolyline.setZIndex(1);
                    focusRoute(polyline.getPoints());

                    Log.d(TAG, "current index: " + index);
                    WipGlobals.detailsIndex.set(index);
                    adapter.notifyDataSetChanged();
                    WipGlobals.details.get(index).forEach(ld -> Log.d(TAG, "getWeather(): "+ ld.getWeather().toString()));
                } else {
                    aMPolyline.setColor(ContextCompat.getColor(getAppContext(), R.color.colorBlueTransparent));
                    aMPolyline.setZIndex(0);
                    ++index;
                }
            }
            displayRouteInfoBox();
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @SuppressLint("NewApi")
    public void slideUp(View view){
        long time = SystemClock.uptimeMillis();
        for(LocationDetail detail : WipGlobals.details.get(WipGlobals.detailsIndex.get())) {
            while(Double.isNaN(detail.getWeather().temperature())) {
                if(SystemClock.uptimeMillis() - time >= 2000)
                    break;
            }
        }
        adapter.notifyDataSetChanged();
        WipGlobals.details.get(0).forEach(ld -> Log.d(TAG, "getWeather(): "+ ld.getWeather()));

        view.setVisibility(View.INVISIBLE);



    }


    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.INVISIBLE);
    }

    public void nextView(View v)
    {
        viewFlipper.showNext();

    }













}
