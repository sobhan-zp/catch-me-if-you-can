package com.comp30022.tarth.catchmeifyoucan.UI;

import android.app.Activity;
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

import com.comp30022.tarth.catchmeifyoucan.Server.Communication;
import com.comp30022.tarth.catchmeifyoucan.Server.Message;
import com.comp30022.tarth.catchmeifyoucan.R;
import com.comp30022.tarth.catchmeifyoucan.Server.WebSocketClient;

import org.json.JSONObject;

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
        WebSocketClient.getClient().setActivity(this);

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
                JSONObject obj = new JSONObject();
                try {
                    obj.put("action", getResources().getInteger(R.integer.GAME_CREATE));
                } catch(Exception e) {
                    e.printStackTrace();
                }
                WebSocketClient.getClient().send(obj.toString());
            }
        });

        buttonJoin.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("action", getResources().getInteger(R.integer.GAME_GET_CURRENT));
                } catch(Exception e) {
                    e.printStackTrace();
                }
                WebSocketClient.getClient().send(obj.toString());
            }
        });
        buttonFriendlist.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFriendlist();
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

    // Disables back button -- you need to click logout to exit
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
        WebSocketClient.getClient().disconnect();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    // Navigates to Settings
    public void settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 1);
    }

    // Navigates to User Activity
    private void openUser(String username) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("username", username);
        // tell userActivity that you're coming from the dashboard so it doesn't load send message button
        intent.putExtra("dashboard", true);
        startActivityForResult(intent, 1);
    }

    @Override
    public void response(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message.getCode().equals(getResources().getInteger(R.integer.GAME_GET_CURRENT_SUCCESS))) {
                    if (message.getResult().length == 0) {
                        toast("No active games");
                        openGamelist();
                    } else {
                        toast("Rejoined existing game");
                        openGame(message);
                    }
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_GET_CURRENT_FAIL))) {
                    toast("Get current game failure");
                    openGamelist();
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_CREATE_SUCCESS))) {
                    toast("Game creation successful");
                    openGame(message);
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_CREATE_FAIL))) {
                    toast("Game creation failed");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_ADD_SUCCESS))) {
                    toast("You have been added to the game");
                    openGame(message);
                } else {
                    toast("Error: Unknown response received");
                }
            }
        });
    }

    // Displays a toast message
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Navigates to Chat activity
    private void openChat() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivityForResult(intent, 1);
    }

    // Navigates to Maps activity
    private void openMaps() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivityForResult(intent, 1);
    }

    // Navigates to User Activity
    private void openUser() {
        Intent intent = new Intent(this, UserActivity.class);
        startActivityForResult(intent, 1);
    }

    // Navigates to Game Activity
    private void openGame(Message message) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("game_id", message.getGame_id());
        startActivityForResult(intent, 1);
    }

    // Navigates to Gamelist Activity
    private void openGamelist() {
        Intent intent = new Intent(this, GamelistActivity.class);
        startActivityForResult(intent, 1);
    }

    // Navigates to Friendlist Activity
    private void openFriendlist() {
        Intent intent = new Intent(this, FriendlistActivity.class);
        startActivityForResult(intent, 1);
    }

    // Resets the current activity connected to the WebSocket upon terminating child activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                WebSocketClient.getClient().setActivity(this);
            }
        }
    }

}

