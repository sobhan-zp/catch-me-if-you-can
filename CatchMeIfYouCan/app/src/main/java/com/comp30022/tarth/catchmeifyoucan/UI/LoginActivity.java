package com.comp30022.tarth.catchmeifyoucan.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AlignmentSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.comp30022.tarth.catchmeifyoucan.Server.Communication;
import com.comp30022.tarth.catchmeifyoucan.Server.Message;
import com.comp30022.tarth.catchmeifyoucan.Server.WebSocketClient;
import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements Communication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Enable Internet permissions
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Add back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialises the WebSocket client
        WebSocketClient.getClient().connect();
        WebSocketClient.getClient().setActivity(this);

        Button buttonLogin = (Button) findViewById(R.id.buttonLogin);
        TextView textViewRegister = (TextView) findViewById(R.id.registered);

        buttonLogin.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        textViewRegister.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    @Override
    public void onBackPressed() {
        WebSocketClient.getClient().disconnect();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
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

    // Resets the current activity connected to the WebSocket upon terminating child activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                WebSocketClient.getClient().setActivity(this);
                ((TextView) findViewById(R.id.editTextUsername)).setText("");
                ((TextView) findViewById(R.id.editTextPassword)).setText("");
            }
        }
        WebSocketClient.getClient().connect();
    }

    @Override
    public void onResponse(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message.getCode().equals(getResources().getInteger(R.integer.LOGIN_SUCCESS_CODE))) {
                    toast("Login Success!");
                    openDashboard();
                } else if (message.getCode().equals(getResources().getInteger(R.integer.LOGIN_EXIST_CODE))) {
                    toast("Login Failed: User is Logged in on Another Device");
                } else if (message.getCode().equals(getResources().getInteger(R.integer.LOGIN_USER_NON_EXIST_CODE))) {
                    toast("Login Failed: Username or Password is Incorrect");
                } else {
                    toast("Error: Unknown Response Received");
                }
            }
        });
    }

    // Get menu
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    // Redirect to register activity
    private void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, 1);
    }

    // Extracts user-entered information into a JSON formatted string to be sent
    private void login() {
        TextView username = (TextView) findViewById(R.id.editTextUsername);
        TextView password = (TextView) findViewById(R.id.editTextPassword);

        if (TextUtils.isEmpty(username.getText().toString())) {
            toast("Username field cannot be empty");
        } else if (TextUtils.isEmpty(password.getText().toString())) {
            toast("Password field cannot be empty");
        } else {
            JSONObject obj = new JSONObject();
            try {
                obj.put("action", getResources().getInteger(R.integer.LOGIN_ACTION));
                obj.put("username", username.getText());
                obj.put("password", password.getText());
            } catch(Exception e) {
                e.printStackTrace();
            }
            WebSocketClient.getClient().send(obj.toString());
        }
    }

    // Navigates to Dashboard Activity
    private void openDashboard() {
        EditText etName1 = (EditText) findViewById(R.id.editTextUsername);
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.putExtra("username", etName1.getText().toString());
        startActivityForResult(intent, 1);
    }

    // Displays a toast message
    private void toast(String text) {
        Spannable centeredText = new SpannableString(text);
        centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                0, text.length() - 1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        Toast.makeText(this, centeredText, Toast.LENGTH_SHORT).show();
    }

}
