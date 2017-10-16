package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.comp30022.tarth.catchmeifyoucan.Server.Communication;
import com.comp30022.tarth.catchmeifyoucan.Server.Message;
import com.comp30022.tarth.catchmeifyoucan.R;
import com.comp30022.tarth.catchmeifyoucan.Server.WebSocketClient;

import org.json.JSONObject;

/* Opens a detailed view of a user and letting them make changes */
public class SettingsActivity extends AppCompatActivity implements Communication{

    private static final Integer PROFILE_ACTION_SUCCESS = 203;       // Profile get success
    private static final Integer PROFILE_ACTION = 102;       // Profile request
    private static final Integer FRIEND_CHECK = 509;       // Check if friend is online request
    private static final Integer FRIEND_CHECK_FAIL = 510; // Online check fail
    private static final Integer FRIEND_CHECK_SUCCESS = 511; // Online check success
    private static final Integer PROFILE_UPDATE = 103; // Profile Update request
    private static final Integer PROFILE_UPDATE_SUCCESS = 104; // Profile Update success
    private static final Integer PROFILE_UPDATE_FAIL = 105; // Profile Update failure


    EditText EditTextName;
    EditText EditTextLocation;
    EditText EditTextStatus;
    EditText EditTextEmail;
    TextView textViewUsername;
    TextView textViewOnline;

   //private Button buttonGet;
    private Button buttonUpdate;

    String getUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        WebSocketClient.getClient().setActivity(this);

        //buttonGet = (Button) findViewById(R.id.buttonGet);
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);

        // Set unchangable fields
        textViewUsername = (TextView) findViewById(R.id.Username);
        textViewOnline = (TextView) findViewById(R.id.Online);

        EditTextLocation = (EditText) findViewById(R.id.Location);
        EditTextStatus = (EditText) findViewById(R.id.Status);
        EditTextName = (EditText) findViewById(R.id.Name);
        EditTextEmail = (EditText) findViewById(R.id.Email);

        // Get username from dashboard
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null)
        {
            getUsername = (String) bd.get("username");
        }

        /*buttonGet.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        // Update fields
        getInfo();

        buttonUpdate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSettings();
            }
        });
    }

    private void changeSettings() {
        JSONObject obj = new JSONObject();

        TextView name = (TextView) findViewById(R.id.Name);
        TextView location = (TextView) findViewById(R.id.Location);
        TextView status = (TextView) findViewById(R.id.Status);
        TextView newEmail = (TextView) findViewById(R.id.Email);

        try {
            obj.put("action", PROFILE_UPDATE);
            obj.put("name", name.getText());
            obj.put("email", newEmail.getText());
            obj.put("location", location.getText());
            obj.put("status", status.getText());

            //System.out.println("SentInfo->" + obj.toString(4));
        } catch(Exception e) {
            e.printStackTrace();
        }
        WebSocketClient.getClient().send(obj.toString());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /* Sends JSON request for latest information of user */
    private void getInfo() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("action", PROFILE_ACTION);
            //System.out.println("SentInfo->" + obj.toString(4));
        } catch(Exception e) {
            e.printStackTrace();
        }
        WebSocketClient.getClient().send(obj.toString());
    }

    /* Sends request to see if user is online */
    private void getOnline(String user) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("username", user);
            obj.put("action", FRIEND_CHECK);
            //System.out.println("SentOnline->" + obj.toString(4));
        } catch(Exception e) {
            e.printStackTrace();
        }
        WebSocketClient.getClient().send(obj.toString());
    }

    /* Handles the response logic from a message sent by the server*/
    private void verify(Message message) {
        /*System.out.println("Message received");

        System.out.println("recv->" + message.toString());
        System.out.println("getAction->" + message.getCode());
        System.out.println(message.getCode() + " + " + PROFILE_UPDATE_SUCCESS);*/

        if (message.getAction() != null) {

            // Update fields with latest information before user is allowed to change
            if (message.getAction().equals(PROFILE_ACTION_SUCCESS)) {

                textViewUsername.setText("@" + message.getUsername());
                getOnline(message.getUsername());

                EditTextLocation.setText(message.getLocation());
                EditTextStatus.setText(message.getStatus());
                EditTextName.setText(message.getName());
                EditTextEmail.setText(message.getEmail());


                if (message.getLocation() == "") {
                    EditTextLocation.setText("Enter Location Here");
                }

                if (message.getStatus() == "") {
                    EditTextStatus.setText("Enter Custom Status Here");
                }

                // System.out.println("Profile get success");
            }

        } else if (message.getCode() != null) {
            // Update UI to show Online
            if (message.getCode().equals(FRIEND_CHECK_SUCCESS)) {
                textViewOnline.setTextColor(Color.parseColor("#16B72E"));
                textViewOnline.setText("ONLINE");
                textViewOnline.setTypeface(null, Typeface.BOLD_ITALIC);
            // Update UI to show Offline
            } else if (message.getCode().equals(FRIEND_CHECK_FAIL)) {
                textViewOnline.setText("OFFLINE");
                textViewOnline.setTextColor(Color.parseColor("#B72616"));
                textViewOnline.setTypeface(null, Typeface.BOLD_ITALIC);
            // manages response to profile update request
            } else if (message.getCode().equals(PROFILE_UPDATE_SUCCESS)) {
                getInfo();
                toast("Profile Update Successful");
            } else if (message.getCode().equals(PROFILE_UPDATE_FAIL)) {
                //getInfo();
                toast("Profile Update Failure. Try again later.");
            }
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

    // Displays a toast message
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}


