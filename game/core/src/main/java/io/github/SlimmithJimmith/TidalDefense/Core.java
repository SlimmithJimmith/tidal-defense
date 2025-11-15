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

// Scene2D
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;

// Utils
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.*;
import org.w3c.dom.Text;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture img_lifeguard;
    private Texture img_bullet;
    private Texture img_background;
    Lifeguard lifeguard; //Instantiate lifeguard object
    PowerUp powerUp; // Instantiate power up object
    boolean powerUpReady = true;

    // Enemy setup
    EnemyManager enemyManager;
    int currentLevel = 1;

    //Scorekeeping
    private int score = 0;

    // Menu variables
    private Stage menuStage;
    private Table settingsTable;
    private Table mainMenuTable;
    private boolean showingMenu = true;
    private boolean showSettings = false;
    private boolean paused = false;
    private boolean running = true;

    GameOver gameOver;
    private boolean showGameOver = false;

    // Overall volume
    float volume = 0.7f;

    // Sound
    Sound bullet_sound;
    Sound button_click_sound;
    Sound enemy_death;

    // Bg Music
    Music music;
    private boolean musicEnabled = true; // toggle button (initially true)

    // image files for each case.
    private Texture soundOnTex, soundOnPressedTex, soundOffTex, soundOffPressedTex;

    // We need this so we can display the textures for Scene2D to the button
    private TextureRegionDrawable soundOn, soundOnPressed, soundOff, soundOffPressed;

    //Leaderboard
    private Leaderboard leaderboard;

    //Heads up display of score and level
    private Stage hudStage;
    private Label scoreLabel;

    @Override
    public void create() {
        // Sound
        bullet_sound = Gdx.audio.newSound(Gdx.files.internal("sounds/gun_shot.mp3"));
        button_click_sound = Gdx.audio.newSound(Gdx.files.internal("sounds/button_click.mp3"));
        enemy_death = Gdx.audio.newSound(Gdx.files.internal("sounds/enemy_death.mp3"));

        // Bg music
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/bg_music.mp3"));
        music.setLooping(true); // repeat forever

        // Play only if music is enabled
        if (musicEnabled) {
            music.setVolume(volume);
            music.play();
        }

        // Load the image files for each button case
        soundOnTex = new Texture(Gdx.files.internal("button/music-on-btn-up.png"));
        soundOnPressedTex = new Texture(Gdx.files.internal("button/music-on-btn-down.png"));
        soundOffTex = new Texture(Gdx.files.internal("button/music-off-btn-up.png"));
        soundOffPressedTex = new Texture(Gdx.files.internal("button/music-off-btn-down.png"));

        // Wrap each texture into a TextureRegionDrawable
        // We did this since Scene2D buttons only understand drawables, not raw textures.
        soundOn = new TextureRegionDrawable(new TextureRegion(soundOnTex));
        soundOnPressed = new TextureRegionDrawable(new TextureRegion(soundOnPressedTex));
        soundOff = new TextureRegionDrawable(new TextureRegion(soundOffTex));
        soundOffPressed = new TextureRegionDrawable(new TextureRegion(soundOffPressedTex));

        batch = new SpriteBatch();
        img_lifeguard = new Texture("LifeguardShootingUp.png"); //loads lifeguad image
        img_bullet = new Texture("Bullet.png"); //loads bullet image
        img_background = new Texture("background-2.png");

        //Create menu
        menuStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(menuStage);
        mainMenu();

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
                    mainMenu();
                }
            }
        );


        //Create heads up display
        hudStage = new Stage(new ScreenViewport());
        BitmapFont font = new BitmapFont();
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

            if (showGameOver) {
                gameOver.render(Gdx.graphics.getDeltaTime());
                return;
            }

        // If the game is running and not paused, the gameplay will run.
            if (running && !paused) {

                //If the menu is not showing, the gameplay will run.
                if (!showingMenu) {

                    // Move enemies
                    enemyManager.updateEnemyPosition();

                    ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
                    batch.begin();
                    batch.draw(img_background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                    lifeguard.Draw(batch);

                    // Check if power up received

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
                        batch.end();   // Batch was begun earlier
                        return;        // Stop the rest of render() for this frame
                    }

                    batch.end();

                    //Draw heads up display
                    hudStage.act(Gdx.graphics.getDeltaTime());
                    hudStage.draw();
                } // End if (!showingMenu)
            } // End if (running && !paused)
    } // End render()

    /*
    Try/catch is used to catch and display runtime errors when building the buttons
    If you run into any issues and see a white box, there may be a typo in your file name.
     */
    /**
     * Helper method to build the buttons using their texture files
     *
     * @param upFile unpressed look for the button
     * @param downFile pressed look for the button
     * @return drawable button
     */
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

    private void triggerGameOver() {
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
    private void mainMenu(){
        // Clear any existing tables.
        clearTables();

        menuStage.clear();
        Image menuBg = new Image(new TextureRegionDrawable(new TextureRegion(img_background)));
        menuBg.setFillParent(true);
        menuStage.addActor(menuBg);

        ImageButton playButton = makeButton("button/play-btn-up.png", "button/play-btn-down.png");

        // Listener listens for the click on the button.
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button_click_sound.play(volume);
                clearTables();
                paused = false;         // Game is not paused.
                running = true;         // Game is running.
                showingMenu = false;    // Closes menu.
            }
        });

        ImageButton quitButton = makeButton("button/quit-btn-up.png", "button/quit-btn-down.png");
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button_click_sound.play(volume);
                Gdx.app.exit();     // Closes the application from the menu.
            }
        });

        ImageButton settingsButton = makeButton("button/settings-btn-up.png", "button/settings-btn-down.png");
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button_click_sound.play(volume);
                System.out.println("Settings button clicked!");
                paused = true;
                settingsMenu();
            }
        });

        // Don't have a listener for this button at this time.
        ImageButton leaderboardButton = makeButton("button/lead-btn-up.png", "button/lead-btn-down.png");

        // Build the table of buttons.
        mainMenuTable = new Table();
        mainMenuTable.setFillParent(true);
        mainMenuTable.center();

        // Add the buttons to the table.
        mainMenuTable.add(playButton).width(300).height(80).row();
        mainMenuTable.add(quitButton).width(300).height(80).row();
        mainMenuTable.add(settingsButton).width(300).height(80).row();
        mainMenuTable.add(leaderboardButton).width(300).height(80).row();

        //Add the actor (button) to the stage.
        menuStage.addActor(mainMenuTable);

        // Event handler for the input on the stage.
        Gdx.input.setInputProcessor(menuStage);
    }
    /**
     * settingsMenu clears the screen of any existing buttons and builds the
     * buttons for the settings screen. Each button will have their own listener
     * to tigger their events.
     */
    private void settingsMenu() {
        // Clear any existing tables.
        clearTables();
        menuStage.clear();

        Image menuBg = new Image(new TextureRegionDrawable(new TextureRegion(img_background)));
        menuBg.setFillParent(true);
        menuStage.addActor(menuBg);

        // Build the buttons and their listeners. (Only 1 right now :) )
        ImageButton backButton = makeButton("button/back-btn-up.png", "button/back-btn-down.png");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button_click_sound.play(volume);
                mainMenu();
            }
        });

        // I did this so we later update it according to the local saved file for each user
        // Create music toggle button (according to the initial settings)
        // If musicEnabled = true -> show (music-on) icon
        // If musicEnabled = false -> show (music-off) icon
        ImageButton musicBtn = new ImageButton(
            musicEnabled ? soundOn : soundOff,
            musicEnabled ? soundOnPressed : soundOffPressed
        );

        musicBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button_click_sound.play(volume); // click sound

                // Flip music state: if On then Off, and vice versa.
                musicEnabled = !musicEnabled;

                // If music is now enabled, start or resume playing it
                if (musicEnabled) {
                    if (!music.isPlaying()) {
                        music.setVolume(volume);
                        music.play(); // Resume if it was paused
                    }

                    // Update the button icon to show (music:on)
                    musicBtn.getStyle().imageUp   = soundOn;
                    musicBtn.getStyle().imageDown = soundOnPressed;
                } else {
                    // Otherwise, pause the music
                    music.pause();
                    // Update the button icon to show (music:off)
                    musicBtn.getStyle().imageUp   = soundOff;
                    musicBtn.getStyle().imageDown = soundOffPressed;
                }
            }
        });

        // Build the table of buttons.
        settingsTable = new Table();
        settingsTable.setFillParent(true);
        settingsTable.center();

        // Add the button to the table. (Music toggle first,then Back)
        settingsTable.add(musicBtn).width(300).height(80).row();
        settingsTable.add(backButton).width(300).height(80).row();

        // Add the actor (button) to the stage.
        menuStage.addActor(settingsTable);

        // Event handler for the input on the stage.
        Gdx.input.setInputProcessor(menuStage);
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

        if(settingsTable != null) {
            settingsTable.remove();
            settingsTable = null;
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

    @Override
    public void dispose() {
        batch.dispose();
        //Dispose of sprites
        img_lifeguard.dispose();
        img_bullet.dispose();
        img_background.dispose();
        if (gameOver != null) gameOver.dispose();
        if (soundOnTex != null)  soundOnTex.dispose();
        if (soundOnPressedTex != null) soundOnPressedTex.dispose();
        if (soundOffTex != null)  soundOffTex.dispose();
        if (soundOffPressedTex != null) soundOffPressedTex.dispose();
        if (music != null) music.dispose();
    }
}


