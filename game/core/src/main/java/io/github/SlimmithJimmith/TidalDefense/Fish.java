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

public class Fish extends Enemy {

    private Texture img_fish = new Texture("Fish.png");

    public Fish(Vector2 position) {
        super(position);
        this.enemy_sprite = new Sprite(img_fish);
        this.enemy_sprite.setSize(40,40); // Adjust to change size of fish
        this.health = 1;
    }

}
