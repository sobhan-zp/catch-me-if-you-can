package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.R;

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
    private static final Integer FRIEND_CHECK = 509;            // Friend check request
    private static final Integer FRIEND_CHECK_FAIL = 510;       // Friend check failure
    private static final Integer FRIEND_CHECK_SUCCESS = 511;    // Friend check success
    private static final String SERVER_IP = "35.197.172.195"; // CentOS 6 Server
    //public static final String SERVER_IP = "45.77.49.3";    // CentOS 7 Server

    private Button buttonBack;
    private ListView listView;
    private TextView textViewEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        String[] projection = {
                //name
                //ID
                //username
                //location
                //status
        };

        Cursor c = getContentResolver().query(
                /*EntryProvider.CONTENT_URI*/null,
                projection,
                null,
                null,
                /*EntryProvider.NAME*/null
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

        listView = (ListView)findViewById(android.R.id.list);
        textViewEmpty = (TextView)findViewById(android.R.id.empty);

        listView.setAdapter(adapter);
        listView.setEmptyView(textViewEmpty);

        listView.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUser();
            }
        });

        buttonBack = (Button) findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        listView.setEmptyView(textViewEmpty);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    // Adds a new friend
    private void addFriend() {

    }

    // Obtains friend details from the server
    private void getFriend() {

    }

    // Searches for an existing friend
    private void searchFriend() {

    }

    // Checks if a friend is online
    private void checkFriend() {

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
