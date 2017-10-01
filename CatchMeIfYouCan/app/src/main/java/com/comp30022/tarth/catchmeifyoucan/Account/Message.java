package com.comp30022.tarth.catchmeifyoucan.Account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Message {

    String status;
    String type;
    Integer code;

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public Integer getCode() {
        return code;
    }

    public static Message parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        Message message = gson.fromJson(response, Message.class);
        return message;
    }

}
