package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameOver {

    private Stage stage;
    private Texture gameOverTitleTex;
    private Texture gameOverBgTex;

    private final Leaderboard leaderboard;
    private final float volume;
    private final com.badlogic.gdx.audio.Sound buttonClickSound;

    private int lastScore = 0;

    // Callbacks to talk back to Core
    private final Runnable onPlayAgain;
    private final Runnable onQuit;
    private final Runnable onReturnToMenu;

    public GameOver(Leaderboard leaderboard, com.badlogic.gdx.audio.Sound buttonClickSound, float volume, Runnable onPlayAgain, Runnable onQuit, Runnable onReturnToMenu) {

        this.leaderboard = leaderboard;
        this.buttonClickSound = buttonClickSound;
        this.volume = volume;
        this.onPlayAgain = onPlayAgain;
        this.onQuit = onQuit;
        this.onReturnToMenu = onReturnToMenu;

        stage = new Stage(new ScreenViewport());
        buildGameOverMenu();
    }

    /** Called by Core to update the score shown/saved */
    public void setScore(int score) {
        this.lastScore = score;
    }

    /** Called when Core wants to show the Game Over screen */
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    public void resetToGameOverScreen() {
        buildGameOverMenu();
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        if (stage != null) stage.dispose();
        if (gameOverTitleTex != null) gameOverTitleTex.dispose();
        if (gameOverBgTex != null) gameOverBgTex.dispose();
    }

    private ImageButton makeButton(String upFile, String downFile) {
        Texture upTex = new Texture(Gdx.files.internal(upFile));
        Texture downTex = new Texture(Gdx.files.internal(downFile));

        TextureRegionDrawable upDrawable = new TextureRegionDrawable(new TextureRegion(upTex));
        TextureRegionDrawable downDrawable = new TextureRegionDrawable(new TextureRegion(downTex));

        return new ImageButton(upDrawable, downDrawable);
    }

    private void buildGameOverMenu() {
        stage.clear();

        // Buttons
        ImageButton playAgainBtn   = makeButton("button/play-btn-up.png",   "button/play-btn-down.png");
        ImageButton quitBtnGo      = makeButton("button/quit-btn-up.png",   "button/quit-btn-down.png");
        ImageButton returnMenuBtn  = makeButton("button/return-btn-up.png", "button/return-btn-down.png");

        // Background
        Table goTable = new Table();
        goTable.setFillParent(true);

        gameOverBgTex = new Texture("GameOver_Bg.png");
        gameOverBgTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        goTable.setBackground(new TextureRegionDrawable(new TextureRegion(gameOverBgTex)));
        goTable.top().center();

        // Title
        gameOverTitleTex = new Texture("GameOver_Title.png");
        Image titleImg = new Image(new TextureRegionDrawable(new TextureRegion(gameOverTitleTex)));
        titleImg.setScaling(com.badlogic.gdx.utils.Scaling.fit);

        goTable.add(titleImg).padTop(100).padLeft(60).padRight(40).row();

        // Score form
        com.badlogic.gdx.scenes.scene2d.ui.Table scoreForm = new com.badlogic.gdx.scenes.scene2d.ui.Table();


        // Font config
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Pixel Game.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        Label.LabelStyle lblStyle = new Label.LabelStyle(font, Color.WHITE);
        TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle();
        tfStyle.font = font;
        tfStyle.fontColor = Color.WHITE;

        Label nameLabel = new Label("Name:", lblStyle);
        final TextField nameField = new TextField("", tfStyle);
        nameField.setMessageText("AAA");
        nameField.setMaxLength(8);

        scoreForm.add(nameLabel).pad(5).right();
        scoreForm.add(nameField).width(160).pad(5).left();
        scoreForm.row();

        final ImageButton saveBtn = makeButton("button/save-btn-up.png", "button/save-btn-down.png");
        scoreForm.add(saveBtn).colspan(2).width(300).height(80).padTop(8).center();
        scoreForm.row();

        // Save score and show the leaderboard
        saveBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                buttonClickSound.play(volume);

                String playerName = nameField.getText() == null ? "" : nameField.getText().trim();
                if (playerName.isEmpty()) playerName = "AAA";

                Scoreboard row = new Scoreboard(playerName, lastScore, System.currentTimeMillis());
                leaderboard.addScore(row);

                showLeaderboardScreen();
            }
        });

        nameField.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean keyDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, int keycode) {
                if (keycode == com.badlogic.gdx.Input.Keys.ENTER) {
                    saveBtn.toggle();
                    saveBtn.toggle();
                    saveBtn.fire(new com.badlogic.gdx.scenes.scene2d.InputEvent());
                    return true;
                }
                return false;
            }
        });

        goTable.add(scoreForm).padBottom(12).center();
        goTable.row();

        // Buttons: Play Again / Quit / Return Menu
        playAgainBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                buttonClickSound.play(volume);
                onPlayAgain.run();
            }
        });

        quitBtnGo.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                buttonClickSound.play(volume);
                onQuit.run();
            }
        });

        returnMenuBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                buttonClickSound.play(volume);
                onReturnToMenu.run();
            }
        });

        goTable.add(playAgainBtn).width(300).height(80).pad(6).row();
        goTable.add(quitBtnGo).width(300).height(80).pad(6).row();
        goTable.add(returnMenuBtn).width(300).height(80).pad(6).row();

        stage.addActor(goTable);
    }

    // Leaderboard table (to display latest scores in descending order)
    private Table buildLeaderboardTable() {
        Table t = new Table();
        // Font config
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Pixel Game.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        Label.LabelStyle ls = new Label.LabelStyle(font, Color.WHITE);

        t.add(new Label("#", ls)).pad(4);
        t.add(new Label("Name", ls)).pad(4);
        t.add(new Label("Score", ls)).pad(4);
        t.row();

        com.badlogic.gdx.utils.Array<Scoreboard> list = leaderboard.loadAll();
        list.sort(new java.util.Comparator<Scoreboard>() {
            @Override
            public int compare(Scoreboard a, Scoreboard b) {
                if (a.score != b.score) return Integer.compare(b.score, a.score);
                return Long.compare(a.when, b.when);
            }
        });

        int count = Math.min(10, list.size);
        for (int i = 0; i < count; i++) {
            Scoreboard s = list.get(i);
            t.add(new Label(String.valueOf(i + 1), ls)).pad(2);
            t.add(new Label(s.name, ls)).pad(2);
            t.add(new Label(String.valueOf(s.score), ls)).pad(2);
            t.row();
        }

        return t;
    }

    private void showLeaderboardScreen() {
        stage.clear();

        Table t = new Table();
        t.setFillParent(true);
        t.top().center();

        // Font config
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Pixel Game.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.WHITE);
        t.add(new Label("LEADERBOARD", titleStyle)).padTop(20).padBottom(10).row();

        Table lb = buildLeaderboardTable();
        t.add(lb).padBottom(20).center().row();

        ImageButton playAgainBtn   = makeButton("button/play-btn-up.png",   "button/play-btn-down.png");
        ImageButton returnMenuBtn  = makeButton("button/return-btn-up.png", "button/return-btn-down.png");

        playAgainBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y) {
                buttonClickSound.play(volume);
                onPlayAgain.run();
            }
        });

        returnMenuBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y) {
                buttonClickSound.play(volume);
                onReturnToMenu.run();
            }
        });

        t.add(playAgainBtn).width(300).height(80).pad(6).row();
        t.add(returnMenuBtn).width(300).height(80).pad(6).row();

        stage.addActor(t);
    }
}
