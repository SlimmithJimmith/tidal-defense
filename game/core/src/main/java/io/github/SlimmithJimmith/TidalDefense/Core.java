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
    private int minX_enemies;
    private int minY_enemies;
    private int maxX_enemies;
    private int maxY_enemies;
    private int direction_enemies = 1;
    private float speed_enemies = 200;
    //Offset to move the enemies
    private Vector2 offset_enemies;

    @Override
    public void create() {
        offset_enemies = new Vector2(0,0);
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

    int amount_alive_enemies = 0;
    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        //Moves the enemies
        offset_enemies.x+= direction_enemies * deltaTime * speed_enemies;
        //This block makes the enemies move
        for (int i = 0; i < enemies.length; i++) {
            enemies[i].position.set(
                enemies[i].position_initial.x + offset_enemies.x,
                enemies[i].position_initial.y + offset_enemies.y
            );
        }
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
        //Variables for enemy movement
        amount_alive_enemies = 0;
        boolean seeded = false;
        for(int i = 0; i < enemies.length; i++){
            if(enemies[i].alive){
                int indexX = i % numWidth_enemies; //Columns
                int indexY = i / numWidth_enemies; //Rows

                if (!seeded) {
                    // seed min/max with the first alive enemy
                    minX_enemies = maxX_enemies = minY_enemies = maxY_enemies = i;
                    seeded = true;
                } else {
                    if(indexX > maxX_enemies) maxX_enemies = i;
                    if(indexX < minX_enemies) minX_enemies = i;
                    if(indexY > maxY_enemies) maxY_enemies = i;
                    if(indexY < minY_enemies) minY_enemies = i;
                }

                amount_alive_enemies++;
            }
        }
        //Regenerates enemies when all are destroyed
        if(amount_alive_enemies == 0){
            for(int i = 0; i < enemies.length; i++){
                enemies[i].alive = true;
            }
            offset_enemies = new Vector2(0,0);
            batch.end();
            return;
        }
        //Bounds checking to ensure enemies do not exit the RIGHT side of the screen
        float rightLimit = Gdx.graphics.getWidth() - enemies[0].enemy_sprite.getWidth();
        if (enemies[maxX_enemies].position.x >= rightLimit) {
            direction_enemies = -1;
            //Makes the enemies go down towards the lifeguard
            offset_enemies.y-= enemies[0].enemy_sprite.getHeight() * enemies[0].enemy_sprite.getScaleY()*0.25f;
            //Makes the speed of the enemies increase over time.
            speed_enemies+= 0.1f;
        }
        //Bounds checking to ensure enemies do not exit the LEFT side of the screen
        if(enemies[minX_enemies].position.x <= 0f){
            direction_enemies = 1;
            //Makes the enemies go down towards the lifeguard
            offset_enemies.y-= enemies[0].enemy_sprite.getHeight() * enemies[0].enemy_sprite.getScaleY()*0.25f;
            //Makes the speed of the enemies increase over time.
            speed_enemies+= 0.1f;
        }
        //If enemies go past bottom of the screen, exit the app.
        //We need to update this so that it shows "game over" and score achived. If score > highscore, update highscore.
        if(enemies[minY_enemies].position.y <= 0){
            Gdx.app.exit();
        }

        for (int i = 0; i < enemies.length; i++) {
            if (enemies[i].alive) {
                enemies[i].Draw(batch);
                //App closes (game ends) when enemy touches the lifeguard. Fix to show "game over" and score achieved.
                //If score > highscore, update highscore.
                if (enemies[i].enemy_sprite.getBoundingRectangle().overlaps(lifeguard.lifeguard_sprite.getBoundingRectangle())) {
                    Gdx.app.exit();
                }
            }
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        //Dispose of sprites
        img_lifeguard.dispose();
        img_bullet.dispose();
        img_enemy.dispose();
    }
}

