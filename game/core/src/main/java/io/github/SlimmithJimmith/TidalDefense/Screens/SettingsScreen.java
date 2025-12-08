/**
 * SettingsScreen.java
 * Creates the actual user interface for settings screen when entered through the main menu
 *
 * @author Team #2 - Brendan Boyko, Jimi Ruble, Mehdi Khazaal, James Watson
 * @version 1.0
 * Create Date: 12-04-2025
 */
package io.github.SlimmithJimmith.TidalDefense.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.SlimmithJimmith.TidalDefense.TidalDefenseGame;

public class SettingsScreen extends BaseScreen {
    private Stage stage;
    private SettingsMenu settingMenuUI;

    public SettingsScreen(TidalDefenseGame game) {
        super(game);
        stage = new Stage(new ScreenViewport());

        settingMenuUI = new SettingsMenu(
            stage,
            game.img_background,
            game.backgroundMusic,
            game.button_click_sound,
            game,
            () -> game.setScreen(new MainMenuScreen(game))
        );
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        settingMenuUI.show();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        settingMenuUI.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        settingMenuUI.dispose();
        stage.dispose();
    }
}
