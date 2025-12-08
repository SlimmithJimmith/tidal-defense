/**
 * PowerUp.java
 * Class mostly used for controlling sprite image and movement.
 * Power Up modifications happen within the Lifeguard class
 *
 * @author Team #2 - Brendan Boyko, Jimi Ruble, Mehdi Khazaal, James Watson
 * @version 1.0
 * Create Date: 11-13-2025
 */

package io.github.SlimmithJimmith.TidalDefense.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class PowerUp {
    public Vector2 position;
    public Sprite powerUp_sprite;
    private final Texture img_PowerUp = new Texture("PowerUp.png");

    // Start position of power up for x and y coordinates to center
    float startX = Gdx.graphics.getWidth()/2f - ((float) Math.random() * 100);
    float startY = Gdx.graphics.getHeight() - 60;
    float nextY = startY;

    public PowerUp() {
        this.position = new Vector2(startX, startY);

        this.powerUp_sprite = new Sprite(img_PowerUp);
        this.powerUp_sprite.setSize(50,60); // Adjust to change size of powerUp
    }

    // Moves the power up
    public void updatePowerUpPosition() {
        nextY = nextY - 2;
        this.position.set(startX, nextY);
    }

    public void Draw(SpriteBatch batch){
        float maxX = Gdx.graphics.getWidth() - powerUp_sprite.getWidth();
        float maxY = Gdx.graphics.getHeight() - powerUp_sprite.getHeight();

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

        powerUp_sprite.setPosition(position.x, position.y);
        powerUp_sprite.draw(batch);
    }

    // Check if player picked up power-up
    public boolean playerCollision(Lifeguard lifeguard) {
        return this.powerUp_sprite.getBoundingRectangle().overlaps(lifeguard.lifeguard_sprite.getBoundingRectangle());
    }

    // Check if power-up was missed
    public boolean powerUpPastPlayer() {
        return this.position.y <= 0;
    }

    // get ridda it
    public void dispose() {
        img_PowerUp.dispose();
    }

}
