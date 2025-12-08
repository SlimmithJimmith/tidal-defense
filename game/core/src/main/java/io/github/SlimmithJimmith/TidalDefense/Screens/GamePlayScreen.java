/**
 * GamePlayScreen.java
 * Builds and manages the Game Play (enemy movement, score keeping, pause button).
 *
 * @author Team #2 - Brendan Boyko, Jimi Ruble, Mehdi Khazaal, James Watson
 * @version 1.1
 * Create Date: 11-29-2025
 */

package io.github.SlimmithJimmith.TidalDefense.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.SlimmithJimmith.TidalDefense.*;
import io.github.SlimmithJimmith.TidalDefense.Enemy.EnemyManager;
import io.github.SlimmithJimmith.TidalDefense.Player.Lifeguard;
import io.github.SlimmithJimmith.TidalDefense.Player.PowerUp;

public class GamePlayScreen extends BaseScreen {

    private Lifeguard lifeguard;
    private EnemyManager enemyManager;
    private PowerUp powerUp;
    private boolean powerUpReady = true;

    private int score = 0;
    private int currentLevel = 1;

    private Stage hudStage;
    private Label scoreLabel;

    private boolean paused = false;
    private Table pauseMenu;

    public GamePlayScreen(TidalDefenseGame game) {
        super(game);

        // Create score/level keeping in top left corner
        createHUD();

        // Create Game Objects
        lifeguard = new Lifeguard(game.img_lifeguard, game.img_bullet, Color.BLUE, game.bullet_sound);
        enemyManager = new EnemyManager();
        enemyManager.createFormation(currentLevel);
    }
    private void showPauseMenu() {
        if (pauseMenu != null) pauseMenu.remove();

        pauseMenu = new Table();
        pauseMenu.setFillParent(true);
        pauseMenu.center();

        // Semi-transparent background
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888
        );
        pixmap.setColor(0, 0, 0, 0.7f);  // Black with 70% opacity
        pixmap.fill();
        Texture dimBg = new Texture(pixmap);
        pixmap.dispose();

        pauseMenu.setBackground(new TextureRegionDrawable(new TextureRegion(dimBg)));

        // Paused label
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Pixel Game.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 60;
        parameter.color = Color.WHITE;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        Label pauseLabel = new Label("PAUSED", style);
        pauseMenu.add(pauseLabel).padBottom(30).row();

        // Resume button
        ImageButton resumeBtn = makeButton("button/play-btn-up.png", "button/play-btn-down.png");
        resumeBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.button_click_sound.play(game.getVolume());
                paused = false;
                pauseMenu.remove();
            }
        });
        pauseMenu.add(resumeBtn).width(300).height(80).padBottom(10).row();

        // Settings button
        ImageButton settingsBtn = makeButton("button/settings-btn-up.png", "button/settings-btn-down.png");
        settingsBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.button_click_sound.play(game.getVolume());
                // Pass THIS GamePlayScreen so we can return to it
                game.setScreen(new PauseSettingsScreen(game, GamePlayScreen.this));
            }
        });
        pauseMenu.add(settingsBtn).width(300).height(80).padBottom(10).row();

        // Quit to main menu button
        ImageButton quitBtn = makeButton("button/return-btn-up.png", "button/return-btn-down.png");
        quitBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.button_click_sound.play(game.getVolume());
                game.setScreen(new MainMenuScreen(game));
            }
        });
        pauseMenu.add(quitBtn).width(300).height(80).row();

        hudStage.addActor(pauseMenu);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(hudStage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw batch but no actions take place when paused
        if (paused) {
            batch.begin();
            batch.draw(game.img_background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            lifeguard.Draw(batch);
            enemyManager.drawBatch(batch);

            if (powerUp != null) {
                powerUp.Draw(batch);
            }
            batch.end();

            hudStage.act(delta);
            hudStage.draw();
            return;
        }

        // Un-paused, continue with normal game play
        // Move enemies
        enemyManager.updateEnemyPosition();
        enemyManager.updateEnemyBounds();

        batch.begin();
        batch.draw(game.img_background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        lifeguard.Draw(batch);

        // If enemy hit, add enemy's value to score
        score += enemyManager.enemyHit(lifeguard, game.enemy_death);
        scoreLabel.setText("Score: " + score + "\nLevel: " + currentLevel);

        // Level complete
        if (enemyManager.allDead()) {
            enemyManager.createFormation(++currentLevel);

            // Determine if power up is ready to drop
            if (powerUpReady && (currentLevel - 1) % 3 == 0) {
                powerUp = new PowerUp();
                powerUpReady = false;
            }
            if ((currentLevel - 1) % 3 != 0) {
                powerUpReady = true;
            }
            batch.end();
            return;
        }

        // Power up creation, disposal, movement logic
        if (powerUp != null) {
            if (powerUp.playerCollision(lifeguard)) {
                lifeguard.PowerUp();
                powerUp.dispose();
                powerUp = null;
            } else if (powerUp.powerUpPastPlayer()) {
                powerUp.dispose();
                powerUp = null;
            } else {
                powerUp.updatePowerUpPosition();
                powerUp.Draw(batch);
            }
        }

        enemyManager.enemyBoundsCheck();
        enemyManager.drawBatch(batch);

        // Game over check (player defeated)
        if (enemyManager.playerCollision(lifeguard) || enemyManager.enemyPastPlayer()) {
            batch.end();
            game.setScreen(new GameOverScreen(game, score));
            return;
        }

        // Boss level
        if (currentLevel % 7 == 0) {
            enemyManager.updateOctoMinis(delta);
            score += enemyManager.octoMiniHit(lifeguard, game.enemy_death);

            if (enemyManager.octoMiniPlayerCollision(lifeguard)) {
                batch.end();
                game.setScreen(new GameOverScreen(game, score));
                return;
            }
        }

        batch.end();

        // Draw HUD
        hudStage.act(delta);
        hudStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        hudStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        hudStage.dispose();
    }

    // Create HUD for score keeping, level tracking, and the pause button
    void createHUD() {
        // Font
        hudStage = new Stage(new ScreenViewport());
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Pixel Game.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.color = Color.WHITE;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        scoreLabel = new Label("Score: " + score, style);
        scoreLabel.setPosition(20, Gdx.graphics.getHeight() - 60);
        hudStage.addActor(scoreLabel);

        ImageButton pauseBtn = makeButton("button/ham-btn-up.png", "button/ham-btn-down.png");
        pauseBtn.setPosition(25, 0);
        pauseBtn.setWidth(60);
        pauseBtn.setHeight(100);
        pauseBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.button_click_sound.play(game.getVolume());
                paused = !paused;
                if (paused) {
                    showPauseMenu();
                }
            }
        });


        hudStage.addActor(pauseBtn);
    }

    /**
     * Helper method to build buttons from texture files.
     *
     * @param upFile unpressed look for the button
     * @param downFile pressed look for the button
     * @return ImageButton backed by those textures
     */
    private ImageButton makeButton(String upFile, String downFile) {
        Texture upTex = new Texture(Gdx.files.internal(upFile));
        Texture downTex = new Texture(Gdx.files.internal(downFile));

        TextureRegionDrawable upDrawable = new TextureRegionDrawable(new TextureRegion(upTex));
        TextureRegionDrawable downDrawable = new TextureRegionDrawable(new TextureRegion(downTex));

        return new ImageButton(upDrawable, downDrawable);
    }
}


