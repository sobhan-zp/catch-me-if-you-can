// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

package com.comp30022.tarth.catchmeifyoucan.UI;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

/**
 * DashboardActivity.java
 * Application main interface
 */
public class DashboardActivity extends Activity implements Communication {

    private String getName;

    /**
     * Called when the activity is starting
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        WebSocketClient.getClient().setActivity(this);

        Button buttonCreate = (Button) findViewById(R.id.buttonCreate);
        Button buttonJoin = (Button) findViewById(R.id.buttonJoin);
        Button buttonFriendlist = (Button) findViewById(R.id.buttonFriendlist);
        Button buttonLogout = (Button) findViewById(R.id.buttonLogout);
        Button buttonSettings = (Button) findViewById(R.id.buttonSettings);
        ImageView imageViewTest = (ImageView) findViewById(R.id.imageViewTest);
        TextView textViewUsername = (TextView) findViewById(R.id.Username);

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null) {
            getName = (String) bd.get("username");
            String username = "@" + getName;
            textViewUsername.setText(username);
        }

        buttonCreate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("action", getResources().getInteger(R.integer.GAME_CREATE));
                    obj.put("name", "game");
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
        imageViewTest.setImageResource(R.mipmap.temp_placeholder);
        imageViewTest.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                openUser(getName);
            }
        });
    }

    /**
     * Called when the activity has detected the user's press of the back key
     */
    @Override
    public void onBackPressed() {
        logoutWarning();
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                WebSocketClient.getClient().setActivity(this);
            }
        }
    }

    /**
     * Method invoked when the WebSocketClient receives a message
     * @param message : Message received from server
     */
    @Override
    public void onResponse(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message.getAction() != null) {
                    toast("New message from " + message.getFrom() + ": " + message.getMessage());
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_GET_CURRENT_SUCCESS))) {
                    toast("Rejoined existing game");
                    if (message.getIs_owner().equals(1)) {
                        System.out.println("GAME NAME: " + message.getName());
                        openTarget(message);
                    } else {
                        System.out.println("GAME NAME: " + message.getName());
                        openSearcher(message);
                    }
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_GET_CURRENT_FAIL))) {
                    toast("No active games");
                    openGamelist();
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_CREATE_SUCCESS))) {
                    toast("Game creation successful");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_CREATE_FAIL))) {
                    toast("Game creation failed");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_ADD_SUCCESS))) {
                    toast("You have been added to the game");
                    if (message.getIs_owner().equals(1)) {
                        openTarget(message);
                    } else {
                        openSearcher(message);
                    }
                } else {
                    toast("Error: Unknown response received");
                }
            }
        });
    }

    /**
     * Displays a toast message
     * @param message : Message to be displayed
     */
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Reveals pop up asking if user really wants to exit
     */
    public void logoutWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle(getResources().getString(R.string.exit_dashboard_title));
        builder.setMessage(getResources().getString(R.string.exit_dashboard_message));
        builder.setPositiveButton(getResources().getString(R.string.exit_dialog_pos), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                logout();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.exit_dialog_neg), null);
        builder.show();
    }

    /**
     * Disconnects from server and returns to main menu
     */
    public void logout() {
        WebSocketClient.getClient().disconnect();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    /**
     * Navigates to Settings Activity
     */
    public void settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 1);
    }

    /**
     * Navigates to User Activity
     * @param username : Username of user to be loaded
     */
    private void openUser(String username) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("username", username);
        // tell userActivity that you're coming from the dashboard so it doesn't load send message button
        intent.putExtra("dashboard", true);
        startActivityForResult(intent, 1);
    }

    /**
     * Navigates to Target Activity
     * @param message : Message received from the server
     */
    private void openTarget(Message message) {
        Intent intent = new Intent(this, TargetActivity.class);
        intent.putExtra("game_id", message.getGame_id());
        startActivityForResult(intent, 1);
    }

    /**
     * Navigates to Searcher Activity
     * @param message : Message received from the server
     */
    private void openSearcher(Message message) {
        Intent intent = new Intent(this, SearcherActivity.class);
        intent.putExtra("game_id", message.getGame_id());
        startActivityForResult(intent, 1);
    }

    /**
     * Navigates to Gamelist Activity
     */
    private void openGamelist() {
        Intent intent = new Intent(this, GamelistActivity.class);
        startActivityForResult(intent, 1);
    }

    /**
     * Navigates to Friendlist Activity
     */
    private void openFriendlist() {
        Intent intent = new Intent(this, FriendlistActivity.class);
        startActivityForResult(intent, 1);
    }

}
