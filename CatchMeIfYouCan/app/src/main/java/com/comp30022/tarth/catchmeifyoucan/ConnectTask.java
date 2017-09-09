package com.comp30022.tarth.catchmeifyoucan;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/*
 * AsyncTask for connecting to the server and receiving responses
 */
public class ConnectTask extends AsyncTask<String, String, Client> {

    @Override
    protected Client doInBackground(String... message) {
        // Create a Client object
        Client mClient = new Client(new Client.OnMessageReceived() {
            @Override
            // messageReceived method is implemented
            public void messageReceived(String message) {
                // Calls the onProgressUpdate
                publishProgress(message);
            }
        });
        mClient.run();
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        // Response received from server
        Log.d("Test", "Response" + values[0]);
        // Process server response here
        // TODO

        // response.setText(response.getText() + "/n" +values[0]);
    }

}
