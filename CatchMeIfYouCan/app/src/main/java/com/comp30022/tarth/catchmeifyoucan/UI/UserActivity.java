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


public class UserActivity extends AppCompatActivity implements Communication{

    private static final Integer FRIEND_SEARCH = 503;       // Profile request
    private static final Integer FRIEND_SEARCH_FAIL = 504;       // Profile get failure
    private static final Integer FRIEND_SEARCH_SUCCESS = 505;       // Profile get success
    private static final Integer FRIEND_CHECK = 509;       // Check if friend is online request
    private static final Integer FRIEND_CHECK_FAIL = 510; // Online check fail
    private static final Integer FRIEND_CHECK_SUCCESS = 511; // Online check success

    TextView textViewName;
    TextView textViewUsername;
    TextView textViewLocation;
    TextView textViewStatus;
    TextView textViewOnline;
    private Button buttonGet;
    User profile;
    String getUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        LoginActivity.getClient().setmCurrentActivity(this);

        buttonGet = (Button) findViewById(R.id.buttonGet);

        textViewLocation = (TextView) findViewById(R.id.Location);
        textViewStatus = (TextView) findViewById(R.id.Status);
        textViewUsername = (TextView) findViewById(R.id.Username);
        textViewName = (TextView) findViewById(R.id.Name);
        textViewOnline = (TextView) findViewById(R.id.Online);

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null)
        {
            getUsername = (String) bd.get("username");
            System.out.println("getUsername" + getUsername);
        }

        buttonGet.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo(getUsername);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void getInfo(String uname) {
        JSONObject obj = new JSONObject();
        System.out.println("uname" + uname);

        try {
            obj.put("username", uname);
            obj.put("action", FRIEND_SEARCH);
            System.out.println("SentInfo->" + obj.toString(4));
        } catch(Exception e) {
            e.printStackTrace();
        }
        LoginActivity.getClient().send(obj.toString());
    }

    private void getOnline() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("username", getUsername);
            obj.put("action", FRIEND_CHECK);
            System.out.println("SentOnline->" + obj.toString(4));
        } catch(Exception e) {
            e.printStackTrace();
        }
        LoginActivity.getClient().send(obj.toString());
    }

    private void verify(Message message) {
        System.out.println("Message received");


        System.out.println("getCode->" + message.getCode());

        if (message.getCode().equals(FRIEND_SEARCH_SUCCESS)) {
            profile = message.getResult()[0];

            System.out.println("recvname->" + profile.getName());
            System.out.println("recvusername->" + profile.getUsername());
            System.out.println("recvemail->" + profile.getEmail());
            System.out.println("recvstatus->" + profile.getStatus());
            System.out.println("recvlocation->" + profile.getLocation());

            textViewName.setText(profile.getName());
            textViewUsername.setText("@" + profile.getUsername());
            textViewLocation.setText(profile.getLocation());
            textViewStatus.setText(profile.getStatus());
            getOnline();

            System.out.println("Profile get success");
        }

        if (message.getCode().equals(FRIEND_CHECK_SUCCESS)) {
            textViewOnline.setTextColor(Color.parseColor("#16B72E"));
            textViewOnline.setText("ONLINE");
            textViewOnline.setTypeface(null, Typeface.BOLD_ITALIC);
        } else if (message.getCode().equals(FRIEND_CHECK_FAIL)) {
            textViewOnline.setText("OFFLINE");
            textViewOnline.setTextColor(Color.parseColor("#B72616"));
            textViewOnline.setTypeface(null, Typeface.BOLD_ITALIC);
        } else {
            System.out.println("User Error: Unknown response received");
        }

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
}

