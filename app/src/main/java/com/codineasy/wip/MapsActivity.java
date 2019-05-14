package com.codineasy.wip;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.Observable;
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
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.model.MapStyleOptions;
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

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.codineasy.wip.GlobalApplication.getAppContext;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback, GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener{

    private ImageButton mGps;
    private ImageButton slideBttn;
    private Button mMenuButton;
    private RelativeLayout mDestinationInfoBox;
    private RelativeLayout mRouteInfoBox;
    private TextView mAddress1;
    private TextView mAddress2;
    private TextView mDuration;
    private TextView mDistance;
    private Button mDirections;
    private Button mStart;
    private ImageButton mCancel;
    private GoogleMap mMap;
    private Boolean mLocationPermissionGranted;
    private PlacesAutocompleteTextView mAutocomplete;
    private Marker mMarker;
    private DrawerLayout mDrawer;
    private Button refresh;
    private Button switchButton;
    private SlidingUpPanelLayout slidePanel;
    private LinearLayout mSlideView;
    private LocationManager mLocationManager;
    private LocationListener mGPSListener;
    private LocationListener mNetListener;
    private WifiManager mWifiManager;
    private Polyline[] mPolyline;
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    public RecyclerViewAdapter adapter;
    public static List<List<HashMap<HashMap<String, String>, HashMap<String, String>>>> mStepsData;
    public static List<List<HashMap<HashMap<String, String>, HashMap<String, String>>>> mRoutesData;
    public static LatLng mDeviceLocation;
    public static JSONObject jDirections;
    public static String mUnits = "metric";
    private static final String TAG = "MapsActivity";
    private static final float DEFAULT_ZOOM = 15f;
    private static final int GOOGLE_PLAY_SERVICE_UPDATED_ERROR_REQUEST = 0;
    private static final int LOCATION_PERMISSION_REQUEST = 1;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String SHARED_PREFS = "sharedPreferences";
    private static final String key = "mapTheme";
    private static int mTheme;
    private static boolean isUp = false;



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getAppContext(), "Connection with Google API failed", Toast.LENGTH_SHORT).show();
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
            mCancel = findViewById(R.id.cancel_action);
            slideBttn = findViewById(R.id.slideUp);
            switchButton = findViewById(R.id.switcher);
            slidePanel = findViewById(R.id.sliding_layout);
            refresh = findViewById(R.id.refresh);
            mDrawer = findViewById(R.id.drawer_layout);
            mMenuButton = findViewById(R.id.bttn_menu);
            mSlideView = findViewById(R.id.bottom_view);
            getLocationPermission();
            init();

        }

        setNavigationViewListner();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        slidePanel.setEnabled(false);
        initImageBitmaps();
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

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
                    Toast.makeText(getApplicationContext(), "GPS location retrieval failed", Toast.LENGTH_SHORT).show();
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
        refresh.setOnClickListener(v -> {
            for(List<LocationDetail> d : WipGlobals.details) {
                for (LocationDetail ld : d) {
                    ld.fetchDarkSky();
                    Log.d(TAG, "fetching DarkSky");
                }
                adapter.notifyDataSetChanged();
            }
        });
        loadData();
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

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new RecyclerViewAdapter(this);
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

        WipGlobals.detailsIndex.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                WipGlobals.currentWeathers.clear();
                for (LocationDetail ld : WipGlobals.details.get(WipGlobals.detailsIndex.get())) {
                    WipGlobals.currentWeathers.add(ld.getWeather());
                }
            }
        });

        mMenuButton.setOnClickListener(v -> {
            // DrawerLayout navDrawer = findViewById(R.id.drawer_layout);
            // If the navigation drawer is not open then open it, if its already open then close it.
            if(!mDrawer.isDrawerOpen(GravityCompat.START)) mDrawer.openDrawer(Gravity.START);
            else mDrawer.closeDrawer(Gravity.END);
        });
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
            if(!mWifiManager.isWifiEnabled()) {
                Toast.makeText(MapsActivity.this, "Please enable wifi", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
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
                    mStart.setVisibility(View.INVISIBLE);
                    mCancel.setVisibility(View.VISIBLE);
                });
                mCancel.setOnClickListener(v -> {
                    for (int i = 0; i < mPolyline.length; i++) {
                        if (mPolyline[i].getZIndex() == 1) {
                            focusRoute(mPolyline[i].getPoints());
                        }
                    }
                    mCancel.setVisibility(View.INVISIBLE);
                    mStart.setVisibility(View.VISIBLE);
                });
            }
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
        //Units
        String units = mUnits;
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&alternatives=true"+ "&units=" + units + "&key=" + getString(R.string.google_maps_key2);
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
        WipGlobals.detailsIndex.notifyChange();

        for(List<LocationDetail> lds : WipGlobals.details) {
            for(LocationDetail ld : lds) {
                ld.getWeather().clearWeather();
            }
        }
        adapter.notifyDataSetChanged();
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

        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
                                CameraPosition camPos = new CameraPosition.Builder()
                                        .target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                                        .zoom(DEFAULT_ZOOM)
                                        .build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
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
        CameraPosition camPos = new CameraPosition.Builder()
                .target(latLng)
                .tilt(0)
                .zoom(zoom)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
        hideSoftKeyboard();
    }

    public void setMarker(LatLng latLng, String title) {
        slideBttn.setVisibility(View.INVISIBLE);
        mMap.clear();
        if(!title.equals("My Location") && mMarker == null){
            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(title));
                    displayDestinationInfoBox();
        } else if(!title.equals("My Location")){
            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(title));
            mMarker.hideInfoWindow();
            displayDestinationInfoBox();
        }
    }

    public void displayRouteInfoBox() {
        if(!mRoutesData.isEmpty()) {
            mDestinationInfoBox.setVisibility(View.INVISIBLE);
            slideBttn.setVisibility(View.INVISIBLE);
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
                    if(mUnits == "metric") {
                        double distance = new DataParser().parseTotalDistance(jDirections)[i];
                        distance = distance / 1000.0;
                        if (distance < 0.1) {
                            mDistance.setText(Math.round(distance * 1000) + " m");
                        }
                        if (distance < 100) {
                            DecimalFormat df = new DecimalFormat("#.#");
                            mDistance.setText(df.format(distance) + " km");
                        } else {
                            distance = Math.round(distance);
                            mDistance.setText((((Double) distance).longValue()) + " km");
                        }
                    }
                    else if(mUnits == "imperial") {
                        double distance = new DataParser().parseTotalDistance(jDirections)[i] * 3.28084;
                        distance = distance / 5280.0;
                        if (distance < 0.1) {
                            mDistance.setText(Math.round(distance * 5280) + " ft");
                        }
                        if (distance < 100) {
                            DecimalFormat df = new DecimalFormat("#.#");
                            mDistance.setText(df.format(distance) + " mi");
                        } else {
                            distance = Math.round(distance);
                            mDistance.setText((((Double) distance).longValue()) + " mi");
                        }
                    }
                }
            }
            slideBttn.setVisibility(View.VISIBLE);
            slideBttn.setOnClickListener(v -> {
                if (isUp) {
                    slideDown(mSlideView);
                } else {
                    slideUp(mSlideView);
                }
                isUp = !isUp;
                slidePanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            });
            mRouteInfoBox.setVisibility(View.VISIBLE);
            startNavigation();
        } else {
            Toast.makeText(getApplicationContext(), "No routes available", Toast.LENGTH_SHORT).show();
        }
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





    @SuppressLint("NewApi")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map ready");
        mMap = googleMap;

        if(mTheme ==1)
        {
            mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));
        }
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
            if(!list.isEmpty()) {
                String title = list.get(0).getAddressLine(0);
                moveCamera(latLng, DEFAULT_ZOOM, title);
                mAutocomplete.setText(title);
            } else {
                Toast.makeText(getApplicationContext(), "No address available", Toast.LENGTH_SHORT).show();
            }
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
                    WipGlobals.detailsIndex.notifyChange();
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

    public void nextView(View v) {
        WipGlobals.isShowingDirection = !WipGlobals.isShowingDirection;
        adapter.notifyDataSetChanged();

        if(!WipGlobals.isShowingDirection )
        {
            switchButton.setText("Weather");
        }

        if(WipGlobals.isShowingDirection )
        {
            switchButton.setText("Directions");
        }

    }

    public void changeStyle(View v) {
        saveData();
        Intent mStartActivity = new Intent(MapsActivity.this, MapsActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(MapsActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)MapsActivity.this.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(mTheme ==0) {
            editor.putInt(key, 1);
            editor.commit();
            return;
        }


        if(mTheme ==1) {
            editor.putInt(key, 0);
            editor.commit();
            return;
        }

    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        mTheme = sharedPreferences.getInt(key, 0);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.dark_mode: {
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.restart_notice);
                dialog.show();
                break;
            }

            case R.id.toggle_units: {
                if(mUnits == "metric") mUnits = "imperial";
                else if(mUnits == "imperial") mUnits = "metric";
                if(!(jDirections == null)) {
                    for (int i = 0; i < mPolyline.length; i++) {
                        if (mPolyline[i].getZIndex() == 1) {
                            if (mUnits == "metric") {
                                double distance = new DataParser().parseTotalDistance(jDirections)[i];
                                distance = distance / 1000.0;
                                if (distance < 0.1) {
                                    mDistance.setText(Math.round(distance * 1000) + " m");
                                }
                                else if (distance < 100) {
                                    DecimalFormat df = new DecimalFormat("#.#");
                                    mDistance.setText(df.format(distance) + " km");
                                } else {
                                    distance = Math.round(distance);
                                    mDistance.setText((((Double) distance).longValue()) + " km");
                                }
                            } else if (mUnits == "imperial") {
                                double distance = new DataParser().parseTotalDistance(jDirections)[i] * 3.28084;
                                distance = distance / 5280.0;
                                if (distance < 0.1) {
                                    mDistance.setText(Math.round(distance * 5280) + " ft");
                                }
                                else if (distance < 100) {
                                    DecimalFormat df = new DecimalFormat("#.#");
                                    mDistance.setText(df.format(distance) + " mi");
                                } else {
                                    distance = Math.round(distance);
                                    mDistance.setText((((Double) distance).longValue()) + " mi");
                                }
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
        //close navigation drawer
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListner() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
}
