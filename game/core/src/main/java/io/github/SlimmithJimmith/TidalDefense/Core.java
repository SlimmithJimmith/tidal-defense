package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture img_lifeguard;
    private Texture img_bullet;
    private Texture img_enemy;
    Lifeguard lifeguard; //Instantiate lifeguard object
    Enemy[] enemies; //Instantiate enemy object
    private int numWidth_enemies = 11; //Sets width of enemy
    private int numHeight_enemies = 5; //Sets height of enemy
    private int spacing_enemies = 40; //Keeps the enemies from being on top of one another

    @Override
    public void create() {
        batch = new SpriteBatch();
        img_lifeguard = new Texture("LifeguardShootingUp.png"); //loads lifeguad image
        img_bullet = new Texture("Bullet.png"); //loads bullet image
        img_enemy = new Texture("Fish.png");
        lifeguard = new Lifeguard(img_lifeguard, img_bullet, Color.BLUE); //creates lifeguard + bullet, bullet color is blue
        enemies = new Enemy[numWidth_enemies * numHeight_enemies]; //creates enemies based on height and width params
        //give space from top of screen for enemy
        float topMargin = 40f;
        //start position of enemies for x and y coordinates to center
        float startX = Gdx.graphics.getWidth()/2f - (numWidth_enemies/2f) * spacing_enemies;
        float startY = Gdx.graphics.getHeight() - topMargin - (numHeight_enemies - 1) * spacing_enemies;
        int i = 0;
        //Nested for loop to generate enemies
        for(int y = 0; y < numHeight_enemies; y++){
            for(int x = 0; x < numWidth_enemies; x++){
                //position & create the enemies
                Vector2 position = new Vector2(startX + x * spacing_enemies,
                    startY + (numHeight_enemies - 1 - y) * spacing_enemies);
                enemies[i] = new Enemy(position, img_enemy);
                i++;
            }
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        lifeguard.Draw(batch);
        for (int i = 0; i < enemies.length; i++) {
            //Check to see if bullet overlaps with enemy. If so, enemy "dies" (gets deleted from screen).
            //Must check if they are alive first, or bullet will stop at first level.
            if(enemies[i].alive){
                if(lifeguard.bullet_sprite.getBoundingRectangle().overlaps(enemies[i].enemy_sprite.getBoundingRectangle())){
                    lifeguard.position_bullet.y = 100000;
                    enemies[i].alive = false;
                    break;
                }
            }
        }
        for (int i = 0; i < enemies.length; i++) {
            if(enemies[i].alive){
                enemies[i].Draw(batch);
            }
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img_lifeguard.dispose();
    }
}

