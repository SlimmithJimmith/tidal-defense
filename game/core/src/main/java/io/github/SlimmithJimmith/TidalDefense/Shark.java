package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Shark extends Enemy {

    private Texture img_sharkUp = new Texture("SharkUp.png");

    public Shark(Vector2 position) {
        super(position);
        this.enemy_sprite = new Sprite(img_sharkUp);
        this.enemy_sprite.setSize(100,80); // Adjust to change size of fish
        this.health = 3;
    }

}
