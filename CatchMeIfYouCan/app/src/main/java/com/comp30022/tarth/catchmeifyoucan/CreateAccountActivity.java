package com.comp30022.tarth.catchmeifyoucan;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class CreateAccountActivity extends AppCompatActivity {

    Button buttonCreate;
    Button buttonBack;
    ConnectTask mConnectTask;

    // Example message sent to the server
    String mMessage = "{\"username\": \"vikramgk\", \"password\": \"cellotape\", \"client_ip\": 1234, \"email\": \"nigerian_prince@student.unimelb.edu.au\", \"name\": \"Nigerian Price\", \"date_of_birth\": 0}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        buttonCreate = (Button) findViewById(R.id.buttonCreate);
        buttonCreate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Starts a connection to the server
                mConnectTask = new ConnectTask();
                mConnectTask.execute("");

                //mConnectTask.doInBackground(mMessage);

                /*
                // Sends a message to the server
                if (mClient != null) {
                    mClient.sendMessage(mMessage);
                }
                // Stopping the connection to the server
                if (mClient != null) {
                    mClient.close();
                }
                */
            }
        });

        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                back(v);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void back(View view) {
        finish();
    }
}
