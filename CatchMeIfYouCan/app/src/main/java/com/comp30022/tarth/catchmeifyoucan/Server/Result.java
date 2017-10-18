package com.comp30022.tarth.catchmeifyoucan.Server;

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
