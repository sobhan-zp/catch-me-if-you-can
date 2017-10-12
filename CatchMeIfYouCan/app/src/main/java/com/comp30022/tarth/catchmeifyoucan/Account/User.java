package com.comp30022.tarth.catchmeifyoucan.Account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class User {

    String username;
    String email;
    String name;
    String status;
    String location;

    public String getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public static User parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        User user = gson.fromJson(response, User.class);
        return user;
    }

    @Override
    /*public String toString() {
        return "{" + username + ", " + email + ", " + name + "}";
    }*/
    public String toString() {
        return "{" + username + ", " + email + ", " + name + ", " + status + ", " + location +"}";
    }

}

