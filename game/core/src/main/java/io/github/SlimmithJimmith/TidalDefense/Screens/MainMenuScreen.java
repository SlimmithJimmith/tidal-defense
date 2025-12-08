/**
 * MainMenuScreen.java
 * Data manager for the buttons and actions of the main menu screen
 *
 * @author Team #2 - Brendan Boyko, Jimi Ruble, Mehdi Khazaal, James Watson
 * @version 1.1
 * Create Date: 11-29-2025
 */

package io.github.SlimmithJimmith.TidalDefense.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.SlimmithJimmith.TidalDefense.TidalDefenseGame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


public class MainMenuScreen extends BaseScreen {
    private Stage stage;
    private Table mainMenuTable;

    public MainMenuScreen(TidalDefenseGame game) {
        super(game);
        stage = new Stage(new ScreenViewport());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        buildMenu();
    }

    private void buildMenu() {
        stage.clear();

        // Background
        Image bg = new Image(game.img_background);
        bg.setFillParent(true);
        stage.addActor(bg);

        // Buttons
        ImageButton playBtn = makeButton("button/play-btn-up.png", "button/play-btn-down.png");
        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.button_click_sound.play(game.getVolume());
                game.setScreen(new GamePlayScreen(game));
            }
        });

        ImageButton settingBtn = makeButton("button/settings-btn-up.png", "button/settings-btn-down.png");
        settingBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.button_click_sound.play(game.getVolume());
                game.setScreen(new SettingsScreen(game));
            }
        });

        ImageButton leaderboardBtn = makeButton("button/lead-btn-up.png", "button/lead-btn-down.png");
        leaderboardBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.button_click_sound.play(game.getVolume());
                game.setScreen(new LeaderboardScreen(game));
            }
        });

        ImageButton quitBtn = makeButton("button/quit-btn-up.png", "button/quit-btn-down.png");
        quitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.button_click_sound.play(game.getVolume());
                Gdx.app.exit();
            }
        });

        // Table layout
        mainMenuTable = new Table();
        mainMenuTable.setFillParent(true);
        mainMenuTable.center().top();

        Texture titleTex = new Texture("title.png");
        mainMenuTable.add(new Image(titleTex)).width(1000).height(300).padBottom(50).padTop(100).row();
        mainMenuTable.add(playBtn).width(300).height(80).row();
        mainMenuTable.add(settingBtn).width(300).height(80).row();
        mainMenuTable.add(leaderboardBtn).width(300).height(80).row();
        mainMenuTable.add(quitBtn).width(300).height(80).row();

        stage.addActor(mainMenuTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    /*
    Try/catch is used to catch and display runtime errors when building the buttons
    If you run into any issues and see a white box, there may be a typo in your file name.
     */

    /**
     * Helper method to build the buttons using their texture file.
     * <p>
     * //@param upFile unpressed look for the button
     * //@param downFile pressed look for the button
     *
     * @return drawable button
     */
    private ImageButton makeButton(String upFile, String downFile) {
        //Load textures for the play button
        try {
            Texture upTex = new Texture(Gdx.files.internal(upFile));
            Texture downTex = new Texture(Gdx.files.internal(downFile));

            //Create Drawables for play button
            TextureRegionDrawable upDrawable = new TextureRegionDrawable(new TextureRegion(upTex));
            TextureRegionDrawable downDrawable = new TextureRegionDrawable(new TextureRegion(downTex));

            return new ImageButton(upDrawable, downDrawable);

        } catch (Exception e) {
            e.printStackTrace();
            Pixmap pixmap = new Pixmap(100, 50, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            Texture fallbackTex = new Texture(pixmap);
            TextureRegionDrawable fallbackDrawable = new TextureRegionDrawable(new TextureRegion(fallbackTex));
            return new ImageButton(fallbackDrawable, fallbackDrawable);
        }
    }
}
