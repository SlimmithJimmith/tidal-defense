/**
 * Fish.java
 * Basic enemy unit
 *
 * @author Team #2 - Brendan Boyko, Jimi Ruble, Mehdi Khazaal, James Watson
 * @version 1.0
 * Create Date: 09-27-2025
 */

package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class OctoBoss extends Enemy {

    //A constant variable that defines how many points an OctoBoss is worth when killed.
    //Static because it is one value for each fish. Final because it never changes.
    private static final int POINTS = 500;

    public OctoBoss(Vector2 position) {
        super(position);
        Texture img_OctoBoss = EnemyManager.img_OctoBoss;
        this.enemy_sprite = new Sprite(img_OctoBoss);
        this.enemy_sprite.setSize(250,250); // Adjust to change size of fish
        this.health = 25;
    }

    //Tells compiler that we are implementing abstract method declared in Enemy.
    @Override
    int getPoints() {
        return POINTS; //Return this boss's points' value.
    }

}
