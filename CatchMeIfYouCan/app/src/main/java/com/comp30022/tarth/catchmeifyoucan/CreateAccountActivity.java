package com.comp30022.tarth.catchmeifyoucan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class CreateAccountActivity extends AppCompatActivity {

    Button buttonCreate;
    Button buttonBack;

    // TCP Client
    Client mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        buttonCreate = (Button) findViewById(R.id.buttonCreate);
        buttonCreate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Starts a connection to the server
                new ConnectTask().execute("");
                // Sends a message to the server
                if (mClient != null) {
                    mClient.sendMessage("Testing");
                }
                // Stopping the connection to the server
                if (mClient != null) {
                    mClient.close();
                }
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
