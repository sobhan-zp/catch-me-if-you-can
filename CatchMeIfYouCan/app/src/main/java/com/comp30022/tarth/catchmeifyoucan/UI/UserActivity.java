package com.comp30022.tarth.catchmeifyoucan.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.Server.Communication;
import com.comp30022.tarth.catchmeifyoucan.Server.Message;
import com.comp30022.tarth.catchmeifyoucan.Server.Result;
import com.comp30022.tarth.catchmeifyoucan.R;
import com.comp30022.tarth.catchmeifyoucan.Server.WebSocketClient;

import org.json.JSONObject;

/* Opens a detailed view of a user by querying the server */
public class UserActivity extends AppCompatActivity implements Communication {

    TextView textViewName;
    TextView textViewUsername;
    TextView textViewLocation;
    TextView textViewStatus;
    TextView textViewOnline;
    private Button buttonChat;
    String getUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        // Set server to send responses back to this class
        WebSocketClient.getClient().setActivity(this);

        buttonChat = (Button) findViewById(R.id.buttonChat);

        textViewLocation = (TextView) findViewById(R.id.Location);
        textViewStatus = (TextView) findViewById(R.id.Status);
        textViewUsername = (TextView) findViewById(R.id.Username);
        textViewName = (TextView) findViewById(R.id.Name);
        textViewOnline = (TextView) findViewById(R.id.Online);

        // Get username from dashboard
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null)
        {
            getUsername = (String) bd.get("username");

            // Check if the Send Message Button should appear
            // Shouldn't appear if user is looking at his own profile
            if ((Boolean) bd.get("dashboard") == true) {
                buttonChat.setVisibility(View.GONE);
            }
        }

        // Fetch details
        getInfo(getUsername);

        buttonChat.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChat(getUsername);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    // Redirects to user to chat
    private void openChat(String friend) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("friend", friend);
        startActivityForResult(intent, 1);
    }

    /* Sends server a JSON request for the information of a user, takes in unique username as string*/
    private void getInfo(String uname) {
        JSONObject obj = new JSONObject();
        System.out.println("uname" + uname);

        try {
            obj.put("username", uname);
            obj.put("action", getResources().getInteger(R.integer.FRIEND_SEARCH));
            //System.out.println("SentInfo->" + obj.toString(4));
        } catch(Exception e) {
            e.printStackTrace();
        }
        // send to server
        WebSocketClient.getClient().send(obj.toString());
    }

    /* Sends server a JSON request to check if user is online*/
    private void getOnline() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("username", getUsername);
            obj.put("action", getResources().getInteger(R.integer.FRIEND_CHECK));
            //System.out.println("SentOnline->" + obj.toString(4));
        } catch(Exception e) {
            e.printStackTrace();
        }
        WebSocketClient.getClient().send(obj.toString());
    }

        /* Grabs response from server */
    @Override
    public void onResponse(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message.getCode().equals(getResources().getInteger(R.integer.FRIEND_SEARCH_SUCCESS))) {
                    // Strip result from JSON
                    Result profile = message.getResult()[0];

                    // Assign updated fields to UI
                    textViewName.setText(profile.getName());
                    textViewUsername.setText("@" + profile.getUsername());
                    textViewLocation.setText(profile.getX() + "," + profile.getY());
                    textViewStatus.setText(profile.getStatus());

                    getOnline();
                }

                // If online
                if (message.getCode().equals(getResources().getInteger(R.integer.FRIEND_CHECK_SUCCESS))) {
                    textViewOnline.setTextColor(Color.parseColor("#16B72E"));
                    textViewOnline.setText("ONLINE");
                    textViewOnline.setTypeface(null, Typeface.BOLD_ITALIC);

                    // If offline
                } else if (message.getCode().equals(getResources().getInteger(R.integer.FRIEND_CHECK_FAIL))) {
                    textViewOnline.setText("OFFLINE");
                    textViewOnline.setTextColor(Color.parseColor("#B72616"));
                    textViewOnline.setTypeface(null, Typeface.BOLD_ITALIC);
                } else {
                    System.out.println("User Error: Unknown response received");
                }
            }
        });
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

