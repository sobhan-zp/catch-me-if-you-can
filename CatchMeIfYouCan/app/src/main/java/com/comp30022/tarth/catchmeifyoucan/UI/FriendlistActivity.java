package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.Account.Communication;
import com.comp30022.tarth.catchmeifyoucan.Account.Message;
import com.comp30022.tarth.catchmeifyoucan.Account.User;
import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class FriendlistActivity extends AppCompatActivity implements Communication {

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

    private Button buttonAdd;
    private Button buttonBack;
    private Button buttonCheck;
    private Button buttonGet;
    private Button buttonSearch;
    private ListView listView;
    private TextView textViewEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);
        LoginActivity.getClient().setmCurrentActivity(this);

        // Enable Internet permissions
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonCheck = (Button) findViewById(R.id.buttonCheck);
        buttonGet = (Button) findViewById(R.id.buttonGet);
        buttonSearch = (Button) findViewById(R.id.buttonSearch);
        listView = (ListView)findViewById(android.R.id.list);
        textViewEmpty = (TextView)findViewById(android.R.id.empty);

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
                searchFriend();
            }
        });

        buttonBack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    // Adds a new friend
    private void addFriend() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", FRIEND_ADD);
            obj.put("username", "1");
        } catch(Exception e) {
            e.printStackTrace();
        }
        LoginActivity.getClient().send(obj.toString());
    }

    // Checks if a friend is online
    private void checkFriend() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", FRIEND_CHECK);
            obj.put("username", "1");
        } catch(Exception e) {
            e.printStackTrace();
        }
        LoginActivity.getClient().send(obj.toString());
    }

    // Obtains a list of all friends from the server
    private void getFriend() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", FRIEND_GET);
        } catch(Exception e) {
            e.printStackTrace();
        }
        LoginActivity.getClient().send(obj.toString());
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
        LoginActivity.getClient().send(obj.toString());
    }

    private void verify(Message message) {

        System.out.println("CODE:" + Integer.toString(message.getCode()));
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

            ArrayList<String> items = new ArrayList<String>();
            User[] users = message.getResult();

            for (User user : users) {
                items.add(user.getUsername());
            }
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                    this,
                    R.layout.list_one_item,
                    items
            );

            listView.setAdapter(adapter2);
            listView.setEmptyView(textViewEmpty);

            listView.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openUser();
                }
            });
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

    @Override
    public void response(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                verify(message);
            }
        });
    }
}
