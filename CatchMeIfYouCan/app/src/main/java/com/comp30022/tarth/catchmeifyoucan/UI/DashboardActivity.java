package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.Account.Communication;
import com.comp30022.tarth.catchmeifyoucan.Account.Message;
import com.comp30022.tarth.catchmeifyoucan.R;

public class DashboardActivity extends AppCompatActivity implements Communication {

    private Button buttonChat;
    private Button buttonFriendlist;
    private Button buttonSettings;
    private Button buttonJoinGame;
    private Button buttonGame;
    private ImageView imageViewTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // constructors
        buttonChat = (Button) findViewById(R.id.buttonChat);
        buttonJoinGame = (Button) findViewById(R.id.buttonJoinGame);
        buttonFriendlist = (Button) findViewById(R.id.buttonFriendlist);
        imageViewTest = (ImageView) findViewById(R.id.imageViewTest);
        buttonFriendlist = (Button) findViewById(R.id.buttonFriendlist);

        TextView TxtViewUsername = (TextView) findViewById(R.id.Username);
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null)
        {
            String getName = (String) bd.get("username");
            TxtViewUsername.setText("@" + getName);
        }

        buttonChat.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChat();
            }
        });

        buttonJoinGame.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMaps();
            }
        });
        buttonFriendlist.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                friends();
            }
        });

        // set a onclick listener for when the button gets clicked
        imageViewTest.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                userprofile();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void openProfile() {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    // Navigates to Friendslist Activity
    private void friends() {
        Intent intent = new Intent(this, FriendlistActivity.class);
        startActivity(intent);
    }

    // Navigates to User Activity
    private void userprofile() {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    @Override
    public void response(final Message message) {

    }

    // Navigates to Chat activity
    private void openChat() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

    // Navigates to Maps activity
    private void openMaps() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}

