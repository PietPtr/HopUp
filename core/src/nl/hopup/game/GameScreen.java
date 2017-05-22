package nl.hopup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.abs;
import static java.lang.Math.random;
import static java.lang.Math.sin;

/**
 * Created by pieter on 28-4-17.
 */

enum GameState {
    STARTUP,
    RUNNING,
    GAMEOVER
}

public class GameScreen implements Screen {
    final public static int WORLDHEIGHT = 20;
    final public static int OBSTACLE_POS_DEVIATION = 1;
    final public static float DEGREES_PER_OBSTACLE = 18;
    final public static float DEGREES_PER_ROCK = 9;
    final public static float CAMERA_OFFSET = -3;
    final public static float HEIGHT_OFFSET = 0.5f;
    final public static int NEW_EVENT_TIME = HopUp.DEBUG ? 1000 : 5000;
    final public static double RESTART_DISTANCE = WORLDHEIGHT - 4;
    final public static float ROTATE_ACC = 1.5f;
    final public static float BASE_ZOOM = 0.005f;
    final public static float ZOOM_ACC = 0.025f;
    final public static int MAX_DOTS = 256; // lower for better performance

    public HashMap<String, Integer> eventTimes = new HashMap<String, Integer>();

    final public HopUp game;

    OrthographicCamera camera;
    OrthographicCamera textCam;

    int frame = 0;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private ShapeRenderer playerRenderer = new ShapeRenderer();
    private ShapeRenderer worldRenderer = new ShapeRenderer();
    private ShapeRenderer rockRenderer = new ShapeRenderer();
    private SpriteBatch textRenderer = new SpriteBatch();

    private GameState gamestate = GameState.RUNNING;
    private Player player;

    private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    private ArrayList<Dot> dots = new ArrayList<Dot>();
    private ArrayList<Rock> rocks = new ArrayList<Rock>();

    private float previousAngle = 0;
    private float zoom = BASE_ZOOM;

    private GlyphLayout gameOverLayout = new GlyphLayout();
    private GlyphLayout tapToContinue = new GlyphLayout();

    public long gameStartTime = 0;
    public long gameOverTime = 0;

    public ArrayList<Color> rainbows = new ArrayList<Color>();

    private BitmapFont smallFont;
    private BitmapFont bigFont;

    private float rotateSpeed = 1f;
    private float zoomSpeed = 1f;


