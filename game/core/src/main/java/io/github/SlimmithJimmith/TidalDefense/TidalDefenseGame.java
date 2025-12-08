/**
 * TidalDefenseGame.java
 * Loads resources for use by all screens and gameplay, as well as saves the states of some settings
 *
 * @author Team #2 - Brendan Boyko, Jimi Ruble, Mehdi Khazaal, James Watson
 * @version 2.0
 * Create Date: 09-27-2025
 */

package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SlimmithJimmith.TidalDefense.Screens.*;

public class TidalDefenseGame extends Game {
    // Shared resources (loaded once, used by all screens)

    // Images
    public SpriteBatch batch;
    public Texture img_background;
    public Texture img_lifeguard;
    public Texture img_bullet;

    // Sound and music
    public Sound bullet_sound;
    public Sound button_click_sound;
    public Sound enemy_death;
    public Music backgroundMusic;

    private float volume = 0.7f;
    private boolean musicEnabled = true;

    public Leaderboard leaderboard;

    @Override
    public void create() {
        // Load all shared assets ONCE
        batch = new SpriteBatch();

        img_background = new Texture("background-2.png");
        img_lifeguard = new Texture("LifeguardShootingUp.png");
        img_bullet = new Texture("Bullet.png");

        bullet_sound = Gdx.audio.newSound(Gdx.files.internal("sounds/gun_shot.mp3"));
        button_click_sound = Gdx.audio.newSound(Gdx.files.internal("sounds/button_click.mp3"));
        enemy_death = Gdx.audio.newSound(Gdx.files.internal("sounds/enemy_death.mp3"));

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/bg_music.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(volume);
        backgroundMusic.play();

        leaderboard = new Leaderboard();
        leaderboard.loadAll();

        // Game starts with main menu
        setScreen(new MainMenuScreen(this));
    }

    // Discard resources when game exits
    @Override
    public void dispose() {
        batch.dispose();
        img_background.dispose();
        img_lifeguard.dispose();
        img_bullet.dispose();
        bullet_sound.dispose();
        button_click_sound.dispose();
        enemy_death.dispose();
        backgroundMusic.dispose();
    }

    // Getters/setters for volume and music
    public float getVolume() { return volume; }
    public void setVolume(float volume) {
        this.volume = volume;
        backgroundMusic.setVolume(volume);
    }
    public boolean isMusicEnabled() { return musicEnabled; }
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (enabled && !backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        } else if (!enabled) {
            backgroundMusic.pause();
        }
    }
}
