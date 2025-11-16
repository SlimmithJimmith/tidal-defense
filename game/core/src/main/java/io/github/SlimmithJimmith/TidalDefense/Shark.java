package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Shark extends Enemy {

    //Defines how many points a shark kill will be worth.
    //Final because this does not change, and it will be the same for every shark.
    private static final int POINTS = 150;

    public Shark(Vector2 position) {
        super(position);
        Texture img_Shark = new Texture("Shark.png");
        this.enemy_sprite = new Sprite(img_Shark);
        this.enemy_sprite.setSize(100,75); // Adjust to change size of shark
        this.health = 3;
    }

    // Tells compiler that we are implementing abstract method declared in Enemy.
    @Override
    int getPoints(){
        return POINTS; //Return static point value given for POINTS in shark class.
    }

}
