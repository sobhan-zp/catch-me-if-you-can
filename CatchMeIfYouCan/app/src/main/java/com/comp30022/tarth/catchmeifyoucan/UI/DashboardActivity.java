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

import com.comp30022.tarth.catchmeifyoucan.Account.Communication;
import com.comp30022.tarth.catchmeifyoucan.Account.Message;
import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;

public class DashboardActivity extends Activity implements Communication {

    private Button buttonLogout;
    private Button buttonFriendlist;
    private Button buttonSettings;
    private Button buttonJoin;
    private Button buttonCreate;
    private ImageView imageViewTest;
    private String getName;

    //private Button buttonTest;

    private static final Integer GAME_CREATE = 700;
    private static final Integer GAME_CREATE_SUCCESS = 701;
    private static final Integer GAME_CREATE_FAIL = 702;
    private static final Integer GAME_ADD_SUCCESS = 704;
    private static final Integer GAME_GET_CURRENT = 718;
    private static final Integer GAME_GET_CURRENT_SUCCESS = 719;
    private static final Integer GAME_GET_CURRENT_FAIL = 720;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        LoginActivity.getClient().setmCurrentActivity(this);

        // constructors
        buttonCreate = (Button) findViewById(R.id.buttonCreate);
        buttonJoin = (Button) findViewById(R.id.buttonJoin);
        buttonFriendlist = (Button) findViewById(R.id.buttonFriendlist);
        imageViewTest = (ImageView) findViewById(R.id.imageViewTest);
        buttonFriendlist = (Button) findViewById(R.id.buttonFriendlist);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonSettings = (Button) findViewById(R.id.buttonSettings);

        //buttonTest = (Button) findViewById(R.id.buttonTest);

        TextView textViewUsername = (TextView) findViewById(R.id.Username);
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null) {
            getName = (String) bd.get("username");
            textViewUsername.setText("@" + getName);
        }

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
                JSONObject obj = new JSONObject();
                try {
                    obj.put("action", GAME_GET_CURRENT);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                LoginActivity.getClient().send(obj.toString());
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

        /*buttonTest.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });*/

        // set a onclick listener for when the button gets clicked
        imageViewTest.setImageResource(R.mipmap.temp_placeholder);
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

    // Navigates to Settings
    public void test() {
        Intent intent = new Intent(this, UserDetailActivity.class);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                verify(message);
            }
        });
    }

    private void verify(Message message) {
        if (message.getCode().equals(GAME_GET_CURRENT_SUCCESS)) {
            if (message.getResult().length == 0) {
                toast("No active games");
                openGamelist();
            } else {
                toast("Rejoined existing game");
                openGame(message);
            }
        } else if (message.getCode().equals(GAME_GET_CURRENT_FAIL)) {
            toast("Get current game failure");
            openGamelist();
        } else if (message.getCode().equals(GAME_CREATE_SUCCESS)) {
            toast("Game creation successful");
            openGame(message);
        } else if (message.getCode().equals(GAME_CREATE_FAIL)) {
            toast("Game creation failed");
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


    // Navigates to Maps activity
    private void openMaps() {
        Intent intent = new Intent(this, MapsActivity.class);
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

    // Navigates to Gamelist Activity
    private void openGamelist() {
        Intent intent = new Intent(this, GamelistActivity.class);
        startActivity(intent);
    }

    // Navigates to Friendlist Activity
    private void openFriendlist() {
        Intent intent = new Intent(this, FriendlistActivity.class);
        startActivity(intent);
    }

}

