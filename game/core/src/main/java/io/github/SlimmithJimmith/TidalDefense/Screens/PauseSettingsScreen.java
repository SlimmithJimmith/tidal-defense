/**
 * PauseSettingScreen.java
 * Builds and manages the GAMEPLAY PAUSE Settings screen UI (music toggle + volume slider + back button) .
 *
 * @author Team #2 - Brendan Boyko, Jimi Ruble, Mehdi Khazaal, James Watson
 * @version 1.1
 * Create Date: 11-29-2025
 */
package io.github.SlimmithJimmith.TidalDefense.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.SlimmithJimmith.TidalDefense.TidalDefenseGame;

public class PauseSettingsScreen extends BaseScreen {
    private Stage stage;
    private SettingsMenu settingsMenuUI;
    private GamePlayScreen returnToGameScreen;

    public PauseSettingsScreen(TidalDefenseGame game, GamePlayScreen gamePlayScreen) {
        super(game);
        this.returnToGameScreen = gamePlayScreen;
        stage = new Stage(new ScreenViewport());

        settingsMenuUI = new SettingsMenu(
            stage,
            game.img_background,
            game.backgroundMusic,
            game.button_click_sound,
            game,
            () -> game.setScreen(returnToGameScreen)
        );
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        settingsMenuUI.show();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        settingsMenuUI.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        settingsMenuUI.dispose();
        stage.dispose();
    }
}
