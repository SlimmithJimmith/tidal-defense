package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Lifeguard {
    public Vector2 position;
    public Sprite sprite;
    public float speed = 300;

    //Lifeguard image
    public Lifeguard(Texture img){
        sprite = new Sprite(img);
        position = new Vector2((float) Gdx.graphics.getWidth() /2,sprite.getScaleY()*sprite.getHeight()/2);
        sprite.setScale(2);
    }

    //Move lifeguard left and right
    public void Update(float deltaTime){
        if(Gdx.input.isKeyPressed(Input.Keys.A)) position.x-= deltaTime*speed; //Press "A", lifeguard moves LEFT
        if(Gdx.input.isKeyPressed(Input.Keys.D)) position.x+= deltaTime*speed; //Press "D", lifeguard moves RIGHT

        //Bounds checking to make sure lifeguard doesn't go off-screen to the left side of window
        if(position.x- (sprite.getWidth()*sprite.getScaleX()/2)<= 0) position.x = (sprite.getWidth()*sprite.getScaleX()/2);
        //Bounds checking to make sure lifeguard doesn't go off-screen to the right side of window.
        if(position.x+ (sprite.getWidth()*sprite.getScaleX()/2)>= Gdx.graphics.getWidth()) position.x = Gdx.graphics.getWidth()-(sprite.getWidth()*sprite.getScaleX()/2);
    }

    public void Draw(SpriteBatch batch){
        Update(Gdx.graphics.getDeltaTime());
        sprite.setPosition(position.x, position.y);
        sprite.draw(batch);
    }
}
