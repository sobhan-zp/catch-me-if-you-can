package com.comp30022.tarth.catchmeifyoucan.Game;

public class Participant {

    private Integer account_id;
    private Integer game_id;
    private Integer is_owner;

    public Integer getAccount_id() {
        return account_id;
    }

    public Integer getGame_id() {
        return game_id;
    }

    public Integer getIs_owner() {
        return is_owner;
    }

    @Override
    public String toString() {
        return "{" + Integer.toString(account_id) + ", " + Integer.toString(game_id) + ", " + Integer.toString(is_owner) + "}";
    }

}
