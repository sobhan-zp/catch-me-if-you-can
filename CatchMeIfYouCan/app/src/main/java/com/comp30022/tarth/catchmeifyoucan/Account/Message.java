package com.comp30022.tarth.catchmeifyoucan.Account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Message {

    String status;
    String type;
    Integer code;
    User[] result;

    Integer action;
    String message;
    String from;

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public User[] getResult() {
        return result;
    }

    public Integer getCode() {
        return code;
    }

    public Integer getAction() {
        return action;
    }

    public String getMessage() {
        return message;
    }

    public String getFrom() {
        return from;
    }

    public Long getTime() {
        return 0l;
    }

    public static Message parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        Message message = gson.fromJson(response, Message.class);
        return message;
    }

    @Override
    public String toString() {
        String str =  "{" + status + ", " + type + ", " + code + ", ";
        if (result != null) {
            for (User user : result) {
                str += user.toString();
            }
        }
        str += "}";
        return str;
    }

}

