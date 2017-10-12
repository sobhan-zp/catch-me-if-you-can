package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.Account.Communication;
import com.comp30022.tarth.catchmeifyoucan.Account.Message;
import com.comp30022.tarth.catchmeifyoucan.Account.User;
import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;

/* Opens a detailed view of a user by querying the server */
public class UserActivity extends AppCompatActivity implements Communication {


    // Server protocol codes
    private static final Integer FRIEND_SEARCH = 503;       // Profile request
    private static final Integer FRIEND_SEARCH_SUCCESS = 505;       // Profile get success
    private static final Integer FRIEND_CHECK = 509;       // Check if friend is online request
    private static final Integer FRIEND_CHECK_FAIL = 510; // Online check fail
    private static final Integer FRIEND_CHECK_SUCCESS = 511; // Online check success

    TextView textViewName;
    TextView textViewUsername;
    TextView textViewLocation;
    TextView textViewStatus;
    TextView textViewOnline;
    //private Button buttonGet;
    private Button buttonChat;
    User profile;
    String getUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        // Set server to send responses back to this class
        LoginActivity.getClient().setmCurrentActivity(this);

        //buttonGet = (Button) findViewById(R.id.buttonGet);
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
            //System.out.println("getUsername" + getUsername);
        }

        // Fetch details
        getInfo(getUsername);

        /*buttonGet.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo(getUsername);
            }
        });*/

        buttonChat.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChat(getUsername);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    // Redirects to user to chat
    private void openChat(String friend) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("friend", friend);
        startActivity(intent);
    }

    /* Sends server a JSON request for the information of a user, takes in unique username as string*/
    private void getInfo(String uname) {
        JSONObject obj = new JSONObject();
        System.out.println("uname" + uname);

        try {
            obj.put("username", uname);
            obj.put("action", FRIEND_SEARCH);
            //System.out.println("SentInfo->" + obj.toString(4));
        } catch(Exception e) {
            e.printStackTrace();
        }
        // send to server
        LoginActivity.getClient().send(obj.toString());
    }

    /* Sends server a JSON request to check if user is online*/
    private void getOnline() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("username", getUsername);
            obj.put("action", FRIEND_CHECK);
            //System.out.println("SentOnline->" + obj.toString(4));
        } catch(Exception e) {
            e.printStackTrace();
        }
        LoginActivity.getClient().send(obj.toString());
    }

    // Handles server response logic
    private void verify(Message message) {
        //System.out.println("Message received");
        System.out.println("getCode->" + message.getCode());

        if (message.getCode().equals(FRIEND_SEARCH_SUCCESS)) {
            // Strip result from JSON
            profile = message.getResult()[0];

            /*System.out.println("recvname->" + profile.getName());
            System.out.println("recvusername->" + profile.getUsername());
            System.out.println("recvemail->" + profile.getEmail());
            System.out.println("recvstatus->" + profile.getStatus());
            System.out.println("recvlocation->" + profile.getLocation());*/

            // Assign updated fields to UI
            textViewName.setText(profile.getName());
            textViewUsername.setText("@" + profile.getUsername());
            textViewLocation.setText(profile.getLocation());
            textViewStatus.setText(profile.getStatus());

            getOnline();

            //System.out.println("Profile get success");
        }

        // If online
        if (message.getCode().equals(FRIEND_CHECK_SUCCESS)) {
            textViewOnline.setTextColor(Color.parseColor("#16B72E"));
            textViewOnline.setText("ONLINE");
            textViewOnline.setTypeface(null, Typeface.BOLD_ITALIC);

        // If offline
        } else if (message.getCode().equals(FRIEND_CHECK_FAIL)) {
            textViewOnline.setText("OFFLINE");
            textViewOnline.setTextColor(Color.parseColor("#B72616"));
            textViewOnline.setTypeface(null, Typeface.BOLD_ITALIC);
        } else {
            System.out.println("User Error: Unknown response received");
        }

    }

        /* Grabs response from server */
    @Override
    public void response(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                verify(message);
            }
        });
    }
}

