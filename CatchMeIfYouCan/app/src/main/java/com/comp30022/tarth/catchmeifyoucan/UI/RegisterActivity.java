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
import android.widget.TextView;
import android.widget.Toast;

import com.comp30022.tarth.catchmeifyoucan.Server.Communication;
import com.comp30022.tarth.catchmeifyoucan.Server.Message;
import com.comp30022.tarth.catchmeifyoucan.Server.WebSocketClient;
import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements Communication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Enable Internet permissions
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Add back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        WebSocketClient.getClient().connect();
        WebSocketClient.getClient().setActivity(this);

        Button buttonCreate = (Button) findViewById(R.id.buttonRegister);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        TextView textViewSignedup = (TextView) findViewById(R.id.signedup);
        textViewSignedup.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                signedup();
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
            }
        }
    }

    @Override
    public void onResponse(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message.getCode().equals(getResources().getInteger(R.integer.REGISTER_SUCCESS))) {
                    toast("Register Success!");
                    finish();
                } else if (message.getCode().equals(getResources().getInteger(R.integer.REGISTER_FAIL))) {
                    toast("Register Failed: Please Try Again.");
                } else {
                    toast("Error: Unknown response received");
                }
            }
        });
    }

    // Get menu
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    // Redirect to login activity
    private void signedup() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    // Checks if email is valid
    boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Extracts user-entered information into a JSON formatted string to be sent
    private void register() {
        TextView username = (TextView) findViewById(R.id.editTextUsername);
        TextView password = (TextView) findViewById(R.id.editTextPassword);
        TextView name = (TextView) findViewById(R.id.editTextName);
        TextView email = (TextView) findViewById(R.id.editTextEmail);

        if (TextUtils.isEmpty(username.getText().toString())) {
            toast("Username field cannot be empty");
        } else if (TextUtils.isEmpty(password.getText().toString())) {
            toast("Password field cannot be empty");
        } else if (TextUtils.isEmpty(name.getText().toString())) {
            toast("Name field cannot be empty");
        } else if (!isValidEmail(email.getText().toString())) {
            toast("Email is not valid");
        } else {
            JSONObject obj = new JSONObject();
            try {
                obj.put("action", getResources().getInteger(R.integer.REGISTER_ACTION));
                obj.put("username", username.getText());
                obj.put("password", password.getText());
                obj.put("name", name.getText());
                obj.put("email", email.getText());
                obj.put("date_of_birth", "0");
            } catch (Exception e) {
                e.printStackTrace();
            }
            WebSocketClient.getClient().send(obj.toString());
        }
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
