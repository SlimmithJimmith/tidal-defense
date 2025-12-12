/**
 * Core.java
 * Manages the program's gameplay and GUI
 *
 * @author Team #2 - Brendan Boyko, Jimi Ruble, Mehdi Khazaal, James Watson
 * @version 1.0
 * Create Date: 09-27-2025
 */

package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

// Audio
import com.badlogic.gdx.audio.*;

// Graphics
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

// Scene2D
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;

// Utils
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends ApplicationAdapter {
    public SpriteBatch batch;
    private Texture img_lifeguard;
    private Texture img_bullet;
    private Texture img_background;
    private Texture img_title;
    Lifeguard lifeguard; //Instantiate lifeguard object
    PowerUp powerUp; // Instantiate power up object
    private boolean powerUpReady = true;

    // Enemy setup
    EnemyManager enemyManager;
    int currentLevel = 10;

    //Scorekeeping
    private int score = 0;

    // Menu variables
    private Stage menuStage;
    private Table mainMenuTable;
    boolean showingMenu = true;
    private boolean paused = false;
    private boolean running = true;

    GameOver gameOver;
    private boolean showGameOver = false;

    // Overall volume
    private float volume = 0.7f;

    // Music enabled flag
    private boolean musicEnabled = true;


    // Sound
    Sound bullet_sound;
    Sound button_click_sound;
    Sound enemy_death;

    // Bg Music
    Music music;

    // Setting screen
    private SettingsMenu settingsMenu;

    private MainMenu mainMenu;

    private GamePlay gamePlay;

    //Leaderboard
    private Leaderboard leaderboard;

    //Heads up display of score and level
    private Stage hudStage;
    private Label scoreLabel;

    private ImageButton pauseBtn;
    public Stage gameStage;
    public Table pauseTable;

    @Override
    public void create() {
        // Sound
        bullet_sound = Gdx.audio.newSound(Gdx.files.internal("sounds/gun_shot.mp3"));
        button_click_sound = Gdx.audio.newSound(Gdx.files.internal("sounds/button_click.mp3"));
        enemy_death = Gdx.audio.newSound(Gdx.files.internal("sounds/enemy_death.mp3"));

        // Bg music
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/bg_music.mp3"));
        music.setLooping(true); // repeat forever
        music.setVolume(volume);
        music.play();

        batch = new SpriteBatch();
        img_lifeguard = new Texture("LifeguardShootingUp.png"); //loads lifeguad image
        img_bullet = new Texture("Bullet.png"); //loads bullet image
        img_background = new Texture("background-2.png");
        img_title = new Texture("Title.png");

        //Create menu
        menuStage = new Stage(new ScreenViewport());

        gamePlay = new GamePlay(powerUpReady, img_background, score, scoreLabel, hudStage,this);

        settingsMenu = new SettingsMenu(menuStage, img_background, music, button_click_sound,
            this, // pass Core so SettingsMenu can call getVolume/setVolume
            new Runnable() {
                @Override
                public void run() {
                    mainMenu.showMainMenu();
                }
            }
        );

        mainMenu = new MainMenu(menuStage, img_background, button_click_sound, volume,
            this,
            //toPlayGame
            new Runnable() {
                 @Override
                public void run() {
                    showingMenu = false;
                    running = true;
                    paused = false;
                }
            },
            //toQuit
            new Runnable() {
                @Override
                public void run() {
                    Gdx.app.exit();
                }
            },
            //toSettingsMenu
            new Runnable() {
            @Override
                public void run() {
                settingsMenu.show();
                settingsMenu.render(Gdx.graphics.getDeltaTime());
                showingMenu = true;
            }
            });

        Gdx.input.setInputProcessor(menuStage);

        lifeguard = new Lifeguard(img_lifeguard, img_bullet, Color.BLUE, bullet_sound); //creates lifeguard + bullet, bullet color is blue

        // Changed to enemy manager to modularize
        enemyManager = new EnemyManager();
        // Initialize enemy formation with level 1
        enemyManager.createFormation(currentLevel);

        //Create and load leaderboard
        leaderboard = new Leaderboard();
        leaderboard.loadAll();

        // Build Game Over UI and wire callbacks
        gameOver = new GameOver(leaderboard, button_click_sound, volume,
            // onPlayAgain
            new Runnable() {
                @Override
                public void run() {
                    resetGame();
                    showGameOver = false;
                    Gdx.input.setInputProcessor(null); // back to game
                }
            },
            // onQuit
            new Runnable() {
                @Override
                public void run() {
                    Gdx.app.exit();
                }
            },
            // onReturnToMenu
            new Runnable() {
                @Override
                public void run() {
                    resetGame();
                    showGameOver = false;
                    showingMenu = true;
                    mainMenu.showMainMenu();
                }
            });


        //Create heads up display
        hudStage = new Stage(new ScreenViewport());
        // Font config
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Pixel Game.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        scoreLabel = new Label("Score: 0", style);

        //Position of scoreboard
        scoreLabel.setPosition(10, Gdx.graphics.getHeight() - 30);
        hudStage.addActor(scoreLabel);
    }


    @Override
    public void render() {

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            menuStage.act(Gdx.graphics.getDeltaTime());
            menuStage.draw();

            if(mainMenu.isVisible){
                menuStage.act(Gdx.graphics.getDeltaTime());
                menuStage.draw();
            }
            if (showGameOver) {
                gameOver.render(Gdx.graphics.getDeltaTime());
                return;
            }

            // If the game is running and not paused, the gameplay will run.
            if (running && !paused) {

                //If the menu is not showing, the gameplay will run.
                if (!showingMenu) {
                    gamePlay.render();

                    // Move enemies
                    enemyManager.updateEnemyPosition();

                    ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
                    batch.begin();
                    batch.draw(img_background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                    lifeguard.Draw(batch);

                    // enemyManager returns zero or num points of enemy killed
                    score += enemyManager.enemyHit(lifeguard, enemy_death);
                    scoreLabel.setText("Level: " + currentLevel + " Score: " + score); //display score to user

                    // Bounds checking for current state of enemy formation
                    enemyManager.updateEnemyBounds();

                    // Regenerates enemies when all are destroyed
                    if (enemyManager.allDead()) {
                        enemyManager.createFormation(++currentLevel);

                        // every 3 levels, create power up if not already created
                        if (powerUpReady && (currentLevel - 1) % 3 == 0) {
                            powerUp = new PowerUp();
                            powerUpReady = false;
                        }
                        // reset power up readiness for next time
                        if ((currentLevel - 1) % 3 != 0) {
                            powerUpReady = true;
                        }
                        batch.end();
                        return;
                    }

                    // Move the power up if it is active
                    if (powerUp != null) {
                        // Check for power up being picked up
                        if (powerUp.playerCollision(lifeguard)) {
                            lifeguard.PowerUp();
                            powerUp.dispose();
                            powerUp = null;
                        } else if (powerUp.powerUpPastPlayer()) { // Player missed it
                            powerUp.dispose();
                            powerUp = null;
                        } else {
                            powerUp.updatePowerUpPosition();
                            powerUp.Draw(batch);
                        }
                    }

                    // Bounds checking to ensure enemies do not exit the RIGHT or LEFT side of the screen
                    enemyManager.enemyBoundsCheck();

                    // Draw enemies still alive
                    enemyManager.drawBatch(batch);

                    // If enemy touches the lifeguard or below player, show Game Over.
                    if (enemyManager.playerCollision(lifeguard) || enemyManager.enemyPastPlayer()) {
                        triggerGameOver();
                        batch.end(); // Batch was begun earlier
                        return; // Stop the rest of render() for this frame
                    }

                    // Spawn OctoMini if in boss level
                    if (currentLevel % 7 == 0) {
                        enemyManager.updateOctoMinis(Gdx.graphics.getDeltaTime()); // move minis

                        score += enemyManager.octoMiniHit(lifeguard, enemy_death); // detect mini being hit

                        if (enemyManager.octoMiniPlayerCollision(lifeguard)) { // detect player being hit
                            triggerGameOver();
                            batch.end();
                            return;
                        }
                    }

                    batch.end();

                    //Draw heads up display
                    hudStage.act(Gdx.graphics.getDeltaTime());
                    hudStage.draw();
                } // End if (!showingMenu)
            } // End if (running && !paused)
    } // End render()

    public void triggerGameOver() {
        // Tell the game that we are now in the "game over" state
        showGameOver = true;

        // update the score that GameOver will save
        gameOver.setScore(score);

        // rebuild the Game Over UI EVERY time the player lose
        gameOver.resetToGameOverScreen();

        // Switch input control to the GameOver stage, so the player can click the Game Over buttons
        gameOver.show();
    }

    private void resetGame(){
        currentLevel = 1;
        score = 0; //Set the score back to 0 when game is reset.
        enemyManager.resetEnemies();
        lifeguard.ResetBullet();

        // Reset lifeguard batch.draw(img_background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());position and bullets:
        float startX = Gdx.graphics.getWidth() / 2f - lifeguard.lifeguard_sprite.getWidth() / 2f;
        lifeguard.position.set(startX, 6f);
        lifeguard.position_bullet.set(0f, 10000f);

        enemyManager.createFormation(currentLevel);
    }

    /**
     * mainMenu builds the buttons for the menu. Each button has its own
     * listener to "listen" for the click of the button.This will trigger
     * whatever the buttons purpose is.
     */

    private void pauseMenu(){
        clearTables();

        menuStage.clear();
        //Background
        Image menuBg = new Image(new TextureRegionDrawable(new TextureRegion(img_background)));
        menuBg.setFillParent(true);
        menuStage.addActor(menuBg);
    }

    private ImageButton makeButton(String upFile, String downFile){
        //Load textures for the play button
        try {
            Texture upTex = new Texture(Gdx.files.internal(upFile));
            Texture downTex = new Texture(Gdx.files.internal(downFile));

            //Create Drawables for play button
            TextureRegionDrawable upDrawable = new TextureRegionDrawable(new TextureRegion(upTex));
            TextureRegionDrawable downDrawable = new TextureRegionDrawable(new TextureRegion(downTex));

            return new ImageButton(upDrawable, downDrawable);

        }catch (Exception e){
            e.printStackTrace();
            Pixmap pixmap = new Pixmap(100, 50, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            Texture fallbackTex = new Texture(pixmap);
            TextureRegionDrawable fallbackDrawable = new TextureRegionDrawable(new TextureRegion(fallbackTex));
            return new ImageButton(fallbackDrawable, fallbackDrawable);
        }
    }

    public void leaderboardMenu(){
        // Clear any existing tables.
        clearTables();
        menuStage.clear();

        Image menuBg = new Image(new TextureRegionDrawable(new TextureRegion(img_background)));
        menuBg.setFillParent(true);
        menuStage.addActor(menuBg);
    }

    /**
     * Clears the table from the stage before adding a new table
     * to the stage. Keeps tables from overlapping.
     */
    private void clearTables() {
        if(mainMenuTable != null) {
            mainMenuTable.remove();
            mainMenuTable = null;
        }
    }

    //Buttons will not distort when the screen is resized.
    @Override
    public void resize(int width, int height) {
        //I have this commented out since the game is not resizable at this time, the buttons don't need to be either.
        //menuStage.getViewport().update(width, height, true);
        if (menuStage != null)     menuStage.getViewport().update(width, height, true);
        if (gameOver != null)    gameOver.resize(width, height);
    }

    /**
     * Getter for current volume.
     *
     * @return current master volume (0.0 - 1.0)
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Setter for current volume. Called by SettingsMenu slider.
     *
     * @param volume new volume (0.0 - 1.0)
     */
    public void setVolume(float volume) {
        this.volume = volume;
    }

    /**
     * Returns whether background music is enabled.
     *
     * @return true if music is enabled, false otherwise
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     * Sets whether background music is enabled.
     *
     * @param musicEnabled new music enabled flag
     */
    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }


    @Override
    public void dispose() {
        batch.dispose();
        //Dispose of sprites
        img_lifeguard.dispose();
        img_bullet.dispose();
        img_background.dispose();
        if (gameOver != null) gameOver.dispose();
        if (settingsMenu != null) settingsMenu.dispose();
        if (music != null) music.dispose();
    }
}


