package com.comp30022.tarth.catchmeifyoucan.UI;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.Account.Communication;
import com.comp30022.tarth.catchmeifyoucan.Account.Message;
import com.comp30022.tarth.catchmeifyoucan.Chat.ChatMessage;
import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements Communication {

    private static final Integer MESSAGE_SEND = 600;
    private static final Integer MESSAGE_RECEIVE = 601;
    private static final Integer MESSAGE_SEND_SUCCESS_ONLINE = 602;
    private static final Integer MESSAGE_SEND_SUCCESS_OFFLINE = 603;
    private static final Integer MESSAGE_SEND_FAIL = 604;
    private static final Integer MESSAGE_OFFLINE_GET = 605;
    private static final Integer MESSAGE_COMMAND_SEND = 606;
    private static final Integer MESSAGE_COMMAND_SUCCESS = 607;
    private static final Integer MESSAGE_COMMAND_FAIL = 608;
    private static final Integer MESSAGE_COMMAND_RECEIVE = 609;
    private static final Integer MESSAGE_READ = 1;
    private static final Integer MESSAGE_UNREAD = 0;

    private ArrayAdapter<String> adapter;
    private List<String> array;

    private String name = "admin";
    private String friend = "vikram";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        LoginActivity.getClient().setmCurrentActivity(this);

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

        //getOfflineMessages();
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
        System.out.println("MESSAGE : " + message.getMessage());
        if (message.getAction() != null) {
            if (message.getAction().equals(MESSAGE_RECEIVE)) {
                System.out.println("Message received");
                displayMessage(message);
            } else if (message.getAction().equals(MESSAGE_OFFLINE_GET)) {
                System.out.println("Message received");
                displayMessage(message);
            } else {
                System.out.println("Error: Unknown response received");
            }
        }
        if (message.getCode() != null) {
             if (message.getCode().equals(MESSAGE_SEND_SUCCESS_ONLINE)) {
                 System.out.println("Message sent, user online");
             } else if (message.getCode().equals(MESSAGE_SEND_SUCCESS_OFFLINE)) {
                 System.out.println("Message sent, user offline");
             }
        }
    }

    private void getOfflineMessages() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", MESSAGE_OFFLINE_GET);
        } catch(Exception e) {
            e.printStackTrace();
        }
        LoginActivity.getClient().send(obj.toString());
    }

    private void displayMessage(Message message) {

        //TextView textViewItem = (TextView) findViewById(R.id.item);
        //TextView textViewText = (TextView) findViewById(R.id.textViewText);
        //TextView textViewUser = (TextView) findViewById(R.id.textViewUser);
        //TextView textViewTime = (TextView) findViewById(R.id.textViewTime);

        //textViewItem.setText(message.getMessage());
        //textViewText.setText(message.getMessage());
        //textViewUser.setText(message.getFrom());
        //textViewTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", message.getTime()));

        array.add(
                message.getFrom() + ": " +  message.getMessage() + "\n" + message.getTime()
        );
        adapter.notifyDataSetChanged();

    }

    private void sendMessage() {
        EditText input = (EditText) findViewById(R.id.input);

        // Read input and push a new instance of ChatMessage to the database
        ChatMessage msg = new ChatMessage(input.getText().toString(), friend);
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", MESSAGE_SEND);
            obj.put("username", msg.getUser());
            obj.put("message", msg.getText());
        } catch(Exception e) {
            e.printStackTrace();
        }
        LoginActivity.getClient().send(obj.toString());

        array.add(
                name + ": " +  msg.getText() + "\n" + "0"
        );
        adapter.notifyDataSetChanged();

        // Clear input
        input.setText("");
    }

    private void back() {
        finish();
    }
}

