package com.comp30022.tarth.catchmeifyoucan.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.R;

public class FriendlistActivity extends AppCompatActivity {

    private static final Integer FRIEND_GET = 500;            // Friend get request
    private static final Integer FRIEND_GET_FAIL = 501;       // Friend get failure
    private static final Integer FRIEND_GET_SUCCESS = 502;    // Friend get success
    private static final Integer FRIEND_SEARCH = 503;         // Friend search request
    private static final Integer FRIEND_SEARCH_FAIL = 504;    // Friend search failure
    private static final Integer FRIEND_SEARCH_SUCCESS = 505; // Friend search success
    private static final String SERVER_IP = "35.197.172.195"; // CentOS 6 Server
    //public static final String SERVER_IP = "45.77.49.3";    // CentOS 7 Server

    private Button buttonBack;
    private ListView listView;
    private TextView textViewEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        listView = (ListView)findViewById(android.R.id.list);
        textViewEmpty = (TextView)findViewById(android.R.id.empty);
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

    private void back() {
        finish();
    }
}
