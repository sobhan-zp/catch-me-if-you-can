package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.Account.Communication;
import com.comp30022.tarth.catchmeifyoucan.Account.Message;
import com.comp30022.tarth.catchmeifyoucan.Account.WebSocketClient;
import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class LoginActivity extends AppCompatActivity implements Communication {

    private static final Integer ACTION_LOGIN = 101;              // Login action
    private static final Integer LOGIN_SUCCESS_CODE = 200;
    private static final Integer LOGIN_USER_NON_EXIST_CODE = 201;
    private static final Integer LOGIN_EXIST_CODE = 202;

    private Button buttonLogin;
    private Button buttonBack;

    public static WebSocketClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Enable Internet permissions
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Initialises the WebSocket client
        mClient = new WebSocketClient();
        mClient.connect();
        mClient.setmCurrentActivity(this);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonBack = (Button) findViewById(R.id.buttonBack);

        buttonLogin.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
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
        mClient.disconnect();
        finish();
    }

    // Extracts user-entered information into a JSON formatted string to be sent
    private void login() {
        TextView username = (TextView) findViewById(R.id.editTextUsername);
        TextView password = (TextView) findViewById(R.id.editTextPassword);
        String client_ip = getHostIP();

        JSONObject obj = new JSONObject();
        try {
            obj.put("action", ACTION_LOGIN);
            obj.put("client_ip", client_ip);
            obj.put("username", username.getText());
            obj.put("password", password.getText());
        } catch(Exception e) {
            e.printStackTrace();
        }
        mClient.send(obj.toString());
    }

    private void verify(Message message) {
        TextView textView = (TextView)findViewById(R.id.textViewResponse);
        textView.setText(message.toString());
        if (message.getCode().equals(LOGIN_SUCCESS_CODE)) {
            System.out.println("Login success");
            openDashboard();
        } else if (message.getCode().equals(LOGIN_EXIST_CODE)) {
            System.out.println("Login failed, user is logged in on another device");
        } else if (message.getCode().equals(LOGIN_USER_NON_EXIST_CODE)) {
            System.out.println("Login failed, username or password is incorrect");
        } else {
            System.out.println("Error: Unknown response received");
        }
    }

    // Obtains the IP Address of the host
    private String getHostIP() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue; // Skip iPv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostIp;
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

    // Navigates to Dashboard Activity
    private void openDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    // Navigates to previous activity
    public void back() {
        mClient.disconnect();
        finish();
    }

    public static WebSocketClient getClient() {
        return mClient;
    }
}

