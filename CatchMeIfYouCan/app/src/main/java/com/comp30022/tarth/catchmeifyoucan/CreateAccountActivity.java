package com.comp30022.tarth.catchmeifyoucan;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class CreateAccountActivity extends AppCompatActivity {
    
    public static final String SERVER_IP = "35.197.172.195"; // CentOS 6 Server
    //public static final String SERVER_IP = "45.77.49.3";   // CentOS 7 Server

    private Button buttonCreate;
    private OkHttpClient mClient;
    private WebSocket webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Enable Internet permissions
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mClient = new OkHttpClient();

        buttonCreate = (Button) findViewById(R.id.buttonCreate);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
                createAccount();
                openDashboard(v);
                disconnect();
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
        EchoWebSocketListener listener = new EchoWebSocketListener();
        webSocket = mClient.newWebSocket(request, listener);
    }

    // Closes the WebSocket connection with the server
    private void disconnect() {
        mClient.dispatcher().executorService().shutdown();
    }

    // Extracts user-entered information into a JSON formatted string to be sent
    private void createAccount() {
        TextView username = (TextView) findViewById(R.id.editTextName);
        TextView password = (TextView) findViewById(R.id.editTextPassword);
        TextView name = (TextView) findViewById(R.id.editTextName);
        TextView email = (TextView) findViewById(R.id.editTextEmail);
        TextView dob = (TextView) findViewById(R.id.editTextDOB);
        String client_ip = getHostIP();

        JSONObject obj = new JSONObject();
        try {
            obj.put("action", 100);
            obj.put("client_ip", client_ip);
            obj.put("username", username.getText());
            obj.put("password", password.getText());
            obj.put("name", name.getText());
            obj.put("email", email.getText());
            obj.put("date_of_birth", dob.getText());
        } catch(Exception e) {
            e.printStackTrace();
        }

        sendMessage(obj.toString());
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
    private void openDashboard(View v) {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }
}
