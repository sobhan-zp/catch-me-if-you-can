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

        textViewName = (TextView) findViewById(R.id.Name);
        textViewUsername = (TextView) findViewById(R.id.Username);
        textViewLocation = (TextView) findViewById(R.id.Location);
        textViewStatus = (TextView) findViewById(R.id.Status);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void back() {
        finish();
    }
}

