package com.comp30022.tarth.catchmeifyoucan.UI;

import android.Manifest;
import android.app.Activity;
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

import com.comp30022.tarth.catchmeifyoucan.Server.Communication;
import com.comp30022.tarth.catchmeifyoucan.Server.Message;
import com.comp30022.tarth.catchmeifyoucan.Game.ChatFragment;
import com.comp30022.tarth.catchmeifyoucan.Game.MapFragment;
import com.comp30022.tarth.catchmeifyoucan.Game.OptionsFragment;
import com.comp30022.tarth.catchmeifyoucan.R;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity implements Communication, OptionsFragment.FragmentCommunication,
        ChatFragment.FragmentCommunication {

    private final static int CHAT_ITEM_ID = 0;
    private final static int MAP_ITEM_ID = 1;
    private final static int OPTIONS_ITEM_ID = 2;

    private BottomNavigationView navigation;
    private Fragment mapFragment;
    private Fragment chatFragment;
    private Fragment optionsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        WebSocketClient.getClient().setActivity(this);

        // Add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapFragment = new MapFragment();
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

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_map, new SupportMapFragment());
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
        MenuItem homeItem = navigation.getMenu().getItem(MAP_ITEM_ID);
        int selectedItemId = navigation.getSelectedItemId();
        if (selectedItemId != homeItem.getItemId()) {
            navigation.getMenu().getItem(MAP_ITEM_ID).setChecked(true);
            switchFragment(homeItem);
        } else {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
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
                    mapViewFragment.getMapAsync((OnMapReadyCallback) this);
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

    // Displays a toast message
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Broadcasts location to server
    private void broadcastLocation() {
        //curr_latitude, curr_longitude
    }

    @Override
    public void onSend(JSONObject obj) {
        WebSocketClient.getClient().send(obj.toString());
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
                    ((ChatFragment) chatFragment).onResponse(message);
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

    // Resets the current activity connected to the WebSocket upon terminating child activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                WebSocketClient.getClient().setActivity(this);
            }
        }
    }

}
