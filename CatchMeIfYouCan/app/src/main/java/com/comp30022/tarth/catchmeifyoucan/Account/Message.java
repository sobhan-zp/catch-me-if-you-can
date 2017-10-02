package com.comp30022.tarth.catchmeifyoucan.Account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Message {

    String status;
    String type;
    String result;
    Integer code;
    User[] users;

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getResult() {
        return result;
    }

    public Integer getCode() {
        return code;
    }

    public User[] getUsers() {
        return users;
    }

    public static Message parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        Message message = gson.fromJson(response, Message.class);
        return message;
    }

    @Override
    public String toString() {
        String str =  "{" + status + ", " + type + ", " + result + ", " + code + ", ";
        if (users != null) {
            for (User user : users) {
                str += user.toString();
            }
        }
        str += "}";
        return str;
    }

}
