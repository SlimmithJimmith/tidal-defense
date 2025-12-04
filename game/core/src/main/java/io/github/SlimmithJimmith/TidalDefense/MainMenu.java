package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

/**
 *
 */
public class MainMenu {
    private final Stage menuStage;
    private final Texture img_background;
    private final Sound button_click_sound;
    private final float volume;
    private Table mainMenuTable;
    private final Runnable toSettingsMenu, playGame, toQuit;
    private boolean paused;
    boolean isVisible;

    /**
     * Constructor wires the MainMenu to the shared Stage,
     * background texture, music, button click sound, Core reference,
     * and callback.
     *
     * @param menuStage Stage used for the menus
     * @param img_background background image
     * @param button_click_sound button click sounds
     * @param volume volume for button click sound
     * @param core reference to Core
     * @param toPlayGame callback to start the game
     * @param toQuit callback to quit the application
     * @param toSettingsMenu callback to call the settings menu
     */
    public MainMenu(Stage menuStage,
                    Texture img_background,
                    Sound button_click_sound,
                    float volume, Core core,
                    Runnable toPlayGame,
                    Runnable toQuit,
                    Runnable toSettingsMenu) {
        this.menuStage = menuStage;
        this.img_background = img_background;
        this.button_click_sound = button_click_sound;
        this.volume = volume;
        this.playGame = toPlayGame;
        this.toQuit = toQuit;
        this.toSettingsMenu = toSettingsMenu;

        showMainMenu();
    }

    /**
     * showMainMenu builds the buttons for the menu. Each button has its own
     * listener to "listen" for the click of the button. This will trigger
     * whatever the buttons purpose is.
     */
    public void showMainMenu(){
        //Clear any existing tables.
        clearTables();
        menuStage.clear();

        //Background
        Image menuBg = new Image(new TextureRegionDrawable(new TextureRegion(img_background)));
        menuBg.setFillParent(true);
        menuStage.addActor(menuBg);

        ImageButton playButton = makeButton("button/play-btn-up.png", "button/play-btn-down.png");

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button_click_sound.play(volume);
                clearTables();
                playGame.run();
            }
        });

        ImageButton quitButton = makeButton("button/quit-btn-up.png", "button/quit-btn-down.png");
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button_click_sound.play(volume);
                toQuit.run();     // Closes the application from the menu.
            }
        });

        ImageButton settingsButton = makeButton("button/settings-btn-up.png", "button/settings-btn-down.png");
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button_click_sound.play(volume);
                System.out.println("Settings button clicked!");
                paused = true;
                // Show the Settings screen handled by SettingsMenu class
                if (toSettingsMenu != null) {
                    clearTables();
                    toSettingsMenu.run();
                }
            }
        });

        // Don't have a listener for this button at this time.
        ImageButton leaderboardButton = makeButton("button/lead-btn-up.png", "button/lead-btn-down.png");

        // Build the table of buttons.
        mainMenuTable = new Table();
        mainMenuTable.setFillParent(true);

        mainMenuTable.center().top();
        Texture title = new Texture("title.png");
        Image titleImage = new Image(new TextureRegionDrawable(new TextureRegion(title)));
        titleImage.setScaling(Scaling.fit);

        mainMenuTable.add(titleImage).width(1000).height(300).padBottom(50).row();

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

    /*
    Try/catch is used to catch and display runtime errors when building the buttons
    If you run into any issues and see a white box, there may be a typo in your file name.
     */
    /**
     * Helper method to build the buttons using their texture file.
     *
     * //@param upFile unpressed look for the button
     * //@param downFile pressed look for the button
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
}
