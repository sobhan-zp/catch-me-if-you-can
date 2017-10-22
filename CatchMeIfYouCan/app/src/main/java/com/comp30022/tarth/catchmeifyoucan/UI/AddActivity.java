package com.comp30022.tarth.catchmeifyoucan.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.comp30022.tarth.catchmeifyoucan.Server.Communication;
import com.comp30022.tarth.catchmeifyoucan.Server.Message;
import com.comp30022.tarth.catchmeifyoucan.R;
import com.comp30022.tarth.catchmeifyoucan.Server.WebSocketClient;

import org.json.JSONObject;

public class AddActivity extends AppCompatActivity implements Communication {

    private EditText editTextAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        WebSocketClient.getClient().setActivity(this);

        // Add back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button buttonAdd = (Button) findViewById(R.id.buttonAdd);
        editTextAdd = (EditText) findViewById(R.id.editTextAdd);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend(editTextAdd.getText().toString());
                editTextAdd.setText("");
            }
        });
    }

    // Set back button on action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Returns to the previous activity
    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
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

    // Called by the WebSocket upon receiving a message
    @Override
    public void onResponse(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message.getAction() != null) {
                    toast("New message from " + message.getFrom() + ": " + message.getMessage());
                } else if (message.getCode().equals(getResources().getInteger(R.integer.FRIEND_ADD_SUCCESS))) {
                    toast("Friend add success");
                    onBackPressed();
                } else if (message.getCode().equals(getResources().getInteger(R.integer.FRIEND_ADD_FAIL))) {
                    toast("Friend add failure");
                }

            }
        });
    }

    // Adds a new friend
    private void addFriend(String username) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", getResources().getInteger(R.integer.FRIEND_ADD));
            obj.put("username", username);
        } catch(Exception e) {
            e.printStackTrace();
        }
        WebSocketClient.getClient().send(obj.toString());
    }

    // Displays a toast message
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
