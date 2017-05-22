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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sun.rmi.runtime.Log;

public class HopUp extends Game {
    public static final boolean CHECK_DEATH  = true;
    public static final boolean DEBUG        = false;

	public SpriteBatch batch;
    public BitmapFont font128;
    public BitmapFont font32;

    public int width  = 1280;
    public int height = 720;

    public ArrayList<String> textureFiles = new ArrayList<String>();
    public Map<String, Texture> textures = new HashMap<String, Texture>();

    private long startTime = 0;
	
	@Override
	public void create () {
        startTime = System.currentTimeMillis();

		batch = new SpriteBatch();

        font128 = new BitmapFont(Gdx.files.internal("font/font64.fnt"));
        font128.getData().setScale(2,2);
        font128.getData().markupEnabled = true;

        font32 = new BitmapFont(Gdx.files.internal("font/font64.fnt"));
        font32.getData().setScale(1f, 1f);
        font32.setColor(Color.BLACK);
        font32.getData().markupEnabled = true;

		this.setScreen(new WarningScreen(this));

        //textureFiles.add("maze.png");

        loadTextures();
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

    public void loadTextures() {
        for (String file : textureFiles) {
            textures.put(file, new Texture(Gdx.files.internal(file)));
        }
    }

	public long elapsed() {
        return System.currentTimeMillis() - startTime;
    }
}
