package com.comp30022.tarth.catchmeifyoucan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class DashboardActivity extends AppCompatActivity {

    Button buttonBack;

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
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void back(View view) {
        finish();
    }
}
