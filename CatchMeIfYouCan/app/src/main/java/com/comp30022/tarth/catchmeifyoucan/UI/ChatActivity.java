package com.comp30022.tarth.catchmeifyoucan.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Date;
import java.util.List;

public class ChatActivity extends Activity implements Communication {

    private ArrayAdapter<String> adapter;
    private List<String> array;

    private String name = "You";
    private String friend;

    SimpleDateFormat dateFormat;
    TextView textViewName;

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
        array = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(
                this,
                R.layout.list_one_item,
                array
        );
        listViewMessages.setAdapter(adapter);

        getOfflineMessages();
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

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

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

    public void setFriend() {
        // Get username from dashboard
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null) {
            friend = (String) bd.get("friend");
        }
    }

    private void getOfflineMessages() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", getResources().getInteger(R.integer.MESSAGE_OFFLINE_GET));
        } catch(Exception e) {
            e.printStackTrace();
        }
        WebSocketClient.getClient().send(obj.toString());
    }

    private void displayMessage(Message message) {
        String time  = dateFormat.format(new Date());

        array.add(message.getFrom() + ": " +  message.getMessage() + "\n" + time);
        adapter.notifyDataSetChanged();
    }

    private void sendMessage() {
        EditText input = (EditText) findViewById(R.id.input);
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", getResources().getInteger(R.integer.MESSAGE_SEND));
            obj.put("username", input.getText().toString());
            obj.put("message", friend);
        } catch(Exception e) {
            e.printStackTrace();
        }
        WebSocketClient.getClient().send(obj.toString());

        String time  = dateFormat.format(new Date());
        array.add(name + ": " +  input.getText().toString() + "\n"  + time);
        adapter.notifyDataSetChanged();

        // Clear input
        input.setText("");
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

