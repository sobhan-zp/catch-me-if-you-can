package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.comp30022.tarth.catchmeifyoucan.Account.Communication;
import com.comp30022.tarth.catchmeifyoucan.Account.Message;
import com.comp30022.tarth.catchmeifyoucan.R;

public class DashboardActivity extends AppCompatActivity implements Communication {

    private Button buttonChat;
    private Button buttonLogout;
    private Button buttonFriendlist;
    private Button buttonSettings;
    private Button buttonJoin;
    private Button buttonCreate;
    private ImageView imageViewTest;
    private String getName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // constructors
        buttonChat = (Button) findViewById(R.id.buttonChat);
        buttonCreate = (Button) findViewById(R.id.buttonCreate);
        buttonJoin = (Button) findViewById(R.id.buttonJoin);
        buttonFriendlist = (Button) findViewById(R.id.buttonFriendlist);
        imageViewTest = (ImageView) findViewById(R.id.imageViewTest);
        buttonFriendlist = (Button) findViewById(R.id.buttonFriendlist);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonSettings = (Button) findViewById(R.id.buttonSettings);


        TextView textViewUsername = (TextView) findViewById(R.id.Username);
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null) {
            getName = (String) bd.get("username");
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
                openGame();
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
        buttonSettings.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings();
            }
        });
        buttonLogout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutWarning();
            }
        });

        // set a onclick listener for when the button gets clicked
        imageViewTest.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                openUser(getName);
            }
        });
    }

    // Disables back button -- you nee dto click logout to exit
    @Override
    public void onBackPressed() {
        logoutWarning();
    }

    // Reveals pop up asking if user really wants to exit
    public void logoutWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle("Exiting Login...");
        builder.setMessage("Are you sure you want to logout? If you are in any game you will leave the game and may end up never finding your friend.");
        builder.setPositiveButton("Get Me Out of Here!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                logout();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // Disconnects from server and returns to main menu
    public void logout() {
        LoginActivity.getClient().disconnect();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Navigates to Settings
    public void settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    // Navigates to User Activity
    private void openUser(String username) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("username", username);
        // tell userActivity that you're coming from the dashboard so it doesn't load send message button
        intent.putExtra("dashboard", true);
        startActivity(intent);
    }

    @Override
    public void response(final Message message) {
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
    private void openGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

}

