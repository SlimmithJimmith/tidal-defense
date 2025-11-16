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

import java.util.ArrayList;

public class EnemyManager {

    // Regular enemy formation
    Enemy[] enemies; //Instantiate enemy object

    // For boss level
    private ArrayList<OctoMini> octoMinis;
    private float octoMiniSpawnTimer = 0f;
    private static final float OCTOMINI_SPAWN_INTERVAL = 2f;

    // Amount, speed and direction variables
    private int numWidth_enemies = 3; // Sets width of enemy formation
    private int numHeight_enemies = 2; // Sets height of enemy formation
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
        octoMinis = new ArrayList<>();
    }

    // Enemy formation creation
    public void createFormation(int level) {
        offset_enemies = new Vector2(0,0);
        applyLevelBaseSpeed(level);
        clearOctoMinis();

        // Get formation size, full-width rows from the start
        numWidth_enemies = colsToFillWidth(60f);

        //Rows ramp 1 → 2 → 3 with a tiny bit of randomness
        if (level <= 3) {
            int maxRows = Math.min(3, level + 1);
            numHeight_enemies = MathUtils.random(level, maxRows);
        } else if (level % 7 == 0) {
            numWidth_enemies = 1; // boss level!
            numHeight_enemies = 1;
        } else {
            int minRows = 2;
            int maxRows = Math.min(4, 1 + (level - 2));
            numHeight_enemies = MathUtils.random(minRows, maxRows);
        }

        // Creates enemy array with length based on height and width params
        enemies = new Enemy[numWidth_enemies * numHeight_enemies];

        int i = 0;
        // Create the enemies, positioning will be dependent on what is in formation (need more space for Sharks)
        for (int y = 0; y < (numHeight_enemies); y++) {
            for (int x = 0; x < (numWidth_enemies); x++) {
                Vector2 tempPosition = new Vector2(0,0);
                enemies[i] = (level % 7 == 0) ? new OctoBoss(tempPosition) : getEnemyType(level, y, numHeight_enemies, tempPosition);
                enemies[i].alive = true;
                i++;
            }
        }

        // Set positions based on what enemy types are in formation
        computeSpacing();
    }

    // Position new formation dynamically
    private void computeSpacing() {
        float minGap = 20f;
        float maxGap = 30f;

        for (int row = 0; row < numHeight_enemies; row++) {
            // Get all enemies in this row
            int rowStart = row * numWidth_enemies;
            int rowEnd = rowStart + numWidth_enemies;
            //float maxHeight = 0;

            float totalEnemyWidth = 0;
            for (int i =rowStart; i < rowEnd; i++) {
                totalEnemyWidth += enemies[i].enemy_sprite.getWidth();
                //maxHeight = Math.max(enemies[i].enemy_sprite.getHeight(), maxHeight);
            }

            // Calculate available space for gaps
            float availableWidth = Gdx.graphics.getWidth()- 2f * SIDE_MARGIN_PX - totalEnemyWidth;
            float gapSize = availableWidth / (numWidth_enemies - 1);

            // Clamp gap size
            gapSize = MathUtils.clamp(gapSize, minGap, maxGap);

            // If gaps are maxed out, center the row
            float totalRowWidth = totalEnemyWidth + gapSize * (numWidth_enemies - 1);
            float startX = (Gdx.graphics.getWidth() - totalRowWidth) / 2f;

            // Calc y position for this row
            float yPos = Gdx.graphics.getHeight() - 60f - (row * 60f);

            float currentX = startX;
            for (int i = rowStart; i < rowEnd; i++) {
                enemies[i].position.set(currentX, yPos);
                enemies[i].position_initial.set(currentX, yPos);
                currentX += enemies[i].enemy_sprite.getWidth() + gapSize;
            }
        }
    }

    // Decides enemy type to put into array for each level
    private Enemy getEnemyType(int level, int rowFromTop, int totalRows, Vector2 position) {
        // Levels 1–3: only Fish (no sharks)
        if (level <= 3) {
            return new Fish(position);
        }
        //Base shark chance: grows with level, capped lower, so we rely on row bias
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
        return baseChance * (0.30f + 0.70f * rowWeight);
    }

    // Detect enemy collision with player
    public boolean playerCollision(Lifeguard lifeguard) {
        for (Enemy enemy : enemies) {
            if (enemy.enemy_sprite.getBoundingRectangle().overlaps(lifeguard.lifeguard_sprite.getBoundingRectangle()))
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
        for (Enemy enemy : enemies) {
            if (enemy.alive) {
                enemy.Draw(batch);
            }
        }

        for (OctoMini octoMini : octoMinis) {
            octoMini.Draw(batch);
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
        for (Enemy enemy : enemies) {
            enemy.position.set(
                enemy.position_initial.x + offset_enemies.x,
                enemy.position_initial.y + offset_enemies.y
            );
        }
    }

    // Bullet collision detection
    public int enemyHit(Lifeguard lifeguard, Sound enemy_death) {
        for (Enemy enemy : enemies) {
            //Check to see if bullet overlaps with enemy. If so, enemy "dies" (gets deleted from screen).
            //Must check if they are alive first, or bullet will stop at first level.
            if (enemy.alive) {
                if (lifeguard.bullet_sprite.getBoundingRectangle().overlaps(enemy.enemy_sprite.getBoundingRectangle())) {
                    lifeguard.position_bullet.y = 100000;
                    enemy.takeDamage(); // Reduce health
                    enemy_death.play(volume);

                    if (enemy.health <= 0) {
                        enemy.alive = false; // Destroy enemy if health is zero
                        return enemy.getPoints(); // Return the point for a single enemy death
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

    //How many columns will fit between side margin
    private int colsToFillWidth(float desiredSpacing) {
        float available = Gdx.graphics.getWidth() - 2f * SIDE_MARGIN_PX;

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

    // Everything below is for OctoMinis
    // Update positions and spawn if time interval is met
    public void updateOctoMinis(float deltaTime) {
        octoMiniSpawnTimer += deltaTime * MathUtils.random() * 3d;
        if (octoMiniSpawnTimer >= OCTOMINI_SPAWN_INTERVAL) {
            spawnOctoMini();
            octoMiniSpawnTimer = 0f;
        }

        for (int i = octoMinis.size() - 1; i >= 0; i--) {
            OctoMini mini = octoMinis.get(i);
            mini.updatePosition(); // Update positions of alve minis
            if (mini.position.y < 0) { // Get rid of ones passed player
                octoMinis.remove(i);
            }
        }
    }

    // Create new OctoMini
    private void spawnOctoMini() {
        // Spawn at center-bottom of boss
        OctoBoss boss = (OctoBoss) enemies[0];
        float spawnX = boss.position.x + boss.enemy_sprite.getWidth() / 2f;
        float spawnY = boss.position.y;

        Vector2 spawnPos = new Vector2(spawnX, spawnY);
        octoMinis.add(new OctoMini(spawnPos));
    }

    // Return points if mini hit
    public int octoMiniHit(Lifeguard lifeguard, Sound enemy_death) {
        for (int i = octoMinis.size() - 1; i >= 0; i--) {
            OctoMini mini = octoMinis.get(i);
            if (lifeguard.bullet_sprite.getBoundingRectangle().overlaps(mini.enemy_sprite.getBoundingRectangle())) {
                lifeguard.position_bullet.y = 100000;
                enemy_death.play();
                octoMinis.remove(i);
                return mini.getPoints();
            }
        }
        return 0;
    }

    // Mini collides with player
    public boolean octoMiniPlayerCollision(Lifeguard lifeguard) {
        for (int i = octoMinis.size() - 1; i >= 0; i--) {
            OctoMini mini = octoMinis.get(i);
            if (mini.enemy_sprite.getBoundingRectangle().overlaps(lifeguard.lifeguard_sprite.getBoundingRectangle())) {
                return true;
            }
        }
        return false;
    }

    public void clearOctoMinis() {
        octoMinis.clear();
        octoMiniSpawnTimer = 0f;
    }
}
