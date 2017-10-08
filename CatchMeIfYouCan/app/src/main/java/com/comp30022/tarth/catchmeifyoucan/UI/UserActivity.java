package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
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

    private static final Integer FRIEND_SEARCH = 503;            // Profile get request
    private static final Integer FRIEND_SEARCH_FAIL = 504;       // Profile get failure
    private static final Integer FRIEND_SEARCH_SUCCESS = 505;       // Profile get success

    TextView textViewName;
    TextView textViewUsername;
    TextView textViewLocation;
    TextView textViewStatus;
    private Button buttonGet;
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

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null)
        {
            getUsername = (String) bd.get("username");
            textViewUsername.setText("@" + getUsername);
        }

        buttonGet.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                get(getUsername);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void get(String name) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("username", name);
            obj.put("action", FRIEND_SEARCH);
            System.out.println("Sent->" + obj.toString(4)); // Print it with specified indentation
        } catch(Exception e) {
            e.printStackTrace();
        }
        LoginActivity.getClient().send(obj.toString());
    }

    private void verify(Message message) {
        System.out.println("Message received");
        if (message.getCode().equals(FRIEND_SEARCH_SUCCESS)) {
            // strip user from array
            User user = (User) message.getResult()[0];
            //System.out.println("Recv->" + user.getUsername());

            textViewName.setText(user.getName());

            System.out.println("Profile get success");
        } else if (message.getCode().equals(FRIEND_SEARCH_FAIL)) {
            System.out.println("Profile get failure");
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

