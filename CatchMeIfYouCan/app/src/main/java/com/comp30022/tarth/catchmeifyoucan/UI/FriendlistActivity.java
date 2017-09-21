package com.comp30022.tarth.catchmeifyoucan.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.R;

public class FriendlistActivity extends AppCompatActivity {

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
                back(v);
            }
        });

        listView.setEmptyView(textViewEmpty);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void back(View v) {
        finish();
    }
}
