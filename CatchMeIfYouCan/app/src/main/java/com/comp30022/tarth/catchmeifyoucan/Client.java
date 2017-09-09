package com.comp30022.tarth.catchmeifyoucan;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/*
 * TCP Client
 */
public class Client {

    public static final String IP = "35.197.172.195";
    public static final Integer PORT = 80;

    private String mServerMessage;
    private OnMessageReceived mMessageListener = null;
    private Boolean mRun = false;
    private PrintWriter mBufferOut;
    private BufferedReader mBufferIn;

    public Client(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    /*
     * Sends a message to the server
     * @param : Message text
     */
    public void sendMessage(String message) {
        if (mBufferOut != null && !mBufferOut.checkError()) {
            mBufferOut.println(message);
            mBufferOut.flush();
        }
    }

    /*
     * Opens a connection to the server
     */
    public void run() {
        mRun = true;
        try {
            InetAddress serverAddr = InetAddress.getByName(IP);
            Log.e("TCP Client", "C: Connecting...");
            // Creates a socket to open a connection
            Socket socket = new Socket(serverAddr, PORT);
            try {
                // Sends messages to the server
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                // Received messages sent back by the server
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // Listens for the messages sent by the server
                while (mRun) {
                    mServerMessage = mBufferIn.readLine();
                    if (mServerMessage != null && mMessageListener != null) {
                        // Call the method messageReceived from activity
                        mMessageListener.messageReceived(mServerMessage);
                    }
                }
                Log.e("Response", "S: Received: '" + mServerMessage + "'");
            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            } finally {
                // Socket must be closed
                socket.close();
            }
        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
        }
    }

    /*
     * Closes the connection with the server
     */
    public void close() {
        mRun = false;
        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }
        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }

    /*
     * Listens for messages received from the server
     * The method messageReceived(String) must be implemented in the activity
     */
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

}
