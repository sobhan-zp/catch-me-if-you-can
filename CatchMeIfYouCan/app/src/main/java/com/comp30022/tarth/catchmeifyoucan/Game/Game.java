package com.comp30022.tarth.catchmeifyoucan.Game;

public class Game {

    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "{"
                + "game_id : " +  Integer.toString(id)
                + ", name : " + name
                + "}";
    }

}
