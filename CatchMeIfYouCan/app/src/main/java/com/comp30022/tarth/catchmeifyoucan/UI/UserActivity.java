package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.R;

public class UserActivity extends AppCompatActivity {

    TextView textViewName;
    TextView textViewID;
    TextView textViewUsername;
    TextView textViewLocation;
    TextView textViewStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Button buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewID = (TextView) findViewById(R.id.textViewID);
        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void back() {
        finish();
    }
}
