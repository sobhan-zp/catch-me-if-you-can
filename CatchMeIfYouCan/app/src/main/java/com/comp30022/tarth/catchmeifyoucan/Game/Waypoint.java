package com.comp30022.tarth.catchmeifyoucan.Game;

public class Waypoint {

    private String info;
    private Double x;
    private Double y;

    public Waypoint(String info, Double x, Double y) {
        this.info = info;
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public String getInfo() {
        return info;
    }

}
