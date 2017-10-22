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
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.Server.Communication;
import com.comp30022.tarth.catchmeifyoucan.Server.Message;
import com.comp30022.tarth.catchmeifyoucan.R;
import com.comp30022.tarth.catchmeifyoucan.Server.WebSocketClient;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * ChatActivity.java
 * Chatting with friends
 */
public class ChatActivity extends Activity implements Communication {

    private ArrayAdapter<String> adapter;
    private List<String> array;

    private String friend;

    SimpleDateFormat dateFormat;
    TextView textViewName;

    /**
     * Called when the activity is starting
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        WebSocketClient.getClient().setActivity(this);

        // Get receiver info
        setFriend();

        // Set date format
        dateFormat = new SimpleDateFormat("HH:mm:ss, dd/MM/yy");

        textViewName = (TextView) findViewById(R.id.Name);
        textViewName.setText(friend);

        Button fab = (Button) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        ListView listViewMessages = (ListView) findViewById(R.id.list_of_messages);
        array = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                this,
                R.layout.list_one_item,
                array
        );
        listViewMessages.setAdapter(adapter);

        getOfflineMessages();
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
                    if (message.getAction().equals(getResources().getInteger(R.integer.MESSAGE_RECEIVE))) {
                        System.out.println("Message received");
                        displayMessage(message);
                    } else if (message.getAction().equals(getResources().getInteger(R.integer.MESSAGE_OFFLINE_GET))) {
                        System.out.println("Message received");
                        displayMessage(message);
                    } else {
                        System.out.println("Chat Error: Unknown response received");
                    }
                }
                if (message.getCode() != null) {
                    if (message.getCode().equals(getResources().getInteger(R.integer.MESSAGE_SEND_SUCCESS_ONLINE))) {
                        System.out.println("Message sent, user online");
                    } else if (message.getCode().equals(getResources().getInteger(R.integer.MESSAGE_SEND_SUCCESS_OFFLINE))) {
                        System.out.println("Message sent, user offline");
                    }
                }
            }
        });
    }

    /**
     * Retrieves recipient username from previous activity
     */
    public void setFriend() {
        // Get username from dashboard
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null) {
            friend = (String) bd.get("friend");
        }
    }

    /**
     * Retrieves offline messages from the server
     */
    private void getOfflineMessages() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", getResources().getInteger(R.integer.MESSAGE_OFFLINE_GET));
            obj.put("username", friend);
        } catch(Exception e) {
            e.printStackTrace();
        }
        WebSocketClient.getClient().send(obj.toString());
    }

    /**
     * Displays a new message
     * @param message : Message to be displayed
     */
    private void displayMessage(Message message) {
        array.add(message.getFrom() + ": " +  message.getMessage() + "\n" + dateFormat);
        adapter.notifyDataSetChanged();
    }

    /**
     * Sends a message to the recipient
     */
    private void sendMessage() {
        EditText input = (EditText) findViewById(R.id.input);
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", getResources().getInteger(R.integer.MESSAGE_SEND));
            obj.put("username", friend);
            obj.put("message", input.getText().toString());
        } catch(Exception e) {
            e.printStackTrace();
        }
        WebSocketClient.getClient().send(obj.toString());

        array.add("You: " +  input.getText().toString() + "\n"  + dateFormat);
        adapter.notifyDataSetChanged();

        // Clear input
        input.setText("");
    }

}
