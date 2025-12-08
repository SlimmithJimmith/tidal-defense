/**
 * GameOverScreen.java
 * Builds and manages the Game Over screen UI (Save score, play again, return to main menu, quit).
 *
 * @author Team #2 - Brendan Boyko, Jimi Ruble, Mehdi Khazaal, James Watson
 * @version 1.1
 * Create Date: 11-29-2025
 */

package io.github.SlimmithJimmith.TidalDefense.Screens;
import io.github.SlimmithJimmith.TidalDefense.GameOver;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.SlimmithJimmith.TidalDefense.*;

public class GameOverScreen extends BaseScreen {
    private Stage stage;
    private int finalScore;
    private GameOver gameOverUI;

    public GameOverScreen(TidalDefenseGame game, int score) {
        super(game);
        this.finalScore = score;
        stage = new Stage(new ScreenViewport());

        gameOverUI = new GameOver(
            game.leaderboard,
            game.button_click_sound,
            game.getVolume(),
            // Play again
            () -> game.setScreen(new GamePlayScreen(game)),
            // Quit
            () -> Gdx.app.exit(),
            // Return to menu
            () -> game.setScreen(new MainMenuScreen(game))
        );

        gameOverUI.setScore(finalScore);
        gameOverUI.resetToGameOverScreen();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(gameOverUI.getStage());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameOverUI.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        gameOverUI.resize(width, height);
    }

    @Override
    public void dispose() {
        gameOverUI.dispose();
    }
}
