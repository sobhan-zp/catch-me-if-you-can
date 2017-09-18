package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.comp30022.tarth.catchmeifyoucan.R;

public class DashboardActivity extends AppCompatActivity {

    Button buttonBack;
    Button buttonMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                back(v);
            }
        });

        buttonMaps = (Button) findViewById(R.id.buttonMaps);
        buttonMaps.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                maps(v);
            }
        });
    }

    public void maps(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void back(View view) {
        finish();
    }
}
