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
import com.badlogic.gdx.math.MathUtils; //for random

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
    private static final float SIDE_MARGIN_PX = 60f; // sets the side margin pixels
    // Base formation speed tuning
    private static final float BASE_SPEED = 180f;  //starting speed at level 1
    private static final float LEVEL_SPEED_DELTA = 50f;   //+per level
    private static final float MAX_LEVEL_BASE_SPEED = 1000f;  //cap so it never feels unfair

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

        applyLevelBaseSpeed(level);

        if (level <= 3) {
            // Give space from top of screen for enemy
            float topMargin = 40f;

            float sideMargin = 60f;
            float desiredSpacing = 60f; //50–70 works

            //Full-width rows from the start
            numWidth_enemies = colsToFillWidth(desiredSpacing, sideMargin);

             //Rows ramp 1 → 2 → 3 with a tiny bit of randomness
            int baseRows = level; // L1=1, L2=2, L3=3
            int maxRows  = Math.min(3, baseRows + 1); //allow a small bump, but cap at 3
            numHeight_enemies = MathUtils.random(baseRows, maxRows);

             //Tight spacing based on current width
            spacing_enemies = computeSpacing(numWidth_enemies, sideMargin);
            //Creates enemy array with length based on height and width params
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
                    int rowFromTop = y; //y=0 is top row in loop
                    int totalRows  = numHeight_enemies;
                    enemies[i] = makeEnemyForLevel(level, rowFromTop, totalRows, position);
                    enemies[i].alive = true;
                    i++;
                }
            }
        } else {
            // Give space from top of screen for enemy
            float topMargin = 100f;

            // Always span rows edge-to-edge (classic feel)
            float desiredSpacing = 60f; // adjust to desired spacing
            numWidth_enemies = colsToFillWidth(desiredSpacing, SIDE_MARGIN_PX);

            // Row count grows a bit with level but stays relatively small. Can adjust later
            int minRows = 2;
            int maxRows = Math.min(4, 1 + (level - 2)); //slowly allow up to 4 rows by higher levels
            numHeight_enemies = MathUtils.random(minRows, Math.max(minRows, maxRows));

            // Tighten spacing to fit the full-width columns
            spacing_enemies = computeSpacing(numWidth_enemies, SIDE_MARGIN_PX);

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
                    int rowFromTop = y; //y=0 is top row in loop
                    int totalRows  = numHeight_enemies;
                    enemies[i] = makeEnemyForLevel(level, rowFromTop, totalRows, position);
                    enemies[i].alive = true;
                    i++;
                }
            }
        }
    }

    //Make enemies for each level
    private Enemy makeEnemyForLevel(int level, int rowFromTop, int totalRows, Vector2 position) {
        // Levels 1–3: only Fish (no sharks)
        if (level <= 3) {
            return new Fish(position);
        }
        //Base shark chance: grows with level, capped lower so we rely on row bias
        float sharkChance = getSharkChance(level, rowFromTop, totalRows);


        boolean spawnShark = MathUtils.randomBoolean(sharkChance);
        return spawnShark ? new Shark(position) : new Fish(position);
    }

    private static float getSharkChance(int level, int rowFromTop, int totalRows) {
        float baseChance = 0.08f + (level - 4) * 0.04f; // 8% at L4, +4%/lvl. Adjust this if too easy or hard
        baseChance = MathUtils.clamp(baseChance, 0.08f, 0.30f); // cap 30%

        // Row bias: 1.0 at top row, 0.0 at bottom row
        float rowWeight = (totalRows <= 1) ? 1f
            : 1f - (rowFromTop / (float) (totalRows - 1)); // top=1, bottom=0

        //Scale base chance by row: bottom gets only 30% of base, top gets 100% of base
        float sharkChance = baseChance * (0.30f + 0.70f * rowWeight);
        return sharkChance;
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
    public int enemyHit(Lifeguard lifeguard, Sound enemy_death) {
        for (int i = 0; i < enemies.length; i++) {
            //Check to see if bullet overlaps with enemy. If so, enemy "dies" (gets deleted from screen).
            //Must check if they are alive first, or bullet will stop at first level.
            if (enemies[i].alive) {
                if (lifeguard.bullet_sprite.getBoundingRectangle().overlaps(enemies[i].enemy_sprite.getBoundingRectangle())) {
                    lifeguard.position_bullet.y = 100000;
                    enemies[i].takeDamage(); // Reduce health
                    enemy_death.play(volume);

                    if (enemies[i].health <= 0) {
                        enemies[i].alive = false; // Destroy enemy if health is zero
                        int points = enemies[i].getPoints();//Get the points from specific enemy type
                        return points; //Return the point for a single enemy death
                    }

                    break; //If bullet hit something, stop checking other enemies in this frame
                }
            }
        }
        return 0; //If no kill happened on this call, return 0.
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

    //Manage the spacing of the enemy formations and keeps the spacing tight.
    private int computeSpacing(int cols, float sideMarginPx) {
        //Don't need to space single column
        if (cols <= 1) {
            return 0;
        }

        float available = Gdx.graphics.getWidth() - 2f * sideMarginPx;
        float spacing = available / (cols - 1);

        //Clamp the spacing so it never becomes too loose or cramped
        spacing = MathUtils.clamp(spacing, 20f, 40f);

        return (int) spacing;
    }

    //How many columns will fit between side margin
    private int colsToFillWidth(float desiredSpacing, float sideMarginPx) {
        float available = Gdx.graphics.getWidth() - 2f * sideMarginPx;

        // +1 because N columns have (N - 1) gaps.
        int cols = 1 + (int) Math.floor(available / desiredSpacing);
        //Adjust this max later if the goal is to have denser rows:
        cols = MathUtils.clamp(cols, 3, 10);
        return cols;
    }

    // Sets the base speed for a level
    private void applyLevelBaseSpeed(int level) {
        float base = BASE_SPEED + (level - 1) * LEVEL_SPEED_DELTA;
        speed_enemies = MathUtils.clamp(base, BASE_SPEED, MAX_LEVEL_BASE_SPEED);
    }
}
