package nl.hopup.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.File;

import sun.rmi.runtime.Log;

public class HopUp extends Game {
	public SpriteBatch batch;
    public BitmapFont font128;
    public BitmapFont font32;
    public int width  = 1280;
    public int height = 720;

    private long startTime = 0;
	
	@Override
	public void create () {
		batch = new SpriteBatch();

        font128 = new BitmapFont(Gdx.files.internal("font/font64.fnt"));
        font128.getData().setScale(2,2);

        font32 = new BitmapFont(Gdx.files.internal("font/font64.fnt"));
        font32.getData().setScale(1f, 1f);
        font32.setColor(Color.BLACK);
        font32.getData().markupEnabled = true;

		this.setScreen(new WarningScreen(this));

        startTime = System.currentTimeMillis();
	}

	@Override
	public void render () {
        super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
        font128.dispose();
	}

	public void restart() {
        this.setScreen(new GameScreen(this));
    }

	public long elapsed() {
        return System.currentTimeMillis() - startTime;
    }
}
