package com.comp30022.tarth.catchmeifyoucan.Server;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/*
 * TCP TCPClient
 */
public class TCPClient {

    private static final String SERVER_IP = "35.197.172.195";
    private static final Integer SERVER_PORT = 80;

    private String mServerMessage;
    private OnMessageReceived mMessageListener = null;
    private Boolean mRun = false;
    private PrintWriter mBufferOut;
    private BufferedReader mBufferIn;

    public TCPClient(OnMessageReceived listener) {
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
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            Log.e("TCP TCPClient", "C: Connecting...");
            // Creates a socket to open a connection
            Socket socket = new Socket(serverAddr, SERVER_PORT);
            try {
                // Sends messages to the server
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                // Received messages sent back by the server
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // Listens for the messages sent by the server
                while (mRun) {
                    mServerMessage = mBufferIn.readLine();

                    /* WRONG PLACE TO SEND MESSAGES */
                    String mMessage = "{\"username\": \"vikramgk\", \"password\": \"cellotape\", \"client_ip\": 1234, \"email\": \"nigerian_prince@student.unimelb.edu.au\", \"name\": \"Nigerian Price\", \"date_of_birth\": 0}";
                    sendMessage(mMessage);

                    if (mServerMessage != null && mMessageListener != null) {
                        // Call the method messageReceived from activity
                        mMessageListener.messageReceived(mServerMessage);
                    }
                    mServerMessage = null;
                }
                Log.e("Response", "S: Received: '" + mServerMessage + "'");
            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            } finally {
                close();
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
        void messageReceived(String message);
    }

}
