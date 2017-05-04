package nl.hopup.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import sun.java2d.DisposerTarget;
import sun.management.Util;

/**
 * Created by pieter on 28-4-17.
 */

public class Obstacle {
    private static final double DISTANCE_CHANGE = 0.01;

    private float angle;
    private double distance;
    private double rotation;
    private float height;
    private float width;

    public Obstacle(double angle) {
        this.angle = (float)angle;
        this.rotation = this.angle;
        this.height = Utils.randint(50, 90) / 100f;
        this.width = Utils.randint(50, 90) / 100f;
        this.distance = GameScreen.WORLDHEIGHT + height / 2 - 0.3;
    }

    public float x() {
        return Utils.polarToPos(distance, angle).x;
    }

    public float y() {
        return Utils.polarToPos(distance, angle).y;
    }

    public void update() {

    }

    public void draw(ShapeRenderer renderer) {
        if (renderer.isDrawing()) {
            renderer.rect(x() - width/2, y() - height/2, width/2, height/2, width, height, 1, 1,
                    (float)rotation);
        }
    }

    private int getPointsInWorld() {
        // TODO: rotation matrix

        ArrayList<Vector2> points = new ArrayList<Vector2>();

        Vector2 rectPos = Utils.polarToPos(distance, angle);
        points.add(new Vector2(rectPos.x + 0,     rectPos.y + 0     ));
        points.add(new Vector2(rectPos.x + 0,     rectPos.y + height));
        points.add(new Vector2(rectPos.x + width, rectPos.y + 0     ));
        points.add(new Vector2(rectPos.x + width, rectPos.y + height));

        int inWorld = 0;

        Matrix3 rotationMatrix = new Matrix3();
        rotationMatrix.rotate(angle);

        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).dst(0,0) < GameScreen.WORLDHEIGHT) {
                inWorld++;
            }
        }
        System.out.println("Points in world: " + inWorld);
        return inWorld;
    }

    public float getAngle() {
        return angle;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Vector2 getCenter() {
        Vector2 pos = Utils.polarToPos(distance, angle);
        return new Vector2(pos.x, pos.y);
    }

    public double getRotation() {
        return rotation;
    }
}
