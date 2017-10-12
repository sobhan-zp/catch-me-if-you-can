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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.comp30022.tarth.catchmeifyoucan.Account.Communication;
import com.comp30022.tarth.catchmeifyoucan.Account.Message;
import com.comp30022.tarth.catchmeifyoucan.Account.User;
import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendlistActivity extends AppCompatActivity implements Communication {

    private static final Integer FRIEND_GET = 500;            // Friend get request
    private static final Integer FRIEND_GET_FAIL = 501;       // Friend get failure
    private static final Integer FRIEND_GET_SUCCESS = 502;    // Friend get success
    private static final Integer FRIEND_SEARCH = 503;         // Friend search request
    private static final Integer FRIEND_SEARCH_FAIL = 504;    // Friend search failure
    private static final Integer FRIEND_SEARCH_SUCCESS = 505; // Friend search success
    private static final Integer FRIEND_CHECK = 509;          // Friend check request
    private static final Integer FRIEND_CHECK_FAIL = 510;     // Friend check failure
    private static final Integer FRIEND_CHECK_SUCCESS = 511;  // Friend check success

    private ArrayAdapter<String> adapter;
    private List<String> array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);
        LoginActivity.getClient().setmCurrentActivity(this);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = menuItem.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_name) {
            openAdd();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    // Obtains a list of all friends from the server
    private void getFriend() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", FRIEND_GET);
        } catch(Exception e) {
            e.printStackTrace();
        }
        LoginActivity.getClient().send(obj.toString());
    }

    /*
    // Checks if a friend is online
    private void checkFriend() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", FRIEND_CHECK);
            obj.put("username", "1");
        } catch(Exception e) {
            e.printStackTrace();
        }
        LoginActivity.getClient().send(obj.toString());
    }
    // Searches for the details of an existing user
    private void searchFriend() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", FRIEND_SEARCH);
            obj.put("username", "TEST");
        } catch(Exception e) {
            e.printStackTrace();
        }
        LoginActivity.getClient().send(obj.toString());
    }
    */

    // Verifies responses from the server
    private void verify(Message message) {
        if (message.getCode().equals(FRIEND_GET_SUCCESS)) {
            toast("Friend get success");

            // Repopulates friendlist
            User[] users = message.getUsers();
            array.clear();
            for (User user : users) {
                array.add(user.getUsername());
            }
            adapter.notifyDataSetChanged();

        } else if (message.getCode().equals(FRIEND_GET_FAIL)) {
            toast("Friend get failure");
        } else if (message.getCode().equals(FRIEND_SEARCH_SUCCESS)) {
            System.out.println("Friend search success - here");
        } else if (message.getCode().equals(FRIEND_SEARCH_FAIL)) {
            System.out.println("Friend search failure");
        } else {
            toast("Error: Unknown response received");
        }
    }

    // Navigates to User Activity
    private void openUser(String username) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("dashboard", false);
        startActivity(intent);
    }

    // Navigates to Add Activity
    private void openAdd() {
        Intent intent = new Intent(this, AddActivity.class);
        startActivityForResult(intent, 1);
    }

    // Resets the current activity connected to the WebSocket upon killing child activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                LoginActivity.getClient().setmCurrentActivity(this);
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
                verify(message);
            }
        });
    }

    // Displays a toast message
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}

