// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

package com.comp30022.tarth.catchmeifyoucan.Server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * WebSocketClient.java
 * Singleton class designed to send and receive responses to the WebSocket server
 */
public class WebSocketClient extends OkHttpClient {

    private static final WebSocketClient instance = new WebSocketClient();

    /**
     * Private constructor to avoid reinitializing
     */
    private WebSocketClient() {
    }

    public static WebSocketClient getClient() {
        return instance;
    }

    // CentOS 6 Server
    //private static final String SERVER2_IP = "ws://35.197.172.195";
    // CentOS 7 Server
    private static final String SERVER_IP = "ws://45.77.49.3";
    private static final int NORMAL_CLOSURE_STATUS = 1000;

    private Communication activity;
    private OkHttpClient client;
    private WebSocket webSocket;

    /**
     * Connects to the server
     */
    public void connect() {
        client = new OkHttpClient();
        Request request = new Request.Builder().url(SERVER_IP).build();
        WebSocketListener listener = new WebSocketListener() {
            /**
             * Invoked when a web socket has been accepted by the remote peer and may begin transmitting
             * messages.
             */
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
            }

            /**
             * Invoked when a text (type {@code 0x1}) message has been received.
             */
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                String filteredText = text.substring(text.indexOf('{'), text.lastIndexOf('}') + 1).replace("\\", "");
                System.out.println("Server:" + filteredText);
                Gson gson = new GsonBuilder().create();
                try {
                    Message message = gson.fromJson(filteredText, Message.class);
                    activity.onResponse(message);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            /**
             * Invoked when a binary (type {@code 0x2}) message has been received.
             */
            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
            }

            /**
             * Invoked when the remote peer has indicated that no more incoming messages will be
             * transmitted.
             */
            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(NORMAL_CLOSURE_STATUS, null);
            }

            /**
             * Invoked when both peers have indicated that no more messages will be transmitted and the
             * connection has been successfully released. No further calls to this listener will be made.
             */
            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
            }

            /**
             * Invoked when a web socket has been closed due to an error reading from or writing to the
             * network. Both outgoing and incoming messages may have been lost. No further calls to this
             * listener will be made.
             */
            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                System.out.println("Connection failed");
                t.printStackTrace();
            }
        };
        webSocket = client.newWebSocket(request, listener);
    }

    /**
     * Closes the WebSocket connection with the server
     */
    public void disconnect() {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        client.dispatcher().executorService().shutdown();
    }

    /**
     * Sends a message to the WebSocket server
     * @param message : Message to be sent
     */
    public void send(String message) {
        webSocket.send(message);
    }

    /**
     * Updates the current running activity
     * @param activity : Current running activity
     */
    public void setActivity(Communication activity) {
        this.activity = activity;
    }

    /**
     * Returns the current running activity
     * @return : Current running activity
     */
    public Communication getActivity() {
        return activity;
    }

}
