/**
 * Scoreboard.java
 * For use as a data type with the Leaderboard
 *
 * @author Team #2 - Brendan Boyko, Jimi Ruble, Mehdi Khazaal, James Watson
 * @version 1.0
 * Create Date: 11-20-2025
 */

package io.github.SlimmithJimmith.TidalDefense;

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
