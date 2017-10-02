package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.Account.EchoWebSocketListener;
import com.comp30022.tarth.catchmeifyoucan.Account.Message;
import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class FriendlistActivity extends AppCompatActivity {

    private static final Integer FRIEND_GET = 500;            // Friend get request
    private static final Integer FRIEND_GET_FAIL = 501;       // Friend get failure
    private static final Integer FRIEND_GET_SUCCESS = 502;    // Friend get success
    private static final Integer FRIEND_SEARCH = 503;         // Friend search request
    private static final Integer FRIEND_SEARCH_FAIL = 504;    // Friend search failure
    private static final Integer FRIEND_SEARCH_SUCCESS = 505; // Friend search success
    private static final Integer FRIEND_ADD = 506;            // Friend add request
    private static final Integer FRIEND_ADD_FAIL = 507;       // Friend add failure
    private static final Integer FRIEND_ADD_SUCCESS = 508;    // Friend add success
    private static final Integer FRIEND_CHECK = 509;          // Friend check request
    private static final Integer FRIEND_CHECK_FAIL = 510;     // Friend check failure
    private static final Integer FRIEND_CHECK_SUCCESS = 511;  // Friend check success
    private static final String SERVER_IP = "35.197.172.195"; // CentOS 6 Server
    //public static final String SERVER_IP = "45.77.49.3";    // CentOS 7 Server

    private Button buttonAdd;
    private Button buttonBack;
    private Button buttonCheck;
    private Button buttonGet;
    private Button buttonSearch;
    private ListView listView;
    private TextView textViewEmpty;

    private OkHttpClient mClient;
    private WebSocket webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        // Enable Internet permissions
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mClient = new OkHttpClient();

        /*
        String[] projection = {
                //name
                //ID
                //username
                //location
                //status
        };

        Cursor c = getContentResolver().query(
                //EntryProvider.CONTENT_URInull,
                projection,
                null,
                null,
                //EntryProvider.NAMEnull
        );

        ListAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.list_one_item,
                c,
                new String[] {
                        //....
                },
                new int[] {
                        R.id.item
                }
        );
        */

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonCheck = (Button) findViewById(R.id.buttonCheck);
        buttonGet = (Button) findViewById(R.id.buttonGet);
        buttonSearch = (Button) findViewById(R.id.buttonSearch);
        listView = (ListView)findViewById(android.R.id.list);
        textViewEmpty = (TextView)findViewById(android.R.id.empty);

        //listView.setAdapter(adapter);
        listView.setEmptyView(textViewEmpty);

        /*
        listView.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUser();
            }
        });
        */

        buttonAdd.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend();
            }
        });

        buttonCheck.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFriend();
            }
        });

        buttonGet.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFriend();
            }
        });

        buttonSearch.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend();
            }
        });

        buttonBack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        connect();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    // Opens a WebSocket connection with the server
    private void connect() {
        Request request = new Request.Builder().url("ws://" + SERVER_IP).build();
        EchoWebSocketListener listener = new EchoWebSocketListener() {
            // Receives response from the server
            @Override
            public void response(final Message message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        verify(message);
                    }
                });
            }
        };
        webSocket = mClient.newWebSocket(request, listener);
    }

    // Closes the WebSocket connection with the server
    private void disconnect() {
        mClient.dispatcher().executorService().shutdown();
    }

    // Sends a message to the WebSocket server
    public void sendMessage(String message) {
        webSocket.send(message);
    }

    // Adds a new friend
    private void addFriend() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", FRIEND_ADD);
            obj.put("username", "TEST");
        } catch(Exception e) {
            e.printStackTrace();
        }
        sendMessage(obj.toString());
    }

    // Checks if a friend is online
    private void checkFriend() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", FRIEND_CHECK);
            obj.put("username", "TEST");
        } catch(Exception e) {
            e.printStackTrace();
        }
        sendMessage(obj.toString());
    }

    // Obtains a list of all friends from the server
    private void getFriend() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", FRIEND_GET);
        } catch(Exception e) {
            e.printStackTrace();
        }
        sendMessage(obj.toString());
    }

    // Searches for the details of an existing user
    private void searchFriend() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", FRIEND_SEARCH);
            obj.put("username", "TEST");
        } catch(Exception e) {
            e.printStackTrace();
        }
        sendMessage(obj.toString());
    }

    private void verify(Message message) {
        if (message.getCode().equals(FRIEND_ADD_SUCCESS)) {
            System.out.println("Friend add success");
        } else if (message.getCode().equals(FRIEND_ADD_FAIL)) {
            System.out.println("Friend add failure");
        } else if (message.getCode().equals(FRIEND_CHECK_SUCCESS)) {
            System.out.println("Friend check success");
        } else if (message.getCode().equals(FRIEND_CHECK_FAIL)) {
            System.out.println("Friend check failure");
        } else if (message.getCode().equals(FRIEND_GET_SUCCESS)) {
            System.out.println("Friend get success");
        } else if (message.getCode().equals(FRIEND_GET_FAIL)) {
            System.out.println("Friend get failure");
        } else if (message.getCode().equals(FRIEND_SEARCH_SUCCESS)) {
            System.out.println("Friend search success");
        } else if (message.getCode().equals(FRIEND_SEARCH_FAIL)) {
            System.out.println("Friend search failure");
        } else {
            System.out.println("Error: Unknown response received");
        }
    }

    // Navigates to User Activity
    private void openUser() {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    // Navigates to previous activity
    private void back() {
        finish();
    }
}
