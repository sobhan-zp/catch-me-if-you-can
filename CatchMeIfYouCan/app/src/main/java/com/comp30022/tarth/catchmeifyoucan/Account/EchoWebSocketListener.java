package com.comp30022.tarth.catchmeifyoucan.Account;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by Xenzaki on 17/09/2017.
 */

public class EchoWebSocketListener extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private static final String SERVER_IP = "35.197.172.195"; // CentOS 6 Server
    //public static final String SERVER_IP = "45.77.49.3";    // CentOS 7 Server

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
        String cleanText = text.substring(text.indexOf('{'), text.indexOf('}') + 1).replace("\\", "");
        Gson gson = new GsonBuilder().create();
        try {
            Message message = gson.fromJson(cleanText, Message.class);
            System.out.println(message.getCode());
            response(message);
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

    public void response(final Message message) {
    }

}
