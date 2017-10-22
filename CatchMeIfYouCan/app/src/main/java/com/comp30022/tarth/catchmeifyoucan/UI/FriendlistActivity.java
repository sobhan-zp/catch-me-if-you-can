// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

package com.comp30022.tarth.catchmeifyoucan.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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

/**
 * FriendlistActivity.java
 * List of friends
 */
public class FriendlistActivity extends AppCompatActivity implements Communication {

    private ArrayAdapter<String> adapter;
    private List<String> array;

    /**
     * Called when the activity is starting
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);
        WebSocketClient.getClient().setActivity(this);

        // Add back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Sets up friendlist
        final ListView listViewFriends = (ListView) findViewById(R.id.listViewFriends);
        array = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                this,
                R.layout.list_one_item_friends,
                array
        );
        listViewFriends.setAdapter(adapter);

        ImageButton fabAdd = (ImageButton) findViewById(R.id.floatingAdd);

        listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openUser(listViewFriends.getItemAtPosition(position).toString());
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAdd();
            }
        });

        // Obtains the list of friends from the server upon incovation
        getFriend();
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
        getFriend();
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
                } else if (message.getCode().equals(getResources().getInteger(R.integer.FRIEND_GET_SUCCESS))) {
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

    /**
     * Displays a toast message
     * @param message : Message to be displayed
     */
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Obtains a list of all friends from the server
     */
    private void getFriend() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", getResources().getInteger(R.integer.FRIEND_GET));
        } catch(Exception e) {
            e.printStackTrace();
        }
        WebSocketClient.getClient().send(obj.toString());
    }

    /**
     * Navigates to User Activity
     * @param username : Username of user to be loaded
     */
    private void openUser(String username) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("dashboard", false);
        startActivityForResult(intent, 1);
    }

    /**
     * Navigates to Add Activity
     */
    private void openAdd() {
        Intent intent = new Intent(this, AddActivity.class);
        startActivityForResult(intent, 1);
    }

}
