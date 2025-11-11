/**
 * Enemy.java
 * Abstract class for different enemy types to inherit
 *
 * @author Team #2 - Brendan Boyko, Jimi Ruble, Mehdi Khazaal, James Watson
 * @version 1.0
 * Create Date: 09-27-2025
 */

package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

abstract class Enemy {
    public Vector2 position;
    public Vector2 position_initial;
    public Sprite enemy_sprite;
    public Boolean alive = true;
    public int health;

    public Enemy(Vector2 position) {
        this.position = new Vector2(position);
        position_initial = new Vector2(position);
    }

    public void Draw(SpriteBatch batch){
        float maxX = Gdx.graphics.getWidth() - enemy_sprite.getWidth();
        float maxY = Gdx.graphics.getHeight() - enemy_sprite.getHeight();

        // Bounds checking
        if(position.x < 0f){
            position.x = 0f;
        }
        else if(position.x > maxX){
            position.x = maxX;
        }

        if(position.y < 0f){
            position.y = 0f;
        }

        else if(position.y > maxY){
            position.y = maxY;
        }

        enemy_sprite.setPosition(position.x, position.y);
        enemy_sprite.draw(batch);
    }

    public void takeDamage() {
        this.health--;
    }

    abstract int getPoints();

    // I think we might need to dispose of enemy sprite here...? or in subclasses
}
