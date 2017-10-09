package com.comp30022.tarth.catchmeifyoucan.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.comp30022.tarth.catchmeifyoucan.Account.Communication;
import com.comp30022.tarth.catchmeifyoucan.Account.Message;
import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;

public class AddActivity extends AppCompatActivity implements Communication {

    private static final Integer FRIEND_ADD = 506;            // Friend add request
    private static final Integer FRIEND_ADD_FAIL = 507;       // Friend add failure
    private static final Integer FRIEND_ADD_SUCCESS = 508;    // Friend add success

    Button buttonAdd;
    EditText editTextAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        LoginActivity.getClient().setmCurrentActivity(this);

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        editTextAdd = (EditText) findViewById(R.id.editTextAdd);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend(editTextAdd.getText().toString());
                editTextAdd.setText("");
            }
        });
    }

    // Adds a new friend
    private void addFriend(String username) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", FRIEND_ADD);
            obj.put("username", username);
        } catch(Exception e) {
            e.printStackTrace();
        }
        LoginActivity.getClient().send(obj.toString());
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
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
        if (message.getCode().equals(FRIEND_ADD_SUCCESS)) {
            toast("Friend add success");
            onBackPressed();
        } else if (message.getCode().equals(FRIEND_ADD_FAIL)) {
            toast("Friend add failure");
        }
    }

    // Displays a toast message
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

