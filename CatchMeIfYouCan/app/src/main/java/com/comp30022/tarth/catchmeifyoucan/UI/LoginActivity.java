package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.Account.EchoWebSocketListener;
import com.comp30022.tarth.catchmeifyoucan.Account.Message;
import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class LoginActivity extends AppCompatActivity {

    private static final Integer ACTION_LOGIN = 101;       // Register action
    private static final Integer LOGIN_SUCCESS_CODE = 200;
    private static final Integer LOGIN_USER_NON_EXIST_CODE = 201;
    private static final Integer LOGIN_EXIST_CODE = 202;
    private static final String SERVER_IP = "35.197.172.195"; // CentOS 6 Server
    //public static final String SERVER_IP = "45.77.49.3";    // CentOS 7 Server

    private Button buttonLogin;
    private Button buttonBack;

    private OkHttpClient mClient;
    private WebSocket webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Enable Internet permissions
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mClient = new OkHttpClient();
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonBack = (Button) findViewById(R.id.buttonBack);

        buttonLogin.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
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
                        TextView textView = (TextView)findViewById(R.id.textViewResponse);
                        textView.setText(message.toString());
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

        sendMessage(obj.toString());
    }

    private void verify(Message message) {
        if (message.getCode().equals(LOGIN_SUCCESS_CODE)) {
            System.out.println("Login success");
            openDashboard();
            disconnect();
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

    // Sends a message to the WebSocket server
    public void sendMessage(String message) {
        webSocket.send(message);
    }

    // Navigates to Dashboard Activity
    private void openDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    // Navigates to previous activity
    public void back() {
        finish();
    }
}
