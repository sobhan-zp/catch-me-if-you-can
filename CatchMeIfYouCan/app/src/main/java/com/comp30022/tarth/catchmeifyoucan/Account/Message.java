package com.comp30022.tarth.catchmeifyoucan.Account;

import com.comp30022.tarth.catchmeifyoucan.Game.Participant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Message {

    String status;
    String type;
    Integer code;
    User[] users;
    Participant[] result;

    Integer action;
    String message;
    String from;

    Integer game_id;
    String name;
    String username;
    String email;
    String location;
    Integer id;
    Integer lv;

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }

    public Integer getId() {
        return id;
    }

    public Integer getLv() {
        return lv;
    }
  
    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public User[] getUsers() {
        return users;
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

    public Integer getGame_id() {
        return game_id;
    }

    public Participant[] getResult() {
        return result;
    }

    public static Message parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        Message message = gson.fromJson(response, Message.class);
        return message;
    }

    @Override
    public String toString() {
        String str =  "{"
                + ((status != null) ? status + ", " : "")
                + ((type != null) ? type + ", " : "")
                + ((code != null) ? code + ", " : "")
                + ((action != null) ? action + ", " : "")
                + ((name != null) ? name + ", " : "")
                + ((username != null) ? username + ", " : "")
                + ((email != null) ? email + ", " : "")
                + ((location != null) ? location + ", " : "")
                + ((id != null) ? id + ", " : "")
                + ((lv != null) ? lv + ", " : "")
                + ((message != null) ? message + ", " : "")
                + ((from != null) ? from + ", " : "");
        if (result != null) {
            for (Participant participant : result) {
                str += participant.toString();
            }
        }
        str += "}";
        return str;
    }

}

