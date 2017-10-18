package com.comp30022.tarth.catchmeifyoucan.Server;

import java.util.Date;

public class Message {

    private Integer action;
    private Integer code;
    private Integer game_id;
    private Integer id;
    private Integer is_owner;
    private Integer lv;

    private String email;
    private String from;
    private String location;
    private String message;
    private String name;
    private String status;
    private String type;
    private String username;

    private Result[] result;

    public Integer getAction() {
        return action;
    }

    public Integer getCode() {
        return code;
    }

    public Integer getGame_id() {
        return game_id;
    }

    public Integer getId() {
        return id;
    }

    public Integer getIs_owner() {
        return is_owner;
    }

    public Integer getLv() {
        return lv;
    }

    public Long getTime() {
        return new Date().getTime();
    }

    public String getEmail() {
        return email;
    }

    public String getFrom() {
        return from;
    }

    public String getLocation() {
        return location;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public Result[] getResult() {
        return result;
    }

    @Override
    public String toString() {
        return(
                "{"
                        + ((action != null) ? ", action: " + Integer.toString(action) : "")
                        + ((code != null) ? ", code: " + Integer.toString(code) : "")
                        + ((game_id != null) ? ", game_id: " + Integer.toString(game_id) : "")
                        + ((id != null) ? ", id: " + Integer.toString(id) : "")
                        + ((lv != null) ? ", lv: " + Integer.toString(lv) : "")
                        + ((email != null) ? ", email: " + email : "")
                        + ((from != null) ? ", from: " + from : "")
                        + ((location != null) ? ", location: " + location : "")
                        + ((message != null) ? ", message: " + message : "")
                        + ((name != null) ? ", name: " + name : "")
                        + ((status != null) ? ", status: " + status : "")
                        + ((type != null) ? ", type: " + type : "")
                        + ((username != null) ? ", username: " + username : "")
                        + ((result != null) ? ", result: " + resultToString() : "")
                        + "}"
        );
    }

    private String resultToString() {
        String str = "";
        for (Result res : result) {
            str += res.toString();
        }
        return str;
    }

}

