package nl.hopup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by pieter on 8-5-17.
 */

public class Dot {
    public static final float MAX_RADIUS = 0.02f;
    public static final float FALL_SPEED = 5;
    public static final float HORI_SPEED = -15;

    private float angle;
    private float distance;
    private float radius;

    public Dot(float angle) {
        this.angle = angle;
        this.distance = GameScreen.WORLDHEIGHT + Utils.randint(0, 400) / 100f;
        this.radius = 0;
    }

    public void update() {
        if (radius >= MAX_RADIUS) {
            radius = MAX_RADIUS;
        } else {
            radius += Gdx.graphics.getDeltaTime() * 0.1f;
        }
    }

    public void draw(ShapeRenderer renderer) {
        if (renderer.isDrawing()) {
            Vector2 pos = Utils.polarToPos(distance, angle);
            renderer.circle(pos.x, pos.y, radius, 32);
        }
    }

    public boolean shouldBeRemoved() {
        return distance + radius < GameScreen.WORLDHEIGHT - 1;
    }
}
