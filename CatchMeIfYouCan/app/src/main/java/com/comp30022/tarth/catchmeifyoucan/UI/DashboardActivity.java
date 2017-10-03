package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.comp30022.tarth.catchmeifyoucan.Account.Communication;
import com.comp30022.tarth.catchmeifyoucan.Account.Message;
import com.comp30022.tarth.catchmeifyoucan.R;

public class DashboardActivity extends AppCompatActivity implements Communication {

    private Button buttonBack;
    private Button buttonFriendlist;
    private ImageView imageViewTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        //LoginActivity.getClient().setmCurrentActivity(this);

        buttonBack = (Button) findViewById(R.id.buttonBack);
        imageViewTest = (ImageView) findViewById(R.id.imageViewTest);

        buttonBack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        imageViewTest.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewTest.setImageResource(R.drawable.doge_angry);
            }
        });

        buttonFriendlist = (Button) findViewById(R.id.buttonFriendlist);
        buttonFriendlist.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFriendlist();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void openFriendlist() {
        Intent intent = new Intent(this, FriendlistActivity.class);
        startActivity(intent);
    }

    private void back() {
        finish();
    }

    @Override
    public void response(final Message message) {

    }
}
