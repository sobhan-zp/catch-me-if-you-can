package com.comp30022.tarth.catchmeifyoucan.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.comp30022.tarth.catchmeifyoucan.Server.Communication;
import com.comp30022.tarth.catchmeifyoucan.Server.Message;
import com.comp30022.tarth.catchmeifyoucan.Server.Result;
import com.comp30022.tarth.catchmeifyoucan.R;
import com.comp30022.tarth.catchmeifyoucan.Server.WebSocketClient;

import org.json.JSONObject;

/* Opens a detailed view of a user by querying the server */
public class UserActivity extends Activity implements Communication {

    TextView textViewName;
    TextView textViewUsername;
    TextView textViewLocation;
    TextView textViewStatus;
    TextView textViewOnline;

    private String getUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        // Set server to send responses back to this class
        WebSocketClient.getClient().setActivity(this);

        ImageButton fabChat = (ImageButton) findViewById(R.id.floatingChat);

        textViewLocation = (TextView) findViewById(R.id.Location);
        textViewStatus = (TextView) findViewById(R.id.Status);
        textViewUsername = (TextView) findViewById(R.id.Username);
        textViewName = (TextView) findViewById(R.id.Name);
        textViewOnline = (TextView) findViewById(R.id.Online);

        ImageView profilePicture = (ImageView) findViewById(R.id.ProfilePicture);
        profilePicture.setImageResource(R.mipmap.temp_placeholder);

        // Get username from dashboard
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null) {
            getUsername = (String) bd.get("username");
        }

        // Fetch details
        getInfo(getUsername);

        fabChat.setOnClickListener(new View.OnClickListener() {

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

    // Resets the current activity connected to the WebSocket upon terminating child activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                WebSocketClient.getClient().setActivity(this);
            }
        }
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
                    String username = "@" + profile.getUsername();
                    String location = "Location: " + profile.getLocation();

                    // Assign updated fields to UI
                    textViewName.setText(profile.getName());
                    textViewUsername.setText(username);
                    textViewLocation.setText(location);
                    textViewStatus.setText(profile.getStatus());

                    getOnline();
                }
                // If online
                if (message.getCode().equals(getResources().getInteger(R.integer.FRIEND_CHECK_SUCCESS))) {
                    textViewOnline.setTextColor(Color.parseColor("#16B72E"));
                    String online = "ONLINE";
                    textViewOnline.setText(online);
                    textViewOnline.setTypeface(null, Typeface.BOLD_ITALIC);
                    toast("Online");
                }
                // If offline
                else if (message.getCode().equals(getResources().getInteger(R.integer.FRIEND_CHECK_FAIL))) {
                    String offline = "OFFLINE";
                    textViewOnline.setText(offline);
                    textViewOnline.setTextColor(Color.parseColor("#B72616"));
                    textViewOnline.setTypeface(null, Typeface.BOLD_ITALIC);
                    toast("Offline");
                } else {
                    toast("User Error: Unknown response received");
                }
            }
        });
    }

    // Redirects to user to chat
    private void openChat(String friend) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("friend", friend);
        startActivityForResult(intent, 1);
    }

    /* Sends server a JSON request for the information of a user, takes in unique username as string */
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        WebSocketClient.getClient().send(obj.toString());
    }

    // Displays a toast message
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
