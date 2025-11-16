/**
 * Lifeguard.java
 * Creates and performs actions for the player's character
 *
 * @author Team #2 - Brendan Boyko, Jimi Ruble, Mehdi Khazaal, James Watson
 * @version 1.0
 * Create Date: 09-27-2025
 */

package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Lifeguard {
    public Vector2 position;
    public Vector2 position_bullet;
    public Sprite lifeguard_sprite;
    public Sprite bullet_sprite;
    private float lifeguardSpeed = 300;
    private float speed_bullet = 1500;
    private float gunAnchor_X = 0.52f; //0.0 is the left edge of sprite, 1.0 is right edge. This will be almost center
    private float gunAnchor_Y = 0.90f;//0.0 is the bottom of sprite, 1.0 is the top. This will make bullet come out top
    private final Sound shootSound;
    private float volume = 0.5f;
    private int powerUpTime = 0;

    public Lifeguard(Texture img, Texture img_bullet, Color color, Sound sound) {
        lifeguard_sprite = new Sprite(img); // Lifeguard sprite
        lifeguard_sprite.setSize(100,113); // Adjust to change size of the lifeguard
        float startX = Gdx.graphics.getWidth()/2f - lifeguard_sprite.getWidth()/2f; // Centers lifeguard on x-axis
        float startY = 10f; // Offsets the lifeguard, so it starts slightly above bottom of screen
        position = new Vector2(startX, startY); // Positions the lifeguard
        bullet_sprite = new Sprite(img_bullet); // Bullet sprite
        bullet_sprite.setSize(36,30); // Adjust to change size of the bullets
        bullet_sprite.setColor(color); // Ability to change color of bullet in main
        position_bullet = new Vector2(0,10000); // Position of the bullet
        this.shootSound = sound;
    }

    public void Update(float deltaTime){

        // Shoot a projectile if allowed with left mouse click or spacebar
        if((Gdx.input.isButtonJustPressed(0) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            && position_bullet.y >=Gdx.graphics.getHeight()){

            // Starts at top of lifeguard's current position
            float gunX = position.x + lifeguard_sprite.getWidth() * gunAnchor_X;
            float gunY = position.y + lifeguard_sprite.getHeight() * gunAnchor_Y;
            position_bullet.x = gunX - bullet_sprite.getWidth() / 2f;
            position_bullet.y = gunY;

            // Sound effects toggle
            if (shootSound != null) shootSound.play(volume);
        }

        // Press "A" or left arrow, lifeguard moves LEFT
        if(Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) position.x-= deltaTime * lifeguardSpeed;
        // Press "D" or right arrow, lifeguard moves RIGHT
        if(Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) position.x+= deltaTime * lifeguardSpeed;

        // Bounds checking to make sure lifeguard doesn't go off-screen to the left or right side of window
        float left = 0f; // Left edge of LibGDX screen is ALWAYS 0, regardless of screen size.
        float right = Gdx.graphics.getWidth() - lifeguard_sprite.getWidth(); // Width of the screen minus width of the lifeguard
        if(position.x < left) position.x = left;
        if(position.x > right) position.x = right;

        // Move the bullet up
        position_bullet.y += deltaTime * speed_bullet;
    }

    public void Draw(SpriteBatch batch){
        Update(Gdx.graphics.getDeltaTime());
        lifeguard_sprite.setPosition(position.x, position.y); // Sets position of the lifeguard position
        lifeguard_sprite.draw(batch); // Draws the lifeguard sprite
        bullet_sprite.setPosition(position_bullet.x, position_bullet.y); // Sets position of the bullet sprite
        bullet_sprite.draw(batch); // Draws the bullet sprite
    }

    // Power Up doubles speed and quadruples size of bullet
    // Sets 'timer' to decrease when each frame is rendered
    public void PowerUp() {
        this.speed_bullet *= 1.5F;
        this.bullet_sprite.setSize((float) (this.bullet_sprite.getWidth() * 1.25),(float) (this.bullet_sprite.getHeight() * 1.25));
        this.lifeguardSpeed += 50;
    }

    // Power Up Timer Decreases with each frame render
    // not used currently but keeping here in case I make a SUPER POWER-UP
    public int PowerUpTime() {
        if (this.powerUpTime > 0) {
            this.powerUpTime = powerUpTime - 1;
        }
        return this.powerUpTime;
    }

    // Reset after power up
    public void ResetBullet() {
        this.speed_bullet = 1000;
        this.bullet_sprite.setSize(24,20);
        this.lifeguardSpeed = 300;
    }
}
