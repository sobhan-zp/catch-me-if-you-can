package com.comp30022.tarth.catchmeifyoucan.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.comp30022.tarth.catchmeifyoucan.Server.Communication;
import com.comp30022.tarth.catchmeifyoucan.Server.Message;
import com.comp30022.tarth.catchmeifyoucan.Server.Result;
import com.comp30022.tarth.catchmeifyoucan.R;
import com.comp30022.tarth.catchmeifyoucan.Server.WebSocketClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendlistActivity extends AppCompatActivity implements Communication {

    private ArrayAdapter<String> adapter;
    private List<String> array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);
        WebSocketClient.getClient().setActivity(this);

        // Add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Enable Internet permissions
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Sets up friendlist
        ListView listView = (ListView)findViewById(android.R.id.list);
        //TextView textViewEmpty = (TextView)findViewById(android.R.id.empty);
        final ListView listViewFriends = (ListView) findViewById(R.id.listViewFriends);
        array = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(
                this,
                R.layout.list_one_item,
                array
        );
        listViewFriends.setAdapter(adapter);

        listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openUser(listViewFriends.getItemAtPosition(position).toString());
            }
        });

        // Obtains the list of friends from the server upon incovation
        getFriend();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_friendlist
        getMenuInflater().inflate(R.menu.menu_friendlist, menu);
        return true;
    }

    // Set back button on action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_name:
                openAdd();
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

    // Obtains a list of all friends from the server
    private void getFriend() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", getResources().getInteger(R.integer.FRIEND_GET));
        } catch(Exception e) {
            e.printStackTrace();
        }
        WebSocketClient.getClient().send(obj.toString());
    }

    // Navigates to User Activity
    private void openUser(String username) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("dashboard", false);
        startActivityForResult(intent, 1);
    }

    // Navigates to Add Activity
    private void openAdd() {
        Intent intent = new Intent(this, AddActivity.class);
        startActivityForResult(intent, 1);
    }

    // Resets the current activity connected to the WebSocket upon terminating child activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                WebSocketClient.getClient().setActivity(this);
            }
        }
        getFriend();
    }

    // Called by the WebSocket upon receiving a message
    @Override
    public void response(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message.getCode().equals(getResources().getInteger(R.integer.FRIEND_GET_SUCCESS))) {
                    toast("Friend get success");

                    // Repopulates list
                    Result[] results = message.getResult();
                    array.clear();
                    for (Result result : results) {
                        array.add(result.getUsername());
                    }
                    adapter.notifyDataSetChanged();

                } else if (message.getCode().equals(getResources().getInteger(R.integer.FRIEND_GET_FAIL))) {
                    toast("Friend get failure");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.FRIEND_SEARCH_SUCCESS))) {
                    System.out.println("Friend search success - here");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.FRIEND_SEARCH_FAIL))) {
                    System.out.println("Friend search failure");
                } else {
                    toast("Error: Unknown response received");
                }
            }
        });
    }

    // Displays a toast message
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}

