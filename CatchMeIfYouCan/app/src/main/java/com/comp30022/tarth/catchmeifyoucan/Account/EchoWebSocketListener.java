package com.comp30022.tarth.catchmeifyoucan.Account;

import android.util.Log;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by Xenzaki on 17/09/2017.
 */

public class EchoWebSocketListener extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS = 1000;

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.i("Websocket", "onOpen");
        //webSocket.send(Build.MANUFACTURER + " " + Build.MODEL);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.i("Websocket", "onMessage: " + text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Log.i("Websocket", "onMessage: " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String s) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        Log.i("Websocket", "onClosing: " + s);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.i("Websocket", "onFailure: " + t.getMessage());
    }
}