    public GameScreen(final HopUp game) {

        addEvent("FALLING DOTS");           // 1
        addEvent("PULSATING OBSTACLES");    // 2
        addEvent("VIEW ZOOM");              // 3
        addEvent("VIEW ROTATE");            // 4
        addEvent("RAINBOW BG");             // 5
        addEvent("RAINBOW ROCKS");          // 6
        addEvent("RAINBOW DOTS");           // 7
        addEvent("RAINBOW OBSTACLES");      // 8
        addEvent("RAINBOW WORLD");          // 9
        addEvent("RAINBOW PLAYER");         // 10

        this.game = game;

        bigFont = new BitmapFont(Gdx.files.internal("font/font64.fnt"));
        bigFont.getData().setScale(2,2);
        bigFont.setColor(Color.BLACK);

        smallFont = new BitmapFont(Gdx.files.internal("font/font64.fnt"));
        smallFont.getData().setScale(1f, 1f);
        smallFont.setColor(Color.BLACK);
        smallFont.getData().markupEnabled = true;


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
        worldRenderer.setColor(Color.BLACK);
        rockRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 1));

        player = new Player(this);

        for (int i = 20; i < 360; i += DEGREES_PER_OBSTACLE)
        {
            float deviation = Utils.randint(-OBSTACLE_POS_DEVIATION * 10, OBSTACLE_POS_DEVIATION * 10) / 10f;
            obstacles.add(new Obstacle(i + deviation, this));
            deviation = Utils.randint(-OBSTACLE_POS_DEVIATION * 10, OBSTACLE_POS_DEVIATION * 10) / 10f;
            obstacles.add(new Obstacle(i + deviation, this));
        }

        for (int i = 0; i < 360; i += DEGREES_PER_ROCK) {
            rocks.add(new Rock(i));
        }

        gameOverLayout.setText(bigFont, "GAME OVER");

        gameStartTime = System.currentTimeMillis();

        camera.zoom = zoom;

    }

    public void resetGame() {
        game.restart();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // Logic updates
        player.update();

        calculateRainbows();

        Vector2 camPos = Utils.polarToPos(WORLDHEIGHT + HEIGHT_OFFSET, player.getAngle() + CAMERA_OFFSET);
        camera.position.set(camPos.x, camPos.y, 0);
        camera.rotate(previousAngle - (player.getAngle() + CAMERA_OFFSET));
        camera.update();

        previousAngle = player.getAngle() + CAMERA_OFFSET;

        boolean dead = testDead();
        if (dead && HopUp.CHECK_DEATH) {
            player.kill();
        }

        // TODO: iterate over player list to check for gameover
        if (player.isDead() && gameOverTime == 0) {
            gamestate = GameState.GAMEOVER;
            gameOverTime = System.currentTimeMillis();
        }

        if (gamestate == GameState.GAMEOVER && player.getDistance() <= RESTART_DISTANCE && Gdx.input.isTouched()) {
            resetGame();
        }

        if (isEventHappening("RAINBOW BG")) {
            smallFont.setColor(rainbows.get(2));
            bigFont.setColor(rainbows.get(2));
        }

        for (Obstacle o : obstacles) {
            o.update();
        }

        if (isEventHappening("FALLING DOTS")) {

            if (Utils.randint(0, 100) < 10) {
                dots.add(new Dot(player.getAngle() - (Utils.randint(0, 320) / 10.0f)));
            }

            for (Dot d : dots) {
                d.update();
            }
        }


        while (dots.size() > MAX_DOTS) {
            System.out.println("removing dot");
            dots.remove(0);
        }
        System.out.println(dots.size());

        if (isEventHappening("VIEW ROTATE")) {
            camera.rotate(rotateSpeed * Gdx.graphics.getDeltaTime());
            rotateSpeed += Gdx.graphics.getDeltaTime() * ROTATE_ACC;
        }

        if (isEventHappening("VIEW ZOOM")) {
            camera.zoom = BASE_ZOOM + (float)Math.sin((System.currentTimeMillis() - gameStartTime - eventTimes.get("VIEW ZOOM")) / 1000.0f * zoomSpeed) / 800f;
            zoomSpeed += Gdx.graphics.getDeltaTime() * ZOOM_ACC;
            //System.out.println(zoomSpeed);
        }


        /////////////
        // Drawing //
        /////////////

        if (isEventHappening("RAINBOW BG"))
            Gdx.gl.glClearColor(rainbows.get(0).r, rainbows.get(0).g, rainbows.get(0).b, 0);
        else
            Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        worldRenderer.setProjectionMatrix(camera.combined);
        rockRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (isEventHappening("RAINBOW DOTS")) {
            shapeRenderer.setColor(rainbows.get(1));
        } else {
            shapeRenderer.setColor(Color.BLACK);
        }

        // Dots
        for (Dot d : dots) {
            d.draw(shapeRenderer);
        }

        if (isEventHappening("RAINBOW OBSTACLES")) {
            shapeRenderer.setColor(rainbows.get(1));
        } else {
            shapeRenderer.setColor(Color.BLACK);
        }

        // Obstacles
        for (Obstacle obstacle : obstacles) {
            obstacle.draw(shapeRenderer);
        }

        shapeRenderer.end();

        // World
        worldRenderer.begin(ShapeRenderer.ShapeType.Filled);
        worldRenderer.circle(0, 0, WORLDHEIGHT, 1024);

        if (isEventHappening("RAINBOW WORLD")) {
            worldRenderer.setColor(rainbows.get(1));
        }

        worldRenderer.end();

        // Rocks
        if (isEventHappening("RAINBOW ROCKS")) {
            rockRenderer.setColor(rainbows.get(3));
        }

        rockRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Rock r : rocks) {
            r.draw(rockRenderer);
        }
        rockRenderer.end();

        game.batch.setProjectionMatrix(textCam.combined);
        game.batch.begin();
        smallFont.draw(game.batch, String.valueOf(player.getRunningTime()), 7, textCam.viewportHeight - 10);

        if (gamestate == GameState.GAMEOVER) {

            if ((int)((System.currentTimeMillis() % 65536) / 400.0) % 2 == 0) {
                tapToContinue.setText(smallFont, "[WHITE]TAP TO PLAY AGAIN");
            } else {
                tapToContinue.setText(smallFont, "[BLACK]TAP TO PLAY AGAIN");
            }
            if (isEventHappening("RAINBOW BG")) {
                tapToContinue.setText(smallFont, "TAP TO PLAY AGAIN");
                gameOverLayout.setText(bigFont, "GAME OVER");
            }

            bigFont.draw(game.batch, gameOverLayout, textCam.viewportWidth / 2 - gameOverLayout.width / 2, 600);

            if (player.getDistance() <= RESTART_DISTANCE)
                smallFont.draw(game.batch, tapToContinue, textCam.viewportWidth / 2 - tapToContinue.width / 2, 400);
        }
        game.batch.end();

        playerRenderer.setProjectionMatrix(camera.combined);
        playerRenderer.begin(ShapeRenderer.ShapeType.Filled);
        player.draw(playerRenderer);
        playerRenderer.end();

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
        textRenderer.dispose();
        shapeRenderer.dispose();
        playerRenderer.dispose();
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

    public void addEvent(String name) {
        eventTimes.put(name, (eventTimes.size() + 1) * NEW_EVENT_TIME);
    }

    public boolean isEventHappening(String name) {
        if (gamestate == GameState.GAMEOVER)
            return (gameOverTime - gameStartTime) > eventTimes.get(name);
        return (System.currentTimeMillis() - gameStartTime) > eventTimes.get(name);
    }

    private void calculateRainbows() {
        rainbows = new ArrayList<Color>();

        double time = (System.currentTimeMillis() % 300003) / 1000.0 ;
        time *= 5;
        double r = (Math.sin(time) + 1) / 2f;
        double g = (Math.sin(time + 2) + 1) / 2f;
        double b = (Math.sin(time + 4) + 1) / 2f;

        rainbows.add(new Color((float)r, (float)g, (float)b, 1));
        rainbows.add(new Color((float)g, (float)b, (float)r, 1));
        rainbows.add(new Color((float)b, (float)r, (float)g, 1));
        rainbows.add(new Color((float)r, (float)b, (float)g, 1));


    }
}
