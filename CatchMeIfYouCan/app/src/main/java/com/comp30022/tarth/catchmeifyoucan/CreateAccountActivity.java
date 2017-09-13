package com.comp30022.tarth.catchmeifyoucan;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import org.json.JSONObject;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class CreateAccountActivity extends AppCompatActivity {

//    Button buttonCreate;
//    Button buttonBack;
//    ConnectTask mConnectTask;

    // Example message sent to the server
//    String mMessage = "{\"username\": \"vikramgk\", \"password\": \"cellotape\", \"client_ip\": 1234, \"email\": \"nigerian_prince@student.unimelb.edu.au\", \"name\": \"Nigerian Price\", \"date_of_birth\": 0}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }


    public void createOnClick(View view) {

        TextView username = (TextView) findViewById(R.id.editTextName);
        TextView password = (TextView) findViewById(R.id.editTextPassword);
        String client_ip = getHostIP();
        TextView email = (TextView) findViewById(R.id.editTextEmail);
        //email.setText(getHostIP());



        JSONObject obj = new JSONObject();
        try {
            obj.put("username", username);
            obj.put("password",password);
            obj.put("client_ip",client_ip);
            obj.put("email",email);

        }catch(Exception ex)
        {
            ex.printStackTrace();
        }

        // TO DO
        // insert into database

        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    public String getHostIP() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
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
    public void onBackPressed() {
        finish();
    }

    public void back(View view) {
        finish();
    }
}
