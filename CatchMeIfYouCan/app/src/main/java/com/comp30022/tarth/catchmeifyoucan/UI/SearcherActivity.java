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
import com.comp30022.tarth.catchmeifyoucan.Game.Waypoint;
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

public class SearcherActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener,
        Communication, OptionsFragment.FragmentCommunication,
        ChatFragment.FragmentCommunication {

    private GoogleMap mMap;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private Marker mCurrLocationMarker = null;
    public static final int REQUEST_LOCATION_CODE = 999;
    private static int UPDATE_INTERVAL = 5000; //SEC
    private static int FATEST_INTERVAL = 3000; //SEC
    private static int DISPLACEMENT = 10; // METERS
    double curr_latitude, curr_longitude;
    double end_latitude, end_longitude;
    MapDirectionsData lastDirectionsData = null;

    List<Marker> mMarkers = new ArrayList<>();
    private static final double WP_RADIUS = 0.00001;
    private boolean nearWp = false;
    private String theWpId = "";

    private List<Marker> othersMarker = new ArrayList<>();

    private final static int CHAT_ITEM_ID = 0;
    private final static int MAP_ITEM_ID = 1;
    private final static int OPTIONS_ITEM_ID = 2;

    SupportMapFragment mapFragment;
    Fragment chatFragment;
    Fragment optionsFragment;
    private BottomNavigationView navigation;

    private Marker tMarker = null;
    private Double targetX;
    private Double targetY;

    static int i = 0;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searcher);
        WebSocketClient.getClient().setActivity(this);

        // Add back button
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Check if Google Play Services available
        if (!checkGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }

        //Check location permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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

    private void switchFragment(MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.navigationChat:
                transaction.hide(mapFragment);
                transaction.add(R.id.fragment_container, chatFragment);
                transaction.addToBackStack("map");
                //transaction.replace(R.id.fragment_container, chatFragment);
                if (this.getActionBar() != null) {
                    this.getActionBar().setTitle("Game Chat");
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

                /*
                if (mapFragment.isAdded()) {
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapViewFragment = (SupportMapFragment) mapFragment.getChildFragmentManager().findFragmentById(R.id.map);
                    mapViewFragment.getMapAsync((OnMapReadyCallback) this);
                }
                */
                if (this.getActionBar() != null) {
                    this.getActionBar().setTitle("Game Map");
                }
                break;
            case R.id.navigationOptions:
                transaction.hide(mapFragment);
                transaction.add(R.id.fragment_container, optionsFragment);
                transaction.addToBackStack("map");
                //transaction.replace(R.id.fragment_container, optionsFragment);
                if (this.getActionBar() != null) {
                    this.getActionBar().setTitle("Game Options");
                }
                break;
        }
        transaction.commit();
    }

    // Set back button on action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

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
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mCurrentLocation != null) {
            curr_latitude = mCurrentLocation.getLatitude();
            curr_longitude = mCurrentLocation.getLongitude();
        }

        LatLng latLng = new LatLng(curr_latitude, curr_longitude);
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

        if(mCurrLocationMarker != null){
            String currLocation = curr_latitude+","+curr_longitude;
            toast(currLocation);
        }

        checkNearWaypoint();
    }

    protected void checkNearWaypoint(){
        if(mMarkers.size() > 0) {
            int nearestWp = -1;
            double nearestRadius = WP_RADIUS * WP_RADIUS;
            int count = mMarkers.size();
            for (int i = 0; i < count; i++) {
                double wp_latitude = mMarkers.get(i).getPosition().latitude;
                double wp_longitude = mMarkers.get(i).getPosition().longitude;
                double radius = (curr_latitude - wp_latitude) * (curr_latitude - wp_latitude) +
                        (curr_longitude - wp_longitude) * (curr_longitude - wp_longitude);
                if ((radius < (WP_RADIUS * WP_RADIUS)) && radius < nearestRadius) {
                    nearestWp = i;
                    nearestRadius = radius;
                }
            }
            if(nearestWp >= 0) {
                mMarkers.get(nearestWp).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                nearWp = true;
                theWpId = mMarkers.get(nearestWp).getId();
            }
        }
    }

    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
    }


    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

        startLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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

    public void onClick(View v) {
        Object dataTransfer[] = new Object[3];

        if(v.getId() == R.id.B_route) {
            if(lastDirectionsData != null){
                lastDirectionsData.clearPolyline();
            }
            String url = getDirectionsUrl();
            MapDirectionsData directionsData = new MapDirectionsData();
            dataTransfer[0] = mMap;
            dataTransfer[1] = url;
            dataTransfer[2] = new LatLng(end_latitude, end_longitude);
            directionsData.execute(dataTransfer);
            lastDirectionsData = directionsData;
        }
    }

    private String getDirectionsUrl() {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+curr_latitude+","+curr_longitude);
        googleDirectionsUrl.append("&destination="+end_latitude+","+end_longitude);
        googleDirectionsUrl.append("&mode=walking");
        googleDirectionsUrl.append("&key="+"AIzaSyAsE7HmeYpP-QDRYblsZZq_yClezBjFQoE");

        return googleDirectionsUrl.toString();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //checkGooglePlayServices();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.d("onConnectionSuspended", "Connection suspended");
        mGoogleApiClient.connect();
    }
    @Override
    protected void onStop() {
        super.onStop();
        // disconnect when user leaves the interface
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
//        marker.setDraggable(true);
        if(marker.getId().equals(theWpId)){

            // ADD AR FRAGMENT HERE

            // remoce the way point from the wp list
            mMarkers.remove(marker);
            // remove the way point from the map
            marker.remove();
            nearWp = false;
        }
        return true;
    }

    // Displays a toast message
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    public void updateOthers(List<Double> othersLocation){
        for(int j=othersMarker.size()-1; j>=0; j--){
            othersMarker.get(j).remove();
        }
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

    public void updateTarget(){
        if(tMarker != null){
            tMarker.remove();
        }
        LatLng latLng = new LatLng(targetX, targetY);
        end_latitude = targetX;
        end_longitude = targetY;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("target");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        if (mMap != null) {
            tMarker = mMap.addMarker(markerOptions);
        }
    }

    @Override
    public void onResponse(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("CODE" + message.getCode());
                if (message.getAction() != null) {
                    toast("New message from " + message.getFrom() + ": " + message.getMessage());
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_GET_USER_SUCCESS))) {
                    toast("Game get users successful");
                    ((OptionsFragment) optionsFragment).onResponse(message);
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_GET_USER_FAIL))) {
                    toast("Game get users failure");
                } else if (message.getCode().equals(614)) {
                    // TESTING TESTING
                    toast("Location get successful");
                    //((ChatFragment) chatFragment).onResponse(message);
                    // TESTING
                    List<Double> locations = new ArrayList<>();
                    Result[] results = message.getResult();
                    for (Result result : results) {
                        //array.add(Double.toString(result.getX()) + ", " + Double.toString(result.getY()));
                        locations.add(result.getX());
                        locations.add(result.getY());
                    }
                    updateOthers(locations);
                    //adapter.notifyDataSetChanged();
                } else if (message.getCode().equals(615)) {
                    toast("Location get failure");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_EXIT_SUCCESS))) {
                    toast("Game exit successful");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_EXIT_FAIL))) {
                    toast("Game exit failure");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_DELETE_SUCCESS))) {
                    toast("Game delete successful");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_DELETE_FAIL))) {
                    toast("Game delete failure");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.LOCATION_GET2_SUCCESS))) {
                    System.out.println("Target location receive successful");
                    Result result = message.getResult()[0];
                    targetX = result.getX();
                    targetY = result.getY();
                    updateTarget();
                } else if (message.getCode().equals(getResources().getInteger(R.integer.LOCATION_GET2_FAIL))) {
                    System.out.println("Target location receive failed");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_GET_WAYPOINT_SUCCESS))) {
                    toast("Get waypoint success");
                    Result[] results = message.getResult();
                    List<Waypoint> waypoints = new ArrayList<>();
                    for (Result result : results) {
                        waypoints.add(new Waypoint(result.getInfo(), result.getX(), result.getY()));
                    }
                    addWp(waypoints);
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_GET_WAYPOINT_FAIL))) {
                    toast("Get waypoint failure");
                }
            }
        });
    }

    @Override
    public void onExit() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", getResources().getInteger(R.integer.GAME_EXIT));
        } catch(Exception e) {
            e.printStackTrace();
        }
        WebSocketClient.getClient().send(obj.toString());
        onBackPressed();
        onBackPressed();
    }

    @Override
    public void onSend(JSONObject obj) {
        WebSocketClient.getClient().send(obj.toString());
    }

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

    private void getTargetLocation() {
        // Queries server for location updates
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", getResources().getInteger(R.integer.LOCATION_GET2));
        } catch(Exception e) {
            e.printStackTrace();
        }
        onSend(obj);
    }

    private void addWp(List<Waypoint> waypoints){
        if(waypoints.size() > 0) {
            int count = waypoints.size() - 1;
            for (int i = 0; i < count; i ++) {
                LatLng latLng = new LatLng(waypoints.get(i).getX(), waypoints.get(i).getY());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Way Point");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                if (mMap != null) {
                    Marker oMarker = mMap.addMarker(markerOptions);
                    mMarkers.add(oMarker);
                }
            }
        }
    }

    private void continuousUpdate() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int delay = 0; // 0 seconds
                int period = 5000; // 5 seconds
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        sendLocation();
                        getTargetLocation();
                        System.out.println(i);
                        if (i == 1) {
                            getWaypoints();
                        }
                        i ++;
                    }
                }, delay, period);
            }
        });
    }

    private void getWaypoints() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", getResources().getInteger(R.integer.GAME_GET_WAYPOINT));
        } catch(Exception e) {
            e.printStackTrace();
        }
        onSend(obj);
    }

}
