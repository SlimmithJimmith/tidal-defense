/**
 * BaseScreen.java
 * Abstract class, implements Screen from LibGDX, and is inherited by the various screens for this game.
 *
 * @author Team #2 - Brendan Boyko, Jimi Ruble, Mehdi Khazaal, James Watson
 * @version 1.1
 * Create Date: 11-29-2025
 */

package io.github.SlimmithJimmith.TidalDefense.Screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SlimmithJimmith.TidalDefense.TidalDefenseGame;

public abstract class BaseScreen implements Screen {
    protected final TidalDefenseGame game;
    protected final SpriteBatch batch;

    public BaseScreen(TidalDefenseGame game) {
        this.game = game;
        this.batch = game.batch;
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {}

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
}
