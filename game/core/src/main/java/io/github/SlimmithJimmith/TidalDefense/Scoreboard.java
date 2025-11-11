package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.utils.Array;

public class Scoreboard {
    public String name; //player initials / name
    public int score; //will store how many points earned
    public long when; //timestamp from System

    //No arg constructor
    public Scoreboard() {

    }

    public Scoreboard(String name, int score, long when){
        this.name = name;
        this.score = score;
        this.when = when;
    }


}
