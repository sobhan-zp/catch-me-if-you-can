// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

package com.comp30022.tarth.catchmeifyoucan.Server;

import java.util.Date;

/**
 * Message.java
 * Maps outer level JSON objects received from the server
 */
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

    public void setAction(Integer action) {
        this.action = action;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setGame_id(Integer game_id) {
        this.game_id = game_id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setIs_owner(Integer is_owner) {
        this.is_owner = is_owner;
    }

    public void setLv(Integer lv) {
        this.lv = lv;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setResult(Result[] result) {
        this.result = result;
    }

    /**
     * String representation of class object
     * @return
     */
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

    /**
     * String representation of nested JSON objects in a JSON array
     * @return
     */
    private String resultToString() {
        String str = "";
        for (Result res : result) {
            str += res.toString();
        }
        return str;
    }

}
