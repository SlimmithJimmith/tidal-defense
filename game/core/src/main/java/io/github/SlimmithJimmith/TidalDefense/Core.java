package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends ApplicationAdapter {

    // Images for objects
    private SpriteBatch batch;
    private Texture img_lifeguard;
    private Texture img_bullet;
    private Texture img_enemy;
    private Texture img_background;

    //Instantiate lifeguard object
    Lifeguard lifeguard;

    // Enemy setup
    EnemyManager enemyManager;
    int currentLevel = 1;

    // Menu variables
    private Stage menuStage;
    private Table settingsTable;
    private Table mainMenuTable;
    private boolean showingMenu = true;
    private boolean showSettings = false;
    private boolean paused = false;
    private boolean running = true;

    // GameOver stage:
    private Stage gameOverStage;
    private boolean showGameOver = false;
    private Texture gameOverTitleTex;
    private Texture gameOverBgTex;

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

    @Override
    public void create() {
        menuStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(menuStage);
        mainMenu();

        // Sound
        bullet_sound = Gdx.audio.newSound(Gdx.files.internal("sounds/gun_shot.mp3"));
        button_click_sound = Gdx.audio.newSound(Gdx.files.internal("sounds/button_click.mp3"));
        enemy_death = Gdx.audio.newSound(Gdx.files.internal("sounds/enemy_death.mp3"));

        // Bg music
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/bg_music.mp3"));
        music.setLooping(true); // repeat forever

        // Play only if music is enabled
        if (musicEnabled) {
            music.play();
        }

        // Load the image files for each button case
        soundOnTex = new Texture(Gdx.files.internal("button/music-on.png"));
        soundOnPressedTex = new Texture(Gdx.files.internal("button/music-on-pressed.png"));
        soundOffTex = new Texture(Gdx.files.internal("button/music-off.png"));
        soundOffPressedTex = new Texture(Gdx.files.internal("button/music-off-pressed.png"));

        // Wrap each texture into a TextureRegionDrawable
        // We did this since Scene2D buttons only understand drawables, not raw textures.
        soundOn = new TextureRegionDrawable(new TextureRegion(soundOnTex));
        soundOnPressed = new TextureRegionDrawable(new TextureRegion(soundOnPressedTex));
        soundOff = new TextureRegionDrawable(new TextureRegion(soundOffTex));
        soundOffPressed = new TextureRegionDrawable(new TextureRegion(soundOffPressedTex));

        batch = new SpriteBatch();
        img_lifeguard = new Texture("LifeguardShootingUp.png"); //loads lifeguad image
        img_bullet = new Texture("Bullet.png"); //loads bullet image
        img_enemy = new Texture("Fish.png");
        img_background = new Texture("Background.png");
        lifeguard = new Lifeguard(img_lifeguard, img_bullet, Color.BLUE); //creates lifeguard + bullet, bullet color is blue

        // Changed to enemy manager to modularize
        enemyManager = new EnemyManager(img_enemy);
        // Initialize enemy formation with level 1
        enemyManager.createFormation(currentLevel);

        // Jimi - I'd recommend moving all the Game Over Screen code to its own function and calling it from right here instead of having
        // all the code inside create(). This will give us more organization and cleaner code.

        // Game Over Screen:
        gameOverStage = new Stage(new ScreenViewport());
        // Reuse the helper to make buttons:
        ImageButton playAgainBtn = makeButton("button/play-button-1.png", "button/play-pressed-button-1.png");
        ImageButton quitBtnGo = makeButton("button/quit-button-1.png", "button/quit-pressed-button-1.png");
        ImageButton returnMenuBtn = makeButton("button/return-button.png", "button/return-pressed-button.png");

        // Functions when user click them:
        playAgainBtn.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               resetGame();
               showGameOver = false;
               Gdx.input.setInputProcessor(null);
               button_click_sound.play();
           }
        });

        quitBtnGo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button_click_sound.play();
                Gdx.app.exit();
            }
        });

        returnMenuBtn.addListener(new ClickListener() {
           @Override
            public void clicked(InputEvent event, float x, float y) {
               button_click_sound.play();
               resetGame();

               // flip screens
               showGameOver = false;
               showingMenu = true;

               // in case it was disposed when we hit Play
               mainMenu();

               //Gdx.input.setInputProcessor(menuStage);
           }

        });

        // Organizing the buttons:
        Table goTable = new Table();
        goTable.setFillParent(true);
        // Background image for Game Over
        gameOverBgTex = new Texture("GameOver_Bg.png");
        gameOverBgTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear); // smoother scaling
        goTable.setBackground(new TextureRegionDrawable(new TextureRegion(gameOverBgTex)));

        // keeping content at the top/center over the background
        goTable.top().center();

        // Game Over title image
        gameOverTitleTex = new Texture("GameOver_Title.png"); // use .png (or your real extension)
        Image titleImg = new Image(new TextureRegionDrawable(new TextureRegion(gameOverTitleTex)));
        titleImg.setScaling(Scaling.fit); // keep aspect ratio

        goTable.top().center(); // top center the content
        goTable.add(titleImg).padTop(20).padBottom(10).row(); // add some spacing above/below title

        goTable.add(playAgainBtn).width(200).height(80).pad(10).row();
        goTable.add(quitBtnGo).width(200).height(80).pad(10).row();
        goTable.add(returnMenuBtn).width(200).height(80).pad(10).row();

        gameOverStage.addActor(goTable);
    }

    @Override
    public void render() {

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            menuStage.act(Gdx.graphics.getDeltaTime());
            menuStage.draw();
        } else {
            //
            float deltaTime = Gdx.graphics.getDeltaTime();

            // Move the enemies
            enemyManager.updateEnemyPosition();

            ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
            batch.begin();
            batch.draw(img_background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            lifeguard.Draw(batch);

            // Detect bullet hitting enemy
            enemyManager.enemyHit(lifeguard);

            // Bounds checking for current state of enemy formation
            enemyManager.updateEnemyBounds();


            // Regenerates enemies when all are destroyed
            if (enemyManager.allDead()) {
                enemyManager.createFormation(++currentLevel);

                batch.end();
                return;
            }

            //Bounds checking to ensure enemies do not exit the RIGHT or LEFT side of the screen
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
        }
    }

                    for (int i = 0; i < enemies.length; i++) {
                        if (enemies[i].alive) {
                            enemies[i].Draw(batch);
                            //App closes (game ends) when enemy touches the lifeguard. Fix to show "game over" and score achieved.
                            //If score > highscore, update highscore.
                            // If enemy touches the lifeguard, show Game Over.
                            if (enemies[i].enemy_sprite.getBoundingRectangle().overlaps(lifeguard.lifeguard_sprite.getBoundingRectangle())) {
                                triggerGameOver();
                                batch.end();   // batch was begun earlier
                                return;        // stop the rest of render() for this frame
                            }
                        }
                    }

                    batch.end();
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

    // Helper methods for GameOver screen:
    private void triggerGameOver(){
        showGameOver = true;
        Gdx.input.setInputProcessor(gameOverStage);
    }

    private void resetGame(){
        currentLevel = 1;
        enemyManager.resetEnemies();

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

        ImageButton playButton = makeButton("button/play-button-1.png", "button/play-pressed-button-1.png");

        // Listener listens for the click on the button.
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button_click_sound.play();
                clearTables();
                paused = false;         // Game is not paused.
                running = true;         // Game is running.
                showingMenu = false;    // Closes menu.
            }
        });

        ImageButton quitButton = makeButton("button/quit-button-1.png", "button/quit-pressed-button-1.png");
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button_click_sound.play();
                Gdx.app.exit();     // Closes the application from the menu.
            }
        });

        ImageButton settingsButton = makeButton("button/settings-button-1.png", "button/settings-pressed-button-1.png");
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button_click_sound.play();
                System.out.println("Settings button clicked!");
                paused = true;
                settingsMenu();
            }
        });

        // Don't have a listener for this button at this time.
        ImageButton leaderboardButton = makeButton("button/leaderboard-button-1.png", "button/leaderboard-pressed-button-1.png");

        // Build the table of buttons.
        mainMenuTable = new Table();
        mainMenuTable.setFillParent(true);
        mainMenuTable.center();

        // Add the buttons to the table.
        mainMenuTable.add(playButton).width(200).height(80).pad(10).row();
        mainMenuTable.add(quitButton).width(200).height(80).pad(10).row();
        mainMenuTable.add(settingsButton).width(200).height(80).pad(10).row();
        mainMenuTable.add(leaderboardButton).width(200).height(80).pad(10).row();

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

        // Build the buttons and their listeners. (Only 1 right now :) )
        ImageButton backButton = makeButton("button/back-button.png", "button/back-pressed-button.png");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button_click_sound.play();
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
                button_click_sound.play(); // click sound

                // Flip music state: if On then Off, and vice versa.
                musicEnabled = !musicEnabled;

                // If music is now enabled, start or resume playing it
                if (musicEnabled) {
                    if (!music.isPlaying()) music.play(); // Resume if it was paused

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
        settingsTable.add(musicBtn).width(200).height(80).pad(10).row();
        settingsTable.add(backButton).width(200).height(80).pad(10).row();

        // Add the actor (button) to the stage.
        menuStage.addActor(settingsTable);

        // Event handler for the input on the stage.
        Gdx.input.setInputProcessor(menuStage);
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
        if (gameOverStage != null) gameOverStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        //Dispose of sprites
        img_lifeguard.dispose();
        img_bullet.dispose();
        img_enemy.dispose();
        img_background.dispose();
        if (gameOverTitleTex != null) gameOverTitleTex.dispose();
        if (gameOverStage != null) gameOverStage.dispose();
        if (gameOverBgTex != null) gameOverBgTex.dispose();
        if (soundOnTex != null)  soundOnTex.dispose();
        if (soundOnPressedTex != null) soundOnPressedTex.dispose();
        if (soundOffTex != null)  soundOffTex.dispose();
        if (soundOffPressedTex != null) soundOffPressedTex.dispose();
        if (music != null) music.dispose();
    }
}


