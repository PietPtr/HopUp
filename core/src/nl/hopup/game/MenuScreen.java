package nl.hopup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by pieter on 22-5-17.
 */

public class MenuScreen implements Screen {
    final HopUp game;

    OrthographicCamera camera;

    TextButton.TextButtonStyle buttonStyle;

    TextButton startButton;
    TextButton highScoreButton;

    Label titleText;

    Stage stage;

    public MenuScreen(final HopUp game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.width, game.height);

        FitViewport viewport = new FitViewport(game.width, game.height, camera);
        stage = new Stage(viewport, game.batch);
        //stage = new Stage();

        Gdx.input.setInputProcessor(stage);

        Label.LabelStyle titleLabelStyle = new Label.LabelStyle(game.font128, Color.WHITE);
        titleText = new Label("Hop Up", titleLabelStyle);
        titleText.setPosition(game.width / 2 - titleText.getWidth() / 2, 550);
//        titleText.setWrap(true);
//        titleText.setHeight(1200);
//        titleText.setAlignment(Align.center);

        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = game.font32;

        startButton = new TextButton("START", buttonStyle);
        startButton.setPosition(game.width / 2 - startButton.getWidth() / 2, 400);


        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        stage.addActor(titleText);
        stage.addActor(startButton);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
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
