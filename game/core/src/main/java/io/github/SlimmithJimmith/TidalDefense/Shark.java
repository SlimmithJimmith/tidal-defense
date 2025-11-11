package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Shark extends Enemy {

    //Defines how many points a shark kill will be worth.
    //Final because this does not change, and it will be the same for every shark.
    private static final int POINTS = 150;

    private Texture img_sharkUp = new Texture("SharkUp.png");

    public Shark(Vector2 position) {
        super(position);
        this.enemy_sprite = new Sprite(img_sharkUp);
        this.enemy_sprite.setSize(60,40); // Adjust to change size of fish
        this.health = 3;
    }

    //Tells compiler that we are implementing abstract method declared in Enemy.
    @Override
    int getPoints(){
        return POINTS; //Return static point value given for POINTS in shark class.
    }

}
