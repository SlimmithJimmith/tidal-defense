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
    private float speed = 300;
    private float speed_bullet = 1000;
    private float gunAnchor_X = 0.52f; //0.0 is the left edge of sprite, 1.0 is right edge. This will be almost center
    private float gunAnchor_Y = 0.90f;//0.0 is the bottom of sprite, 1.0 is the top. This will make bullet come out top
    private final Sound shootSound;

    public Lifeguard(Texture img, Texture img_bullet, Color color, Sound sound){
        lifeguard_sprite = new Sprite(img); //Lifeguard sprite
        lifeguard_sprite.setSize(64,64); //Adjust to change size of the lifeguard
        float startX = Gdx.graphics.getWidth()/2f - lifeguard_sprite.getWidth()/2f; //Centers lifeguard on x axis
        float startY = 6f; //Offsets the lifeguard so it starts slightly above bottom of screen
        position = new Vector2(startX, startY); //Positions the lifeguard
        bullet_sprite = new Sprite(img_bullet); //Bullet sprite
        bullet_sprite.setSize(20,16); //Adjust to change size of the bullets
        bullet_sprite.setColor(color); //ability to change color of bullet in main
        position_bullet = new Vector2(0,10000); //Position of the bullet
        this.shootSound = sound;
    }

    public void Update(float deltaTime){
        if(Gdx.input.isButtonJustPressed(0) && position_bullet.y >=Gdx.graphics.getHeight()){
            float gunX = position.x + lifeguard_sprite.getWidth() * gunAnchor_X;
            float gunY = position.y + lifeguard_sprite.getHeight() * gunAnchor_Y;

            position_bullet.x = gunX - bullet_sprite.getWidth() / 2f;
            position_bullet.y = gunY;

            if (shootSound != null) shootSound.play();
        }
        //Press "A", lifeguard moves LEFT
        if(Gdx.input.isKeyPressed(Input.Keys.A)) position.x-= deltaTime*speed;
        //Press "D", lifeguard moves RIGHT
        if(Gdx.input.isKeyPressed(Input.Keys.D)) position.x+= deltaTime*speed;

        //Bounds checking to make sure lifeguard doesn't go off-screen to the left side of window
        float left = 0f; //left edge of LibGDX screen is ALWAYS 0, regardless of screen size.

        //This just takes the width of the screen and subtracts how wide the lifeguard sprite is to
        //ensure the lifeguard stays within the right bounds in if block that follows.
        float right = Gdx.graphics.getWidth() - lifeguard_sprite.getWidth();

        //Bounds checking and stops lifeguard from exiting screen left
        if(position.x < left){
            position.x = left;
        }
        //Bounds checking and stops lifeguard from exiting screen right.
        if(position.x > right){
            position.x = right;
        }
        //move the bullet up
        position_bullet.y+= deltaTime*speed_bullet;
    }

    public void Draw(SpriteBatch batch){
        Update(Gdx.graphics.getDeltaTime());
        lifeguard_sprite.setPosition(position.x, position.y); //sets position of the lifeguard position
        lifeguard_sprite.draw(batch); //draws the lifeguard sprite
        bullet_sprite.setPosition(position_bullet.x, position_bullet.y); //sets position of the bullet sprite
        bullet_sprite.draw(batch); //draws the bullet sprite

    }
}
