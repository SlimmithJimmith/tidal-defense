/**
 * SettingsMenu.java
 * Builds and manages the Settings screen UI (music toggle + volume slider + back button).
 *
 * @author Team #2 - Brendan Boyko, Jimi Ruble, Mehdi Khazaal, James Watson
 * @version 1.1
 * Create Date: 11-29-2025
 */

package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * SettingsMenu builds the Settings screen and handles
 * toggling music on/off, adjusting volume, and back to the main menu.
 */
public class SettingsMenu {

    // Shared stage from Core
    private final Stage menuStage;

    // Background image (same as main menu)
    private final Texture img_background;

    // Audio
    private final Music music;
    private final Sound button_click_sound;

    // Reference to Core to read/update volume and music state
    private final Core core;

    // Callback to go back to main menu (Core.mainMenu())
    private final Runnable onBackToMainMenu;

    // Textures for the music toggle button
    private Texture soundOnTex, soundOnPressedTex, soundOffTex, soundOffPressedTex;

    // Drawables for Scene2D button states
    private TextureRegionDrawable soundOn, soundOnPressed, soundOff, soundOffPressed;

    // Settings table
    private Table settingsTable;

    // Volume slider pieces
    private Slider volumeSlider;
    private Texture sliderBgTex, sliderKnobTex;
    private TextureRegionDrawable sliderBgDrawable, sliderKnobDrawable;



    /**
     * Constructor wires the SettingsMenu to the shared Stage,
     * background texture, music, button click sound, Core reference,
     * and callback.
     *
     * @param menuStage the Stage used for menu screens
     * @param img_background background texture
     * @param music background music instance
     * @param button_click sound effect for button clicks
     * @param core reference to Core for volume and music flags
     * @param onBackToMainMenu callback to return to the main menu
     */
    public SettingsMenu(Stage menuStage, Texture img_background, Music music, Sound button_click, Core core, Runnable onBackToMainMenu) {

        this.menuStage = menuStage;
        this.img_background = img_background;
        this.music = music;
        this.button_click_sound = button_click;
        this.core = core;
        this.onBackToMainMenu = onBackToMainMenu;

        // Load the image files for each button case.
        soundOnTex = new Texture(Gdx.files.internal("button/music-on-btn-up.png"));
        soundOnPressedTex = new Texture(Gdx.files.internal("button/music-on-btn-down.png"));
        soundOffTex = new Texture(Gdx.files.internal("button/music-off-btn-up.png"));
        soundOffPressedTex = new Texture(Gdx.files.internal("button/music-off-btn-down.png"));

        sliderBgTex = new Texture(Gdx.files.internal("button/slider2.png"));
        sliderKnobTex = new Texture(Gdx.files.internal("button/knob2.png"));

        // Wrap each texture into a TextureRegionDrawable
        soundOn = new TextureRegionDrawable(new TextureRegion(soundOnTex));
        soundOnPressed = new TextureRegionDrawable(new TextureRegion(soundOnPressedTex));
        soundOff = new TextureRegionDrawable(new TextureRegion(soundOffTex));
        soundOffPressed = new TextureRegionDrawable(new TextureRegion(soundOffPressedTex));

        // Volume Slider
        sliderBgDrawable = new TextureRegionDrawable(new TextureRegion(sliderBgTex));
        sliderKnobDrawable = new TextureRegionDrawable(new TextureRegion(sliderKnobTex));
    }

    public void render(float delta) {
        menuStage.act(delta);
        menuStage.draw();
    }

    /**
     * Shows the Settings screen on the shared menuStage.
     * Builds the music toggle button, volume slider, and Back button.
     */
    public void show() {
        // Clear any existing actors
        menuStage.clear();

        Image menuBg = new Image(new TextureRegionDrawable(new TextureRegion(img_background)));
        menuBg.setFillParent(true);
        menuStage.addActor(menuBg);

        // Build the Back button and its listener.
        ImageButton backButton = makeButton("button/back-btn-up.png", "button/back-btn-down.png");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button_click_sound.play(core.getVolume());
                if (onBackToMainMenu != null) {
                    onBackToMainMenu.run();
                }
            }
        });

        // Music toggle button (icon depends on current state from Core)
        boolean musicEnabled = core.isMusicEnabled();
        ImageButton musicBtn = new ImageButton(
            musicEnabled ? soundOn : soundOff,
            musicEnabled ? soundOnPressed : soundOffPressed
        );

        musicBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button_click_sound.play(core.getVolume()); // click sound

                // Flip music state: if On then Off, and vice versa.
                boolean enabled = !core.isMusicEnabled();
                core.setMusicEnabled(enabled);

                // If music is now enabled, start or resume playing it
                if (enabled) {
                    if (!music.isPlaying()) {
                        music.setVolume(core.getVolume());
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

        // Build a SliderStyle using the Pixmap-based bar and knob
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = sliderBgDrawable;
        sliderStyle.knob = sliderKnobDrawable;

        // Slider ranges from 0.0 (mute) to 1.0 (full volume)
        volumeSlider = new Slider(0f, 1f, 0.05f, false, sliderStyle);
        volumeSlider.setValue(core.getVolume()); // sync with current volume

        // When the slider moves, update Core volume and music volume.
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float newVolume = volumeSlider.getValue();
                core.setVolume(newVolume);
                if (music != null) {
                    music.setVolume(newVolume);
                }
            }
        });

        // Build the table of buttons.
        settingsTable = new Table();
        settingsTable.setFillParent(true);
        settingsTable.center();

        // Add the widgets to the table.
        // Order: Music toggle, Volume slider, Back button.
        settingsTable.add(musicBtn).width(300).height(80).row();
        settingsTable.add(volumeSlider).width(300).height(50).padTop(20).row();
        settingsTable.add(backButton).width(300).height(80).padTop(20).row();

        // Add the table to the stage
        menuStage.addActor(settingsTable);

        // Event handler for input on the stage
        Gdx.input.setInputProcessor(menuStage);
    }

    /**
     * Helper method to build buttons from texture files.
     *
     * @param upFile unpressed look for the button
     * @param downFile pressed look for the button
     * @return ImageButton backed by those textures
     */
    private ImageButton makeButton(String upFile, String downFile) {
        try {
            Texture upTex = new Texture(Gdx.files.internal(upFile));
            Texture downTex = new Texture(Gdx.files.internal(downFile));

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

    /**
     * Dispose of textures owned by the SettingsMenu.
     * Called from Core.dispose().
     */
    public void dispose() {
        if (soundOnTex != null) soundOnTex.dispose();
        if (soundOnPressedTex != null) soundOnPressedTex.dispose();
        if (soundOffTex != null) soundOffTex.dispose();
        if (soundOffPressedTex != null) soundOffPressedTex.dispose();
        if (sliderBgTex != null) sliderBgTex.dispose();
        if (sliderKnobTex != null) sliderKnobTex.dispose();
    }
}
