package nl.hopup.game;


import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by pieter on 8-5-17.
 */

public class Rock {
    public float distance;
    public float angle;
    public float rotation;
    public float height;
    public float width;

    public Rock(float angle) {
        this.angle = angle;
        distance = GameScreen.WORLDHEIGHT - Utils.randint(60, 220) / 100f;
        rotation = Utils.randint(0, 360);
        height = Utils.randint(50, 90) / 100f;
        width = Utils.randint(50, 90) / 100f;
    }

    public void draw(ShapeRenderer renderer) {
        if (renderer.isDrawing()) {
            renderer.rect(x() - width/2, y() - height/2, width/2, height/2, width, height, 1, 1,
                    rotation);
        }
    }

    public float x() {
        return Utils.polarToPos(distance, angle).x;
    }

    public float y() {
        return Utils.polarToPos(distance, angle).y;
    }
}
