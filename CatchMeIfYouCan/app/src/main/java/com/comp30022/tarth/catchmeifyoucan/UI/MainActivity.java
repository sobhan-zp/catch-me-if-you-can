package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.comp30022.tarth.catchmeifyoucan.R;

public class MainActivity extends AppCompatActivity {

    Button buttonCreateAccount;
    Button buttonLogin;
    Button buttonExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCreateAccount = (Button) findViewById(R.id.buttonCreateAccount);
        buttonCreateAccount.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(v);
            }
        });

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });

        buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit(v);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }

    public void createAccount(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void exit(View view) {
        finish();
        System.exit(0);
    }
}
