/**
 * EnemyManager.java
 * Manages the different enemy arrays and their actions
 *
 * @author Team #2 - Brendan Boyko, Jimi Ruble, Mehdi Khazaal, James Watson
 * @version 1.0
 * Create Date: 09-27-2025
 */


package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class EnemyManager {

    // Enemy Setup
    Enemy[] enemies; //Instantiate enemy object

    // Amount, speed and direction variables
    private int numWidth_enemies = 3; // Sets width of enemy formation
    private int numHeight_enemies = 2; // Sets height of enemy formation
    private int spacing_enemies; // Keeps the enemies from being on top of one another
    private int minX_enemies, minY_enemies, maxX_enemies, maxY_enemies; // Bounds checking
    private int direction_enemies = 1; // Toggles for left/right movement
    private float speed_enemies = 200;
    private Vector2 offset_enemies; // Offset to move the enemies
    public float volume = 0.5f;

    private int amount_alive_enemies;

    public EnemyManager() {
        offset_enemies = new Vector2(0,0);
    }

    /**
     * The if/else are basically copies of each other.
     * Switches to Sharks after level 3
     * I had to break it out to adjust formation size and the spacing between Sprites
     * Otherwise the sharks overlapped or had their heads above the screen when created
     * @param level - The formation iteration the player is on
     */
    // Enemy formation creation
    public void createFormation(int level) {
        offset_enemies = new Vector2(0, 0);

        if (level <= 3) {
            // Give space from top of screen for enemy
            float topMargin = 40f;

            // Space between enemies
            spacing_enemies = 40;

            // Calculate enemy matrix size here, randomize...?
            numWidth_enemies = numWidth_enemies + (level - 1);
            numHeight_enemies = numHeight_enemies + (level - 1);

            // Creates enemy array with length based on height and width params
            enemies = new Enemy[numWidth_enemies * numHeight_enemies];

            // Start position of enemies for x and y coordinates to center
            float startX = Gdx.graphics.getWidth()/2f - (numWidth_enemies/2f) * spacing_enemies;
            float startY = Gdx.graphics.getHeight() - topMargin - (numHeight_enemies - 1) * spacing_enemies;

            int i = 0;
            // Nested for loop to generate enemies
            for (int y = 0; y < (numHeight_enemies); y++) {
                for (int x = 0; x < (numWidth_enemies); x++) {

                    //position & create the enemies
                    Vector2 position = new Vector2(startX + x * spacing_enemies,
                        startY + (numHeight_enemies - 1 - y) * spacing_enemies);
                    enemies[i] = new Fish(position);
                    enemies[i].alive = true;
                    i++;
                }
            }
        } else {
            // Give space from top of screen for enemy
            float topMargin = 100f;

            // Enemy matrix size and space between
            numHeight_enemies = 1;
            numWidth_enemies = 3;
            spacing_enemies = 100;

            // Creates enemy array with length based on height and width params
            enemies = new Enemy[numWidth_enemies * numHeight_enemies];

            // Start position of enemies for x and y coordinates to center
            float startX = Gdx.graphics.getWidth()/2f - (numWidth_enemies/2f) * spacing_enemies;
            float startY = Gdx.graphics.getHeight() - topMargin - (numHeight_enemies - 1) * spacing_enemies;

            int i = 0;
            // Nested for loop to generate enemies
            for (int y = 0; y < (numHeight_enemies); y++) {
                for (int x = 0; x < (numWidth_enemies); x++) {

                    //position & create the enemies
                    Vector2 position = new Vector2(startX + x * spacing_enemies,
                        startY + (numHeight_enemies - 1 - y) * spacing_enemies);
                    enemies[i] = new Shark(position);
                    enemies[i].alive = true;
                    i++;
                }
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
    public void enemyHit(Lifeguard lifeguard, Sound enemy_death) {
        for (int i = 0; i < enemies.length; i++) {
            //Check to see if bullet overlaps with enemy. If so, enemy "dies" (gets deleted from screen).
            //Must check if they are alive first, or bullet will stop at first level.
            if (enemies[i].alive) {
                if (lifeguard.bullet_sprite.getBoundingRectangle().overlaps(enemies[i].enemy_sprite.getBoundingRectangle())) {
                    lifeguard.position_bullet.y = 100000;
                    enemies[i].takeDamage(); // Reduce health
                    enemy_death.play(volume);

                    if (enemies[i].health <= 0) enemies[i].alive = false; // Destroy enemy if health is zero

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
            // Move enemies one step inward to prevent 'falling'
            this.updateEnemyPosition();
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
