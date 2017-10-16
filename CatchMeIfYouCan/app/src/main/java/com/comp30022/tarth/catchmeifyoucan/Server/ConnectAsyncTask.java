package com.comp30022.tarth.catchmeifyoucan.Server;

import android.os.AsyncTask;
import android.util.Log;

/*
 * AsyncTask for connecting to the server and receiving responses
 */
public class ConnectAsyncTask extends AsyncTask<String, String, TCPClient> {

    @Override
    protected TCPClient doInBackground(String... message) {
        // Create a TCPClient object
        TCPClient mClient = new TCPClient(new TCPClient.OnMessageReceived() {
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
