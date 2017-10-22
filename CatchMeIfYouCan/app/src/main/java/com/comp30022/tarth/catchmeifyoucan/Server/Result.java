// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

package com.comp30022.tarth.catchmeifyoucan.Server;

/**
 * Result.java
 * Maps first level nested JSON objects received from the server
 */
public class Result {

    private Double x;
    private Double y;

    private Integer id;
    private Integer account_id;
    private Integer game_id;
    private Integer is_owner;

    private String email;
    private String info;
    private String name;
    private String status;
    private String username;
    private String location;

    public Double getX() {
        return x;
    }
    public Double getY() {
        return y;
    }

    public Integer getId() {
        return id;
    }
    public Integer getAccount_id() {
        return account_id;
    }
    public Integer getGame_id() {
        return game_id;
    }
    public Integer getIs_owner() {
        return is_owner;
    }

    public String getEmail() {
        return email;
    }
    public String getInfo() {
        return info;
    }
    public String getName() {
        return name;
    }
    public String getStatus() {
        return status;
    }
    public String getUsername() {
        return username;
    }
    public String getLocation() {
        return location;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setAccount_id(Integer account_id) {
        this.account_id = account_id;
    }

    public void setGame_id(Integer game_id) {
        this.game_id = game_id;
    }

    public void setIs_owner(Integer is_owner) {
        this.is_owner = is_owner;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * String representation of class object
     * @return
     */
    @Override
    public String toString() {
        return (
                "{"
                        + ((x != null) ? "x: " + Double.toString(x) : "")
                        + ((y != null) ? ", y: " + Double.toString(y) : "")
                        + ((id != null) ? ", id: " + Integer.toString(id) : "")
                        + ((account_id != null) ? ", account_id: " + Integer.toString(account_id) : "")
                        + ((game_id != null) ? ", game_id: " + Integer.toString(game_id) : "")
                        + ((is_owner != null) ? ", is_owner: " + Integer.toString(is_owner) : "")
                        + ((email != null) ? ", email: " + email : "")
                        + ((info != null) ? ", info: " + info : "")
                        + ((name != null) ? ", name: " + name : "")
                        + ((location != null) ? ", location: " + location : "")
                        + ((status != null) ? ", status: " + status : "")
                        + ((username != null) ? ", username: " + username : "")
                        + "}"
        );
    }

}
