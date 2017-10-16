package com.comp30022.tarth.catchmeifyoucan.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.comp30022.tarth.catchmeifyoucan.Account.Communication;
import com.comp30022.tarth.catchmeifyoucan.Account.Message;
import com.comp30022.tarth.catchmeifyoucan.Account.Result;
import com.comp30022.tarth.catchmeifyoucan.Game.Game;
import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GamelistActivity extends AppCompatActivity implements Communication {

    private static final Integer GAME_ADD = 703;
    private static final Integer GAME_ADD_SUCCESS = 704;
    private static final Integer GAME_ADD_FAIL = 705;

    private static final Integer GAME_GET = 709;
    private static final Integer GAME_GET_SUCCESS = 710;
    private static final Integer GAME_GET_FAIL = 711;

    private static final Integer GAME_GET_CURRENT = 718;
    private static final Integer GAME_GET_CURRENT_SUCCESS = 719;
    private static final Integer GAME_GET_CURRENT_FAIL = 720;

    private ArrayAdapter<String> adapter;
    private List<String> array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamelist);
        LoginActivity.getClient().setmCurrentActivity(this);

        // Enable Internet permissions
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final ListView listViewGames = (ListView) findViewById(R.id.listViewGames);
        array = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(
                this,
                R.layout.list_one_item,
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

    @Override
    public void onBackPressed() {
        finish();
    }

    // Resets the current activity connected to the WebSocket upon killing child activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                LoginActivity.getClient().setmCurrentActivity(this);
            }
        }
        getGames();
    }

    // Called by the WebSocket upon receiving a message
    @Override
    public void response(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                verify(message);
            }
        });
    }

    // Verifies responses from the server
    private void verify(Message message) {
        if (message.getCode().equals(GAME_GET_CURRENT_SUCCESS)) {
            toast("Game resume get success");
            joinGame(message.getResult()[0].getGame_id());
        } else if (message.getCode().equals(GAME_GET_CURRENT_FAIL)) {
            toast("Game resume get failure");
        } else if (message.getCode().equals(GAME_GET_SUCCESS)) {
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

        } else if (message.getCode().equals(GAME_GET_FAIL)) {
            toast("Game get failure");
        } else if (message.getCode().equals(GAME_ADD_SUCCESS)) {
            toast("Successfully joined game");
            openGame();
        } else if (message.getCode().equals(GAME_ADD_FAIL)) {
            toast("Failed to join game");
        } else {
            toast("Error: Unknown response received");
        }
    }

    // Obtains a list of all games from the server
    private void getGames() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", GAME_GET);
        } catch(Exception e) {
            e.printStackTrace();
        }
        LoginActivity.getClient().send(obj.toString());
    }

    // Attempts to join a game
    private void joinGame(Integer game_id) {
        //{"action":703, "id":1}
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", GAME_ADD);
            obj.put("id", game_id);
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println(obj.toString());
        LoginActivity.getClient().send(obj.toString());
    }

    // Navigates to Game Activity
    private void openGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    // Displays a toast message
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
