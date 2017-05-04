package nl.hopup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

import java.util.ArrayList;

import static java.lang.Math.abs;

/**
 * Created by pieter on 28-4-17.
 */

public class GameScreen implements Screen {
    final public static int WORLDHEIGHT = 20;
    final public static int OBSTACLE_POS_DEVIATION = 1;
    final public static float DEGREES_PER_OBSTACLE = 18;
    final public static float CAMERA_OFFSET = -5;
    final public static float HEIGHT_OFFSET = 1;

    final HopUp game;

    OrthographicCamera camera;
    OrthographicCamera textCam;

    int frame = 0;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private ShapeRenderer playerRenderer = new ShapeRenderer();
    private SpriteBatch textRenderer = new SpriteBatch();

    private Player player;
    private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    private float previousAngle = 0;
    private float zoom = 0.005f;

    public GameScreen(final HopUp game) {
        this.game = game;

        //camera = new OrthographicCamera();
        //camera.setToOrtho(false, game.width, game.height);

        game.font128.setColor(Color.BLACK);

        //camera.position.set(-camera.viewportWidth / 2, -camera.viewportHeight / 2, 0);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(1280, 1280 * (h / w));
        camera.zoom = 0.1f;
        camera.position.set(0, 0, 0);
        camera.rotate(90);
        camera.update();

        textCam = new OrthographicCamera(1280, 1280 * (h / w));
        textCam.position.set(1280 / 2, 1280 * (h/w) / 2, 0);
        textCam.update();

        shapeRenderer.setColor(Color.BLACK);
        playerRenderer.setColor(Color.BLACK);

        player = new Player();

        for (int i = 20; i < 360; i += DEGREES_PER_OBSTACLE)
        {
            float deviation = Utils.randint(-OBSTACLE_POS_DEVIATION * 10, OBSTACLE_POS_DEVIATION * 10) / 10f;
            obstacles.add(new Obstacle(i + deviation));
            deviation = Utils.randint(-OBSTACLE_POS_DEVIATION * 10, OBSTACLE_POS_DEVIATION * 10) / 10f;
            obstacles.add(new Obstacle(i + deviation));
        }


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // Logic updates
        player.update();

        Vector2 camPos = Utils.polarToPos(WORLDHEIGHT + HEIGHT_OFFSET, player.getAngle() + CAMERA_OFFSET);
        camera.position.set(camPos.x, camPos.y, 0);
        camera.zoom = zoom;
        camera.rotate(previousAngle - (player.getAngle() + CAMERA_OFFSET));
        camera.update();

        boolean dead = testDead();
        if (dead) {
            player.kill();
        }

        previousAngle = player.getAngle() + CAMERA_OFFSET;

        // Drawing

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        playerRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // World
        shapeRenderer.circle(0, 0, WORLDHEIGHT, 1024);


        // Obstacles
        for (Obstacle obstacle : obstacles) {
            obstacle.draw(shapeRenderer);
        }

        shapeRenderer.end();

        playerRenderer.begin(ShapeRenderer.ShapeType.Filled);
        player.draw(playerRenderer);
        playerRenderer.end();

        textRenderer.begin();
        textRenderer.end();

        game.batch.setProjectionMatrix(textCam.combined);
        game.batch.begin();
        game.font32.draw(game.batch, String.valueOf(player.getRunningTime()), 0, textCam.viewportHeight - 10);
        game.batch.end();

        frame++;
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

    private boolean testDead() {
        int closest = 0;
        for (int i = 0; i < obstacles.size(); i++) {
            if (abs(obstacles.get(i).getAngle() - player.getAngle()) % 360 <
                abs(obstacles.get(closest).getAngle() - player.getAngle()) % 360) {
                    closest = i;
            }
        }

        for (int i = -2; i <= 2; i++) {
            int index = (closest + i) % obstacles.size();
            if (index < 0) {
                index = obstacles.size() + index;
            }

            boolean hit = Utils.testRectangleToCircle(obstacles.get(index).getWidth(),
                    obstacles.get(index).getHeight(), obstacles.get(index).getAngle(),
                    obstacles.get(index).getCenter().x, obstacles.get(index).getCenter().y,
                    player.getPosition().x, player.getPosition().y, player.getRadius());

            if (hit) {
                return true;
            }
        }


        return false;
    }
}
