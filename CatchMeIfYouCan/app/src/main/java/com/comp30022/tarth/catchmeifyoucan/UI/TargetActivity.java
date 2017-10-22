// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

package com.comp30022.tarth.catchmeifyoucan.UI;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.comp30022.tarth.catchmeifyoucan.Game.ChatFragment;
import com.comp30022.tarth.catchmeifyoucan.Game.OptionsFragment;
import com.comp30022.tarth.catchmeifyoucan.R;
import com.comp30022.tarth.catchmeifyoucan.Server.Communication;
import com.comp30022.tarth.catchmeifyoucan.Server.Message;
import com.comp30022.tarth.catchmeifyoucan.Server.Result;
import com.comp30022.tarth.catchmeifyoucan.Server.WebSocketClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TargetActivity.java
 * Target game interface
 */
public class TargetActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
        Communication, OptionsFragment.FragmentCommunication,
        ChatFragment.FragmentCommunication {

    protected GoogleMap mMap;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    // user's current location data
    private Location mCurrentLocation;
    // the marker indicating user's current location
    private Marker mCurrLocationMarker = null;
    // hardcoded number for requesting location permission
    public static final int REQUEST_LOCATION_CODE = 999;
    // time intervals for requesting location updates
    private static int UPDATE_INTERVAL = 5000; //SEC
    private static int FATEST_INTERVAL = 3000; //SEC
    private static int DISPLACEMENT = 10; // METERS

    // user's current geocoordinates - latitude and longitude
    double curr_latitude, curr_longitude;

    // indicates whther waypoints have been created by user
    boolean addWaypoints = false;
    // timer used for continuous updating locations of other users
    Timer timer;

    //coordinates of way points
    List<Double> cWaypoints = new ArrayList<Double>();
    //markers of  way points
    List<Marker> mMarkers = new ArrayList<Marker>();
    //markers of other users
    private List<Marker> othersMarker = new ArrayList<Marker>();

    private final static int MAP_ITEM_ID = 1;

    // fragment switching related variables
    SupportMapFragment mapFragment;
    Fragment chatFragment;
    Fragment optionsFragment;
    private BottomNavigationView navigation;

    /**
     * Called when the activity is starting
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        WebSocketClient.getClient().setActivity(this);

        //Check if Google Play Services available
        if (!checkGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }

        //Check location permission
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = new SupportMapFragment();
        chatFragment = new ChatFragment();
        optionsFragment = new OptionsFragment();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.getMenu().getItem(MAP_ITEM_ID).setChecked(true);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switchFragment (item);
                return true;
            }
        });

        MenuItem item = navigation.getMenu().getItem(MAP_ITEM_ID);
        switchFragment(item);
        continuousUpdate();
    }

    /**
     * Called when the activity is becoming visible to the user
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Called when the activity will start interacting with the user
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Called when the activity will start interacting with the user
     */
    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    /**
     * Called when the activity is no longer visible to the user
     */
    @Override
    protected void onStop() {
        super.onStop();
        // disconnect when user leaves the interface
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * This hook is called whenever an item in your options menu is selected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the activity has detected the user's press of the back key
     */
    @Override
    public void onBackPressed() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getLocation();
                sendLocation();
            }
        }, 0, 0);
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                WebSocketClient.getClient().setActivity(this);
            }
        }
    }

    /**
     * Method invoked when the WebSocketClient receives a message
     * @param message : Message received from server
     */
    @Override
    public void onResponse(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message.getAction() != null) {
                    toast("New message from " + message.getFrom() + ": " + message.getMessage());
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_GET_USER_SUCCESS))) {
                    toast("Game get users successful");
                    ((OptionsFragment) optionsFragment).onResponse(message);
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_GET_USER_FAIL))) {
                    toast("Game get users failure");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.LOCATION_GET_SUCCESS))) {
                    toast("Location get successful");
                    List<Double> locations = new ArrayList<>();
                    Result[] results = message.getResult();
                    for (Result result : results) {
                        locations.add(result.getX());
                        locations.add(result.getY());
                    }
                    updateOthers(locations);
                } else if (message.getCode().equals(getResources().getInteger(R.integer.LOCATION_GET_FAIL))) {
                    toast("Location get failure");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_EXIT_SUCCESS))) {
                    toast("Game exit successful");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_EXIT_FAIL))) {
                    toast("Game exit failure");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_DELETE_SUCCESS))) {
                    toast("Game delete successful");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_DELETE_FAIL))) {
                    toast("Game delete failure");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_ADD_WAYPOINT_SUCCESS))) {
                    toast("Waypoint add success");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_ADD_WAYPOINT_FAIL))) {
                    toast("Waypoint add failure");
                }
            }
        });
    }

    /**
     * User exits the game
     */
    @Override
    public void onExit() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", getResources().getInteger(R.integer.GAME_EXIT));
        } catch(Exception e) {
            e.printStackTrace();
        }
        WebSocketClient.getClient().send(obj.toString());

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getLocation();
                sendLocation();
            }
        }, 0, 0);
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    /**
     * Sends a message to the server
     * @param obj
     */
    @Override
    public void onSend(JSONObject obj) {
        WebSocketClient.getClient().send(obj.toString());
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        // set map options
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
    }

    /**
     * This interface is the contract for receiving the results for permission requests
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    toast("Permission denied");
                }
                break;
            }
        }
    }

    /**
     * Called when the location has changed
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        JSONObject obj = new JSONObject();
        try {
            //obj.put("action", MESSAGE_COMMAND_SEND);
            //obj.put("message", "test");
            // TESTING
            obj.put("action", getResources().getInteger(R.integer.LOCATION_GET));
        } catch(Exception e) {
            e.printStackTrace();
        }
        //onSend(obj);

        // only need the marker indicating current user's location when the user enter the map at the 1st time
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // get the user's current location
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mCurrentLocation != null) {
            curr_latitude = mCurrentLocation.getLatitude();
            curr_longitude = mCurrentLocation.getLongitude();
        }

        LatLng latLng = new LatLng(curr_latitude, curr_longitude);
        // add a marker when the user enter the map at the first time
        if(mCurrLocationMarker == null){
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.draggable(true);
            markerOptions.title("Original Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mCurrLocationMarker = mMap.addMarker(markerOptions);

            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

            toast("Your Current Location");
        }
    }

    /**
     * Provides callbacks for scenarios that result in a failed attempt to connect the client to the service
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    /**
     * Called when the client is temporarily in a disconnected state
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d("onConnectionSuspended", "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Defines signatures for methods that are called when a marker is clicked or tapped
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    /**
     * Called when the user makes a tap gesture on the map,
     * but only if none of the overlays of the map handled the gesture
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        if(addWaypoints){
            Marker waypoint = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("waypoint")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                    .snippet(latLng.toString()));

            cWaypoints.add(latLng.latitude);
            cWaypoints.add(latLng.longitude);
            System.out.println("Waypoint added: " + latLng.latitude + ", " + latLng.longitude);
            mMarkers.add(waypoint);
        }
    }

    /**
     * Displays a toast message
     * @param message : Message to be displayed
     */
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Switches fragments based on bottom navigation menu state
     * @param item
     */
    private void switchFragment(MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.navigationChat:
                if (!chatFragment.isAdded()) {
                    if (optionsFragment.isAdded()) {
                        transaction.remove(optionsFragment);
                    }
                    transaction.hide(mapFragment);
                    transaction.add(R.id.fragment_container, chatFragment);
                    transaction.addToBackStack("map");
                    if (this.getActionBar() != null) {
                        this.getActionBar().setTitle("Game Chat");
                    }
                }
                break;
            case R.id.navigationMap:
                if (chatFragment.isAdded()) {
                    transaction.remove(chatFragment);
                }
                if (optionsFragment.isAdded()) {
                    transaction.remove(optionsFragment);
                }
                if (!mapFragment.isAdded()) {
                    transaction.replace(R.id.fragment_container, mapFragment);
                } else {
                    transaction.show(mapFragment);
                }
                mapFragment.getMapAsync(this);
                if (this.getActionBar() != null) {
                    this.getActionBar().setTitle("Game Map");
                }
                break;
            case R.id.navigationOptions:
                if (!optionsFragment.isAdded()) {
                    if (chatFragment.isAdded()) {
                        transaction.remove(chatFragment);
                    }
                    transaction.hide(mapFragment);
                    transaction.add(R.id.fragment_container, optionsFragment);
                    transaction.addToBackStack("map");
                    if (this.getActionBar() != null) {
                        this.getActionBar().setTitle("Game Options");
                    }
                }
                break;
        }
        transaction.commit();
    }

    /**
     * Checks whether Google Play Services have been enabled
     * @return
     */
    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    /**
     * Checks whether location permissions have been enabled
     * @return
     */
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_CODE);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_CODE);
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Builder to configure a GoogleApiClient
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Receives location updates
     */
    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Provides callbacks that are called when the client is connected or disconnected from the service
     * @param bundle
     */
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

        startLocationUpdates();
    }

    /**
     * Gives a reaction to the user according to user's behavior
     * Eg: Tapping different buttons
     * @param v
     */
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.B_addWaypoints:
                addWaypoints = true;
                break;

            case R.id.B_finishAddWP:
                addWaypoints = false;
                sendWaypoints();
                break;
        }
    }

    /**
     * Update markers indicating other users on the map
     * @param othersLocation
     */
    public void updateOthers(List<Double> othersLocation){
        // clear old markers
        for(int j=othersMarker.size()-1; j>=0; j--){
            othersMarker.get(j).remove();
        }
        // add new markers
        int count = othersLocation.size()-1;
        for(int i=0; i<count; i+=2){
            LatLng latLng = new LatLng(othersLocation.get(i), othersLocation.get(i+1));
            System.out.println("LOCATION : " + Double.toString(othersLocation.get(i)) + ", " +  Double.toString(othersLocation.get(i + 1)) );
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("people");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            if (mMap != null) {
                Marker oMarker = mMap.addMarker(markerOptions);
                othersMarker.add(oMarker);
            }
        }
    }

    /**
     * Sends location details to the server
     */
    private void sendLocation() {
        JSONObject loc = new JSONObject();
        try {
            loc.put("x", curr_latitude);
            loc.put("y", curr_longitude);
        } catch(Exception e) {
            e.printStackTrace();
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", getResources().getInteger(R.integer.LOCATION_SEND));
            obj.put("location", loc);
        } catch(Exception e) {
            e.printStackTrace();
        }
        WebSocketClient.getClient().send(obj.toString());
    }

    /**
     * Requests user location data from the server
     */
    protected void getLocation() {
        // Queries server for location updates
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", getResources().getInteger(R.integer.LOCATION_GET));
        } catch(Exception e) {
            e.printStackTrace();
        }
        onSend(obj);
    }

    /**
     * Continuous requests for getting other users location data to the server
     */
    private void continuousUpdate() {
        int delay = 0; // 0 seconds
        int period = 3000; // 3 seconds
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getLocation();
                sendLocation();
            }
        }, delay, period);
    }

    /**
     * Sends location data of waypoints to server, only called once
     */
    void sendWaypoints() {
        for (int i = 0; i < (cWaypoints.size() - 1); i +=2) {
            System.out.println("WP: " + cWaypoints.get(i) + ", " + cWaypoints.get(i + 1));
            JSONObject loc = new JSONObject();
            try {
                loc.put("x", cWaypoints.get(i));
                loc.put("y", cWaypoints.get(i + 1));
            } catch(Exception e) {
                e.printStackTrace();
            }
            JSONObject obj = new JSONObject();
            try {
                obj.put("action", getResources().getInteger(R.integer.GAME_ADD_WAYPOINT));
                obj.put("info", "Waypoint " + Integer.toString(i / 2));
                obj.put("location", loc);
            } catch(Exception e) {
                e.printStackTrace();
            }
            onSend(obj);
        }
    }

}
