package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
    public Vector2 position;
    public Vector2 position_initial;
    public Sprite enemy_sprite;
    public Boolean alive = true;
    public Enemy(Vector2 position, Texture img) {
        this.position = new Vector2(position);
        position_initial = new Vector2(position);
        enemy_sprite = new Sprite(img);
        enemy_sprite.setSize(32,32); //Adjust to change size of the lifeguard
    }

    public void Draw(SpriteBatch batch){
        float maxX = Gdx.graphics.getWidth() - enemy_sprite.getWidth();
        float maxY = Gdx.graphics.getHeight() - enemy_sprite.getHeight();

        //Stop enemy from going through left of screen
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
}
