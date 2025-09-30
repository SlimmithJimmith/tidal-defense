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

//Test test test

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture img_lifeguard;
    private Texture img_bullet;
    private Texture img_enemy;
    private Texture img_background;
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


    private Stage menuStage;
    private boolean showingMenu = true;

    @Override
    public void create() {
        menuStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(menuStage);

        //Helper method creates the buttons
        ImageButton playButton = makeButton("button/play-button-1.png", "button/play-pressed-button-1.png");
        ImageButton quitButton = makeButton("button/quit-button-1.png", "button/quit-pressed-button-1.png");

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
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(playButton).width(200).height(80).pad(10).row();
        table.add(quitButton).width(200).height(80).pad(10).row();

        menuStage.addActor(table);

        offset_enemies = new Vector2(0,0);
        batch = new SpriteBatch();
        img_lifeguard = new Texture("LifeguardShootingUp.png"); //loads lifeguad image
        img_bullet = new Texture("Bullet.png"); //loads bullet image
        img_enemy = new Texture("Fish.png");
        img_background = new Texture("Background.png");
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

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (showingMenu) {
            menuStage.act(Gdx.graphics.getDeltaTime());
            menuStage.draw();
        } else {
            //
            float deltaTime = Gdx.graphics.getDeltaTime();

            //Moves the enemies
            offset_enemies.x += direction_enemies * deltaTime * speed_enemies;

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
                if (enemies[i].alive) {
                    if (lifeguard.bullet_sprite.getBoundingRectangle().overlaps(enemies[i].enemy_sprite.getBoundingRectangle())) {
                        lifeguard.position_bullet.y = 100000;
                        enemies[i].alive = false;
                        break;
                    }
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
        batch.draw(img_background, 0, 0);
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

            //Regenerates enemies when all are destroyed
            if (amount_alive_enemies == 0) {
                for (int i = 0; i < enemies.length; i++) {
                    enemies[i].alive = true;
                }
                offset_enemies = new Vector2(0, 0);
                batch.end();
                return;
            }

            //Bounds checking to ensure enemies do not exit the RIGHT side of the screen
            float rightLimit = Gdx.graphics.getWidth() - enemies[0].enemy_sprite.getWidth();
            if (enemies[maxX_enemies].position.x >= rightLimit) {
                direction_enemies = -1;
                //Makes the enemies go down towards the lifeguard
                offset_enemies.y -= enemies[0].enemy_sprite.getHeight() * enemies[0].enemy_sprite.getScaleY() * 0.25f;
                //Makes the speed of the enemies increase over time.
                speed_enemies += 0.1f;
            }

            //Bounds checking to ensure enemies do not exit the LEFT side of the screen
            if (enemies[minX_enemies].position.x <= 0f) {
                direction_enemies = 1;
                //Makes the enemies go down towards the lifeguard
                offset_enemies.y -= enemies[0].enemy_sprite.getHeight() * enemies[0].enemy_sprite.getScaleY() * 0.25f;
                //Makes the speed of the enemies increase over time.
                speed_enemies += 0.1f;
            }

            //If enemies go past bottom of the screen, exit the app.
            //We need to update this so that it shows "game over" and score achived. If score > highscore, update highscore.
            if (enemies[minY_enemies].position.y <= 0) {
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

    //Buttons will not distort when the screen is resized.
    @Override
    public void resize(int width, int height) {
        menuStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        //Dispose of sprites
        img_lifeguard.dispose();
        img_bullet.dispose();
        img_enemy.dispose();
        img_background.dispose();
    }
}


