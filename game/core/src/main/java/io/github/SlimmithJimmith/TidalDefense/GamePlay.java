package io.github.SlimmithJimmith.TidalDefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GamePlay {
    private final Core core;
    private boolean powerUpReady;
    private final Texture img_background;
    private int score;
    private final Label scoreLabel;
    private final Stage hudStage;
    private final Stage gamePlayStage;
    private boolean visible = false;


    public GamePlay(boolean powerUpReady, Texture img_background, int score, Label scoreLabel, Stage hudStage, Core core) {
        this.powerUpReady = powerUpReady;
        this.img_background = img_background;
        this.score = score;
        this.scoreLabel = scoreLabel;
        this.core = core;
        this.hudStage = hudStage;

        gamePlayStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(gamePlayStage);

    }

    public void show(){
        visible = true;
    }

    public void hide(){
        visible = false;
    }
    public void render(){
        if(!visible){
            return;
        }
//        gamePlayStage.act(Gdx.graphics.getDeltaTime());
//        gamePlayStage.draw();

        // Move enemies
        core.enemyManager.updateEnemyPosition();

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        core.batch.begin();
        core.batch.draw(img_background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        core.lifeguard.Draw(core.batch);

        // enemyManager returns zero or num points of enemy killed
        score += core.enemyManager.enemyHit(core.lifeguard, core.enemy_death);
        scoreLabel.setText("Level: " + core.currentLevel + " Score: " + score); //display score to user

        // Bounds checking for current state of enemy formation
        core.enemyManager.updateEnemyBounds();

        // Regenerates enemies when all are destroyed
        if (core.enemyManager.allDead()) {
            core.enemyManager.createFormation(++core.currentLevel);

            // every 3 levels, create power up if not already created
            if (powerUpReady && (core.currentLevel - 1) % 3 == 0) {
                core.powerUp = new PowerUp();
                powerUpReady = false;
            }
            // reset power up readiness for next time
            if ((core.currentLevel - 1) % 3 != 0) {
                powerUpReady = true;
            }
            core.batch.end();
            return;
        }

        // Move the power up if it is active
        if (core.powerUp != null) {
            // Check for power up being picked up
            if (core.powerUp.playerCollision(core.lifeguard)) {
                core.lifeguard.PowerUp();
                core.powerUp.dispose();
                core.powerUp = null;
            } else if (core.powerUp.powerUpPastPlayer()) { // Player missed it
                core.powerUp.dispose();
                core.powerUp = null;
            } else {
                core.powerUp.updatePowerUpPosition();
                core.powerUp.Draw(core.batch);
            }
        }

        // Bounds checking to ensure enemies do not exit the RIGHT or LEFT side of the screen
        core.enemyManager.enemyBoundsCheck();

        // Draw enemies still alive
        core.enemyManager.drawBatch(core.batch);

        // If enemy touches the lifeguard or below player, show Game Over.
        if (core.enemyManager.playerCollision(core.lifeguard) || core.enemyManager.enemyPastPlayer()) {
            core.triggerGameOver();
            core.batch.end();   // Batch was begun earlier
            return;        // Stop the rest of render() for this frame
        }

        // Spawn OctoMini if in boss level
        if (core.currentLevel % 7 == 0) {
            core.enemyManager.updateOctoMinis(Gdx.graphics.getDeltaTime()); // move minis

            score += core.enemyManager.octoMiniHit(core.lifeguard, core.enemy_death); // detect mini being hit

            if (core.enemyManager.octoMiniPlayerCollision(core.lifeguard)) { // detect player being hit
                core.triggerGameOver();
                core.batch.end();
                return;
            }
        }

        core.batch.end();

        //Draw heads up display
        hudStage.act(Gdx.graphics.getDeltaTime());
        hudStage.draw();

        gamePlayStage.act(Gdx.graphics.getDeltaTime());
        gamePlayStage.draw();
    } // End if (!showingMenu)
}

