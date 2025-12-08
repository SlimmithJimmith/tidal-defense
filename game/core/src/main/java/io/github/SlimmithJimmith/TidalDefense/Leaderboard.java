/**
 * Leaderboard.java
 * Handles a json file for keeping track of high scores between application runs
 *
 * @author Team #2 - Brendan Boyko, Jimi Ruble, Mehdi Khazaal, James Watson
 * @version 1.0
 * Create Date: 11-20-2025
 */

package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class Leaderboard {
    private static final String PREFS_NAME = "tidal-defense";
    private static final String KEY_SCORES = "scores";

    private final Preferences prefs;
    private final Json json;

    public Leaderboard() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        json = new Json();
    }

    public Array<Scoreboard> loadAll() {
        String jsonText = prefs.getString(KEY_SCORES, "[]");
        Gdx.app.log("LB", "loaded json: " + jsonText);

        Array<Scoreboard> list = json.fromJson(Array.class, Scoreboard.class, jsonText);

        if (list == null) {
            list = new Array<>();
        }

        return list;

    }

    //Add score to leaderboard
    public void addScore(Scoreboard entry) {
        //load what has been saved
        Array<Scoreboard> list = loadAll();

        //Add a new row
        list.add(entry);

        //sort, tie-break is newer score
        list.sort(new java.util.Comparator<Scoreboard>() {
            @Override
            public int compare(Scoreboard a, Scoreboard b) {
                //higher score first
                if (a.score != b.score) {
                    return Integer.compare(b.score, a.score);
                }
                //tie-break: newer first
                return Long.compare(b.when, a.when);
            }
        });

        //Keep list to only top 10
        while (list.size > 10) {
            list.pop();
        }

        //Save it back to prefs
        String out = json.toJson(list);
        prefs.putString(KEY_SCORES, out);
        prefs.flush();
    }

}
