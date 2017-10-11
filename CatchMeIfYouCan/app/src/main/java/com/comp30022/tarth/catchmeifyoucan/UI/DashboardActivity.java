package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.comp30022.tarth.catchmeifyoucan.Account.Communication;
import com.comp30022.tarth.catchmeifyoucan.Account.Message;
import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;

public class DashboardActivity extends AppCompatActivity implements Communication {

    private Button buttonChat;
    private Button buttonFriendlist;
    private Button buttonSettings;
    private Button buttonJoin;
    private Button buttonCreate;
    private ImageView imageViewTest;

    private static final Integer GAME_CREATE = 700;
    private static final Integer GAME_CREATE_SUCCESS = 701;
    private static final Integer GAME_CREATE_FAIL = 702;
    private static final Integer GAME_ADD_SUCCESS = 704;
    private static final Integer GAME_ADD_FAIL = 705;
    private static final Integer GAME_EXIT = 706;
    private static final Integer GAME_GET = 709;
    private static final Integer GAME_DELETE = 712;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        LoginActivity.getClient().setmCurrentActivity(this);

        // constructors
        buttonChat = (Button) findViewById(R.id.buttonChat);
        buttonCreate = (Button) findViewById(R.id.buttonCreate);
        buttonJoin = (Button) findViewById(R.id.buttonJoin);
        buttonFriendlist = (Button) findViewById(R.id.buttonFriendlist);
        imageViewTest = (ImageView) findViewById(R.id.imageViewTest);
        buttonFriendlist = (Button) findViewById(R.id.buttonFriendlist);

        TextView textViewUsername = (TextView) findViewById(R.id.Username);
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null) {
            String getName = (String) bd.get("username");
            textViewUsername.setText("@" + getName);
        }

        buttonChat.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChat();
            }
        });

        buttonCreate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("action", GAME_CREATE);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                LoginActivity.getClient().send(obj.toString());
            }
        });

        buttonJoin.setOnClickListener(new Button.OnClickListener() {
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
                openUser();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
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

    private void verify(Message message) {
        if (message.getCode().equals(GAME_CREATE_SUCCESS)) {
            toast("Game creation successful");
        } else if (message.getCode().equals(GAME_CREATE_FAIL)) {
            toast("Game creation failed");
            JSONObject obj = new JSONObject();
            try {
                obj.put("action", GAME_EXIT);
            } catch(Exception e) {
                e.printStackTrace();
            }
            LoginActivity.getClient().send(obj.toString());
        } else if (message.getCode().equals(GAME_ADD_SUCCESS)) {
            toast("You have been added to the game");
            openGame(message);
        } else {
            toast("Error: Unknown response received");
        }
    }

    // Displays a toast message
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

    // Navigates to Friendslist Activity
    private void friends() {
        Intent intent = new Intent(this, FriendlistActivity.class);
        startActivity(intent);
    }

    // Navigates to User Activity
    private void openUser() {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    // Navigates to Game Activity
    private void openGame(Message message) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("game_id", message.getGame_id());
        startActivity(intent);
    }
}

