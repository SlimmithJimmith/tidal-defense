/**
 * LeaderboardScreen.java
 * Builds and manages the Leaderboard screen UI, specifically the leaderboard when entered through the main menu screen.
 *
 * @author Team #2 - Brendan Boyko, Jimi Ruble, Mehdi Khazaal, James Watson
 * @version 1.1
 * Create Date: 11-29-2025
 */

package io.github.SlimmithJimmith.TidalDefense.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.SlimmithJimmith.TidalDefense.Scoreboard;
import io.github.SlimmithJimmith.TidalDefense.TidalDefenseGame;

public class LeaderboardScreen extends BaseScreen {
    private Stage stage;

    public LeaderboardScreen(TidalDefenseGame game) {
        super(game);
        stage = new Stage(new ScreenViewport());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        buildLeaderboardUI();
    }

    private void buildLeaderboardUI() {
        stage.clear();

        // Background
        Image bg = new Image(new TextureRegionDrawable(new TextureRegion(game.img_background)));
        bg.setFillParent(true);
        stage.addActor(bg);

        // Main table
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().center();

        // Title
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Pixel Game.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.color = Color.WHITE;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.WHITE);

        Label titleLabel = new Label("LEADERBOARD", titleStyle);
        mainTable.add(titleLabel).padTop(40).padBottom(30).row();

        // Leaderboard table
        Table leaderboardTable = buildLeaderboardTable();
        mainTable.add(leaderboardTable).padBottom(30).center().row();

        // Back button
        ImageButton backBtn = makeButton("button/back-btn-up.png", "button/back-btn-down.png");
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.button_click_sound.play(game.getVolume());
                game.setScreen(new MainMenuScreen(game));
            }
        });

        mainTable.add(backBtn).width(300).height(80).row();

        stage.addActor(mainTable);
    }

    private Table buildLeaderboardTable() {
        Table table = new Table();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Pixel Game.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.color = Color.WHITE;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

        // Headers
        table.add(new Label("Rank", style)).pad(4).padLeft(10).padRight(20);
        table.add(new Label("Name", style)).pad(4).padLeft(10).padRight(20);
        table.add(new Label("Score", style)).pad(4).padLeft(10).padRight(20);
        table.row();

        // Load and sort scores
        Array<Scoreboard> scores = game.leaderboard.loadAll();
        scores.sort((a, b) -> {
            if (a.score != b.score) return Integer.compare(b.score, a.score);
            return Long.compare(b.when, a.when);
        });

        // Display top 10
        int count = Math.min(10, scores.size);
        for (int i = 0; i < count; i++) {
            Scoreboard score = scores.get(i);
            table.add(new Label(String.valueOf(i + 1), style)).pad(4).padLeft(10).padRight(20);
            table.add(new Label(score.name, style)).pad(4).padLeft(10).padRight(20);
            table.add(new Label(String.valueOf(score.score), style)).pad(4).padLeft(10).padRight(20);
            table.row();
        }

        return table;
    }

    private ImageButton makeButton(String upFile, String downFile) {
        Texture upTex = new Texture(Gdx.files.internal(upFile));
        Texture downTex = new Texture(Gdx.files.internal(downFile));

        TextureRegionDrawable upDrawable = new TextureRegionDrawable(new TextureRegion(upTex));
        TextureRegionDrawable downDrawable = new TextureRegionDrawable(new TextureRegion(downTex));

        return new ImageButton(upDrawable, downDrawable);
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
}
