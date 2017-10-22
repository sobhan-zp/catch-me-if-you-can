// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

package com.comp30022.tarth.catchmeifyoucan.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.comp30022.tarth.catchmeifyoucan.Server.Communication;
import com.comp30022.tarth.catchmeifyoucan.Server.Message;
import com.comp30022.tarth.catchmeifyoucan.Server.Result;
import com.comp30022.tarth.catchmeifyoucan.R;
import com.comp30022.tarth.catchmeifyoucan.Server.WebSocketClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * GamelistActivity.java
 * List of open games
 */
public class GamelistActivity extends AppCompatActivity implements Communication {

    private ArrayAdapter<String> adapter;
    private List<String> array;

    /**
     * Called when the activity is starting
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamelist);
        WebSocketClient.getClient().setActivity(this);

        // Add back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Enable Internet permissions
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final ListView listViewGames = (ListView) findViewById(R.id.listViewGames);
        array = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                this,
                R.layout.list_one_item_friends,
                array
        );
        listViewGames.setAdapter(adapter);

        listViewGames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(listViewGames.getItemAtPosition(position).toString());
                joinGame(Integer.parseInt(listViewGames.getItemAtPosition(position).toString()));
            }
        });

        // Obtains the list of friends from the server upon incovation
        getGames();
    }

    /**
     * This hook is called whenever an item in your options menu is selected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the activity has detected the user's press of the back key
     */
    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
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
        getGames();
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
                    toast("Game resume get success");
                    joinGame(message.getResult()[0].getGame_id());
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_GET_CURRENT_FAIL))) {
                    toast("Game resume get failure");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_GET_SUCCESS))) {
                    toast("Game get success");

                    // Repopulates list
                    Result[] results = message.getResult();
                    array.clear();
                    for (Result result : results) {
                        array.add(
                                Integer.toString(result.getId())
                        );
                    }
                    adapter.notifyDataSetChanged();

                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_GET_FAIL))) {
                    toast("Game get failure");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_ADD_SUCCESS))) {
                    toast("Successfully joined game");
                    if (message.getIs_owner().equals(1)) {
                        openTarget(message);
                    } else {
                        openSearcher(message);
                    }
                } else if (message.getCode().equals(getResources().getInteger(R.integer.GAME_ADD_FAIL))) {
                    toast("Failed to join game");
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
     * Obtains a list of all games from the server
     */
    private void getGames() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", getResources().getInteger(R.integer.GAME_GET));
        } catch(Exception e) {
            e.printStackTrace();
        }
        WebSocketClient.getClient().send(obj.toString());
    }

    /**
     * Attempts to join a game
     * @param game_id : ID of the selected game
     */
    private void joinGame(Integer game_id) {
        //{"action":703, "id":1}
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", getResources().getInteger(R.integer.GAME_ADD));
            obj.put("id", game_id);
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println(obj.toString());
        WebSocketClient.getClient().send(obj.toString());
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

}
