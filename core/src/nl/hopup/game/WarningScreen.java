package nl.hopup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.*;

/**
 * Created by pieter on 26-4-17.
 */

public class WarningScreen implements Screen {

    final public int WARNING_SHOW_TIME = HopUp.DEBUG ? 1 : 2222;

    final HopUp game;

    OrthographicCamera camera;

    GlyphLayout layout = new GlyphLayout();
    GlyphLayout timerLayout = new GlyphLayout();

    public WarningScreen(final HopUp game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.width, game.height);

        layout.setText(game.font128, "EPILEPSY WARNING");
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        if (game.elapsed() > WARNING_SHOW_TIME) {
            game.setScreen(new MenuScreen(game));
            game.font128.setColor(Color.BLACK);
        }

        timerLayout.setText(game.font128, String.valueOf(WARNING_SHOW_TIME - game.elapsed()));

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font128.draw(game.batch, layout, game.width / 2 - layout.width / 2,
                game.height / 2 + layout.height / 2);

        game.font32.draw(game.batch, timerLayout, game.width / 2 - timerLayout.width / 2,
                game.height / 2 - timerLayout.height / 2 - 40);

        game.batch.end();


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
