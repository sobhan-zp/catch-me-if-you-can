package com.comp30022.tarth.catchmeifyoucan.Account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketClient extends OkHttpClient {

    private static final String SERVER_IP = "35.197.172.195"; // CentOS 6 Server
    //public static final String SERVER_IP = "45.77.49.3";    // CentOS 7 Server
    private static final int NORMAL_CLOSURE_STATUS = 1000;

    private Communication mCurrentActivity;
    private OkHttpClient mClient;
    private WebSocket webSocket;
    private WebSocketListener listener;

    public void connect() {
        mClient = new OkHttpClient();
        Request request = new Request.Builder().url("ws://" + SERVER_IP).build();
        listener = new WebSocketListener() {
            /**
             * Invoked when a web socket has been accepted by the remote peer and may begin transmitting
             * messages.
             */
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
            }

            /** Invoked when a text (type {@code 0x1}) message has been received. */
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                String filteredText = text.substring(text.indexOf('{'), text.lastIndexOf('}') + 1).replace("\\", "");
                System.out.println("Server:" + filteredText);
                Gson gson = new GsonBuilder().create();
                try {
                    Message message = gson.fromJson(filteredText, Message.class);
                    mCurrentActivity.response(message);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            /** Invoked when a binary (type {@code 0x2}) message has been received. */
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
                t.printStackTrace();
            }
        };
        webSocket = mClient.newWebSocket(request, listener);
    }

    // Closes the WebSocket connection with the server
    public void disconnect() {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        mClient.dispatcher().executorService().shutdown();
    }

    // Sends a message to the WebSocket server
    public void send(String message) {
        webSocket.send(message);
    }

    // Updates the current running activity
    public void setmCurrentActivity(Communication activity) {
        mCurrentActivity = activity;
    }

    // Returns the current running activity
    public Communication getmCurrentActivity() {
        return mCurrentActivity;
    }

}

