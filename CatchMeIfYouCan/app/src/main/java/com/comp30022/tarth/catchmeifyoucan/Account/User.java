package com.comp30022.tarth.catchmeifyoucan.Account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class User {

    String username;
    String email;
    String name;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public static User[] parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        User[] users = gson.fromJson(response, User[].class);
        return users;
    }

    @Override
    public String toString() {
        return "{" + username + ", " + email + ", " + name + "}";
    }

}
