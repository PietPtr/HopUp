package nl.hopup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by pieter on 22-5-17.
 */

public class HighScoreScreen implements Screen {
    private final HopUp game;

    private Label titleText;
    private Label scores;

    Stage stage;
    OrthographicCamera camera;

    public HighScoreScreen(final HopUp game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.width, game.height);

        FitViewport viewport = new FitViewport(game.width, game.height, camera);
        stage = new Stage(viewport, game.batch);

        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);

        Label.LabelStyle titleLabelStyle = new Label.LabelStyle(game.font128, Color.WHITE);
        titleText = new Label("HIGH SCORES", titleLabelStyle);
        titleText.setPosition(game.width - titleText.getWidth() - 40,
                720 / 2 - titleText.getHeight() / 2);

        String scoreStr = "";
        for (int i = 9; i >= 0; i--) {
            scoreStr += (10 - i) + ". " + game.highscores.getFloat(Integer.toString(i));
            if (i != 0) {
                scoreStr += "\n";
            }
        }

        Label.LabelStyle scoreStyle = new Label.LabelStyle(game.font32, Color.WHITE);
        scores = new Label(scoreStr, scoreStyle);

        stage.addActor(scores);
        stage.addActor(titleText);
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyPressed(Input.Keys.BACK) ||
            Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)) {
            game.setScreen(new MenuScreen(game));
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
