package com.comp30022.tarth.catchmeifyoucan.UI;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.comp30022.tarth.catchmeifyoucan.Account.Communication;
import com.comp30022.tarth.catchmeifyoucan.Account.Message;
import com.comp30022.tarth.catchmeifyoucan.Game.ChatFragment;
import com.comp30022.tarth.catchmeifyoucan.Game.MapFragment;
import com.comp30022.tarth.catchmeifyoucan.Game.OptionsFragment;
import com.comp30022.tarth.catchmeifyoucan.R;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapClickListener, Communication, OptionsFragment.FragmentCommunication,
        ChatFragment.FragmentCommunication {

    private static final Integer GAME_CREATE = 700;
    private static final Integer GAME_ADD = 703;
    private static final Integer GAME_EXIT = 706;
    private static final Integer GAME_GET = 709;
    private static final Integer GAME_DELETE = 712;
    private static final Integer GAME_USER_REMOVE = 715;
    private static final Integer GAME_GET_CURRENT = 718;
    private static final Integer GAME_NOTIFICATION_SEND = 721;
    private static final Integer GAME_NOTIFICATION_RECEIVE = 722;
    private static final Integer GAME_GET_USER = 723;
    private static final Integer GAME_OWNER = 1;
    private static final Integer GAME_PLAYER = 0;

    private static final Integer GAME_CREATE_SUCCESS = 701;
    private static final Integer GAME_CREATE_FAIL = 702;
    private static final Integer GAME_ADD_SUCCESS = 704;
    private static final Integer GAME_ADD_FAIL = 705;
    private static final Integer GAME_EXIT_SUCCESS = 707;
    private static final Integer GAME_EXIT_FAIL = 708;
    private static final Integer GAME_GET_SUCCESS = 710;
    private static final Integer GAME_GET_FAIL = 711;
    private static final Integer GAME_DELETE_SUCCESS = 713;
    private static final Integer GAME_DELETE_FAIL = 714;
    private static final Integer GAME_USER_REMOVE_SUCCESS = 716;
    private static final Integer GAME_USER_REMOVE_FAIL = 717;
    private static final Integer GAME_GET_CURRENT_SUCCESS = 719;
    private static final Integer GAME_GET_CURRENT_FAIL = 720;
    private static final Integer GAME_GET_USER_SUCCESS = 724;
    private static final Integer GAME_GET_USER_FAIL = 725;

    private final static int CHAT_ITEM_ID = 0;
    private final static int MAP_ITEM_ID = 1;
    private final static int OPTIONS_ITEM_ID = 1;

    private BottomNavigationView navigation;
    private Fragment mapFragment;
    private Fragment chatFragment;
    private Fragment optionsFragment;

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
    boolean addWaypoints = false;
    MapDirectionsData lastDirectionsData = null;
    List<Marker> mMarkers = new ArrayList<Marker>();
    private static final double WP_RADIUS = 100;
    private boolean nearWp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        LoginActivity.getClient().setmCurrentActivity(this);

        mapFragment = new MapFragment();
        chatFragment = new ChatFragment();
        optionsFragment = new OptionsFragment();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.getMenu().getItem(MAP_ITEM_ID).setChecked(true);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switchFragment(item);
                return true;
            }
        });

        MenuItem item = navigation.getMenu().getItem(MAP_ITEM_ID);
        switchFragment(item);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_map, new SupportMapFragment());
        transaction.commit();

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
    }

    @Override
    public void onBackPressed() {
        MenuItem homeItem = navigation.getMenu().getItem(MAP_ITEM_ID);
        int selectedItemId = navigation.getSelectedItemId();
        if (selectedItemId != homeItem.getItemId()) {
            navigation.getMenu().getItem(MAP_ITEM_ID).setChecked(true);
            switchFragment(homeItem);
        } else {
            super.onBackPressed();
        }
    }

    private void switchFragment(MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.navigationChat:
                transaction.replace(R.id.fragment_container, chatFragment);
                if (this.getActionBar() != null) {
                    this.getActionBar().setTitle("Game Chat");
                }
                break;
            case R.id.navigationMap:
                transaction.replace(R.id.fragment_container, mapFragment);
                if (mapFragment.isAdded()) {
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapViewFragment = (SupportMapFragment) mapFragment.getChildFragmentManager().findFragmentById(R.id.map);
                    mapViewFragment.getMapAsync(this);
                }
                if (this.getActionBar() != null) {
                    this.getActionBar().setTitle("Game Map");
                }
                break;
            case R.id.navigationOptions:
                transaction.replace(R.id.fragment_container, optionsFragment);
                if (this.getActionBar() != null) {
                    this.getActionBar().setTitle("Game Options");
                }
                break;
        }
        transaction.commit();
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
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
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
//        if (mCurrLocationMarker != null) {
//            mCurrLocationMarker.remove();
//        }
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
        int count = mMarkers.size();
        for(int i=0; i<count; i++){
            double wp_latitude = mMarkers.get(i).getPosition().latitude;
            double wp_longitude = mMarkers.get(i).getPosition().longitude;
            if((curr_latitude-wp_latitude)*(curr_latitude-wp_latitude)+(curr_longitude-wp_longitude)*(curr_longitude-wp_longitude)
                    <= WP_RADIUS*WP_RADIUS){
                toast("A waypoint is nearby");
                nearWp = true;
                break;
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
                return;
            }
        }
    }

    public void onClick(View v) {
        Object dataTransfer[] = new Object[3];

        switch(v.getId()) {
            case R.id.B_search: {
                EditText tf_location = (EditText) findViewById(R.id.TF_location);
                String location = tf_location.getText().toString();
                List<Address> addressList = null;
                MarkerOptions markerOptions = new MarkerOptions();
                Log.d("location = ", location);

                if (!location.equals("")) {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 5);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList != null) {
                        for (int i = 0; i < addressList.size(); i++) {
                            Address myAddress = addressList.get(i);
                            LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                            markerOptions.position(latLng);
                            mMap.addMarker(markerOptions);
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        }
                    }
                }
            }
            break;

            case R.id.B_addWaypoints:
                addWaypoints = true;
                break;

            case R.id.B_finishAddWP:
                addWaypoints = false;
                break;

            case R.id.B_route:
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
                break;

            case R.id.B_ar:
                if(nearWp){
                    nearWp = false;
                    Intent intent = new Intent(this, DashboardActivity.class);
                    startActivity(intent);
                }
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
        checkGooglePlayServices();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
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
        marker.setDraggable(true);
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        end_latitude = marker.getPosition().latitude;
        end_longitude =  marker.getPosition().longitude;

        Log.d("end_lat",""+end_latitude);
        Log.d("end_lng",""+end_longitude);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(addWaypoints){
            Marker waypoint = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("waypoint")
                    .snippet(latLng.toString()));
            //waypoint.showInfoWindow();

            mMarkers.add(waypoint);
        }
    }

    // Displays a toast message
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Broadcasts location to server
    private void broadcastLocation() {
        //curr_latitude, curr_longitude
    }

    private void verify(Message message) {
        System.out.println("CODE" + message.getCode());
        if (message.getCode().equals(GAME_GET_USER_SUCCESS)) {
            toast("Game get users successful");
            ((OptionsFragment) optionsFragment).onResponse(message);
        } else if (message.getCode().equals(GAME_GET_USER_FAIL)) {
            toast("Game get users failure");
        } else if (message.getCode().equals(614)) {
            // TESTING TESTING
            ((ChatFragment) chatFragment).onResponse(message);
        }
    }

    @Override
    public void onSend(JSONObject obj) {
        LoginActivity.getClient().send(obj.toString());
    }

    @Override
    public void response(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                verify(message);
            }
        });
    }

    @Override
    public void onExit() {
        onBackPressed();
        onBackPressed();
    }

}
