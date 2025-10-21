package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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

    private Stage menuStage;
    private boolean showingMenu = true;
    private Table settingsTable;
    private boolean showSettings = false;

    // GameOver stage:
    private Stage gameOverStage;
    private boolean showGameOver = false;
    private Texture gameOverTitleTex;
    private Texture gameOverBgTex;

    @Override
    public void create() {
        menuStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(menuStage);

//        Helper method creates the buttons
        ImageButton playButton = makeButton("button/play-button-1.png", "button/play-pressed-button-1.png");
        ImageButton quitButton = makeButton("button/quit-button-1.png", "button/quit-pressed-button-1.png");
        ImageButton settingsButton = makeButton("button/settings-button-1.png", "button/settings-pressed-button-1.png");

        //Listener listens for the click on the button
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showingMenu = false;    //Closes menu
                menuStage.dispose();    //Removes button actions from screen
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();     //Closes the application from the menu
            }
        });

        //Build the table of buttons
        Table mainMenuTable = new Table();
        mainMenuTable.setFillParent(true);
        mainMenuTable.center();

        mainMenuTable.add(playButton).width(200).height(80).pad(10).row();
        mainMenuTable.add(quitButton).width(200).height(80).pad(10).row();
        mainMenuTable.add(settingsButton).width(200).height(80).pad(10).row();

        menuStage.addActor(mainMenuTable);

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

        // Game Over Screen:
        gameOverStage = new Stage(new ScreenViewport());
        // Reuse the helper to make buttons:
        ImageButton playAgainBtn = makeButton("button/play-button-1.png", "button/play-pressed-button-1.png");
        ImageButton quitBtnGo = makeButton("button/quit-button-1.png", "button/quit-pressed-button-1.png");
        ImageButton returnMenuBtn = makeButton("button/settings-button-1.png", "button/settings-pressed-button-1.png");

        // Functions when user click them:
        playAgainBtn.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               resetGame();
               showGameOver = false;
               Gdx.input.setInputProcessor(null);
           }
        });

        quitBtnGo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        returnMenuBtn.addListener(new ClickListener() {
           @Override
            public void clicked(InputEvent event, float x, float y) {
               resetGame();

               // flip screens
               showGameOver = false;
               showingMenu = true;

               // in case it was disposed when we hit Play
               buildMainMenu();

               Gdx.input.setInputProcessor(menuStage);
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

        if(showGameOver){
            gameOverStage.act(Gdx.graphics.getDeltaTime());
            gameOverStage.draw();
            return; // skip running the game while Game Over is showing
        }

        if (showingMenu) {
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



    /*
    Try/catch is used to catch and display runtime errors when building the buttons
     */
    //Helper method to build the buttons using their texture files
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

    private void buildMainMenu() {
        // First dispose old stages, to avoid leaks
        if(menuStage != null){
            try {menuStage.dispose();
            }
            catch (Exception ignored){}
        }

        menuStage = new Stage(new ScreenViewport());

        ImageButton playButton = makeButton("button/play-button-1.png", "button/play-pressed-button-1.png");
        ImageButton quitButton = makeButton("button/quit-button-1.png", "button/quit-pressed-button-1.png");
        ImageButton settingsButton = makeButton("button/settings-button-1.png", "button/settings-pressed-button-1.png");

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showingMenu = false;
                // optional: dispose the menu now that we're entering gameplay
                menuStage.dispose();
                // (we don’t need to set to null; we rebuild via buildMainMenu() when needed)
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Table mainMenuTable = new Table();
        mainMenuTable.setFillParent(true);
        mainMenuTable.center();
        mainMenuTable.add(playButton).width(200).height(80).pad(10).row();
        mainMenuTable.add(quitButton).width(200).height(80).pad(10).row();
        mainMenuTable.add(settingsButton).width(200).height(80).pad(10).row();

        menuStage.addActor(mainMenuTable);
    }

    //Buttons will not distort when the screen is resized.
    @Override
    public void resize(int width, int height) {
//        menuStage.getViewport().update(width, height, true);
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
    }
}


