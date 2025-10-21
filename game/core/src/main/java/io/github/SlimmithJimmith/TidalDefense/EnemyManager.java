package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class EnemyManager {

    // Enemy Setup
    Enemy[] enemies; //Instantiate enemy object

    // Amount, speed and direction variables
    private int numWidth_enemies = 3; // Sets width of enemy formation
    private int numHeight_enemies = 2; //Sets height of enemy formation
    private int spacing_enemies = 40; // Keeps the enemies from being on top of one another
    private int minX_enemies, minY_enemies, maxX_enemies, maxY_enemies; // Bounds checking
    private int direction_enemies = 1; // Toggles for left/right movement
    private float speed_enemies = 200;
    private Vector2 offset_enemies; // Offset to move the enemies

    private int amount_alive_enemies;

    // Enemy images
    private Texture img_enemy1;

    public EnemyManager(Texture enemy1) {
        this.img_enemy1 = enemy1;
        offset_enemies = new Vector2(0,0);
    }

    // Enemy formation creation
    public void createFormation(int level) {
        offset_enemies = new Vector2(0, 0);

        // Calculate enemy matrix size here, randomize...?
        numWidth_enemies = numWidth_enemies + (level - 1);
        numHeight_enemies = numHeight_enemies + (level - 1);

        // Creates enemy array with length based on height and width params
        enemies = new Enemy[numWidth_enemies * numHeight_enemies];

        // Give space from top of screen for enemy
        float topMargin = 40f;

        // Start position of enemies for x and y coordinates to center
        float startX = Gdx.graphics.getWidth()/2f - (numWidth_enemies/2f) * spacing_enemies;
        float startY = Gdx.graphics.getHeight() - topMargin - (numHeight_enemies - 1) * spacing_enemies;

        int i = 0;
        // Nested for loop to generate enemies
        for(int y = 0; y < (numHeight_enemies); y++){
            for(int x = 0; x < (numWidth_enemies); x++){

                //position & create the enemies
                Vector2 position = new Vector2(startX + x * spacing_enemies,
                    startY + (numHeight_enemies - 1 - y) * spacing_enemies);
                enemies[i] = new Enemy(position, img_enemy1);
                enemies[i].alive = true;
                i++;
            }
        }

    }

    // Detect enemy collision with player
    public boolean playerCollision(Lifeguard lifeguard) {
        for (int i = 0; i < enemies.length; i++) {
            if (enemies[i].enemy_sprite.getBoundingRectangle().overlaps(lifeguard.lifeguard_sprite.getBoundingRectangle()))
                return true;
        }
        return false;
    }

    // Detect enemies below player
    public boolean enemyPastPlayer() {
        return enemies[minY_enemies].position.y <= 0;
    }

    // For drawing the remaining enemies
    public void drawBatch(SpriteBatch batch) {
        for (int i = 0; i < enemies.length; i++) {
            if (enemies[i].alive) {
                enemies[i].Draw(batch);
            }
        }
    }

    // Find edges of current formation
    public void updateEnemyBounds() {
        //Variables for enemy movement
        amount_alive_enemies = 0;
        boolean seeded = false;
        for (int i = 0; i < enemies.length; i++) {
            if (enemies[i].alive) {
                int indexX = i % numWidth_enemies; //Columns
                int indexY = i / numWidth_enemies; //Rows

                if (!seeded) {

                    // seed min/max with the first alive enemy
                    minX_enemies = maxX_enemies = minY_enemies = maxY_enemies = i;
                    seeded = true;
                } else {
                    if (indexX > maxX_enemies) maxX_enemies = i;
                    if (indexX < minX_enemies) minX_enemies = i;
                    if (indexY > maxY_enemies) maxY_enemies = i;
                    if (indexY < minY_enemies) minY_enemies = i;
                }

                amount_alive_enemies++;
            }
        }
    }

    // Moves the enemy formation
    public void updateEnemyPosition() {
        offset_enemies.x += direction_enemies * Gdx.graphics.getDeltaTime() * speed_enemies;

        //This block updates the enemies positions
        for (int i = 0; i < enemies.length; i++) {
            enemies[i].position.set(
                enemies[i].position_initial.x + offset_enemies.x,
                enemies[i].position_initial.y + offset_enemies.y
            );
        }
    }

    // Bullet collision detection
    public void enemyHit(Lifeguard lifeguard) {
        for (int i = 0; i < enemies.length; i++) {
            //Check to see if bullet overlaps with enemy. If so, enemy "dies" (gets deleted from screen).
            //Must check if they are alive first, or bullet will stop at first level.
            if (enemies[i].alive) {
                if (lifeguard.bullet_sprite.getBoundingRectangle().overlaps(enemies[i].enemy_sprite.getBoundingRectangle())) {
                    lifeguard.position_bullet.y = 100000;
                    enemies[i].alive = false;
                    break;
                }
            }
        }
    }

    // Bounds checking to ensure enemies do not exit the RIGHT or LEFT side of the screen
    public void enemyBoundsCheck() {
        float rightLimit = Gdx.graphics.getWidth() - enemies[0].enemy_sprite.getWidth();
        if (enemies[maxX_enemies].position.x >= rightLimit || enemies[minX_enemies].position.x <= 0f) {
            // Change directions
            direction_enemies *= -1;
            // Makes the enemies go down towards the lifeguard
            offset_enemies.y -= enemies[0].enemy_sprite.getHeight() * enemies[0].enemy_sprite.getScaleY() * 0.25f;
            // Makes the speed of the enemies increase over time
            speed_enemies += 0.1f;
        }
    }

    // Check if all enemies in current round are dead
    public boolean allDead() {
        return amount_alive_enemies == 0;
    }

    // Reset the enemy difficulty and formation
    public void resetEnemies() {
        speed_enemies = 200;
        direction_enemies = 1;
        numWidth_enemies = 3;
        numHeight_enemies = 2;
    }

}
