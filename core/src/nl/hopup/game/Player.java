package nl.hopup.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

/**
 * Created by pieter on 28-4-17.
 */

enum PlayerState {
    BUILDING_UP,
    JUMPING,
    RUNNING,
    DEAD_WAIT,
    DEAD_FALL
}

public class Player {
    final double MAX_JUMP_BUILDUP = 7.0;
    final double MIN_JUMP_BUILDUP = 3.0;
    final double ACCELERATION = 0.16;
    final double DEATH_WAIT_TIME = 100;
    final double GRAVITY = 14.81;
    final double BASE_RADIUS = 0.15;

    private PlayerState state;
    private double radius = BASE_RADIUS;
    private double angle = 0;
    private double distance = GameScreen.WORLDHEIGHT;
    private double landSpeed = 7.5;
    private double verticalSpeed = 0;
    private double buildup = MIN_JUMP_BUILDUP;
    private double buildupSpeed = 24.0;
    private double collisionDistance = GameScreen.WORLDHEIGHT + radius;
    private long timeOfDeath = 0;
    private boolean dead = false;
    private float runningTime = 0;
    private GameScreen gameScreen;

    public Player(GameScreen gameScreen) {
        this.state = PlayerState.RUNNING;
        this.gameScreen = gameScreen;
    }

    public void update() {
        switch (state) {
            case BUILDING_UP:
                if (!Gdx.input.isTouched()) {
                    verticalSpeed = buildup;
                    buildup = MIN_JUMP_BUILDUP;
                    state = PlayerState.JUMPING;
                } else {
                    buildup += Gdx.graphics.getDeltaTime() * buildupSpeed;
                    buildup = buildup > MAX_JUMP_BUILDUP ? MAX_JUMP_BUILDUP : buildup;
                }
                updatePhysics();
                updateDeathStatus();
                break;
            case JUMPING:
                if (distance <= collisionDistance) {
                    state = PlayerState.RUNNING;
                }
                updatePhysics();
                updateDeathStatus();
                break;
            case RUNNING:
                if (Gdx.input.isTouched()) {
                    state = PlayerState.BUILDING_UP;
                }
                updatePhysics();
                updateDeathStatus();
                break;
            case DEAD_WAIT:
                if (System.currentTimeMillis() - timeOfDeath >= DEATH_WAIT_TIME) {
                    state = PlayerState.DEAD_FALL;
                    landSpeed = 0;
                    verticalSpeed = 7;
                }
                break;
            case DEAD_FALL:
                updatePhysics();
                break;
        }
    }

    public float x() {
        return Utils.polarToPos(distance + radius - BASE_RADIUS, angle).x;
    }

    public float y() {
        return Utils.polarToPos(distance + radius - BASE_RADIUS, angle).y;
    }

    public void updatePhysics() {
        angle -= landSpeed * Gdx.graphics.getDeltaTime();
        //angle = 37;
        landSpeed += ACCELERATION * Gdx.graphics.getDeltaTime();

        distance += Gdx.graphics.getDeltaTime() * verticalSpeed;
        verticalSpeed -= Gdx.graphics.getDeltaTime() * GRAVITY;

        radius = BASE_RADIUS - ((buildup - MIN_JUMP_BUILDUP) / (MAX_JUMP_BUILDUP + MIN_JUMP_BUILDUP) * BASE_RADIUS);

        if (distance <= collisionDistance && state != PlayerState.DEAD_FALL) {
            distance = collisionDistance;
        }

        if (!dead) {
            runningTime += Gdx.graphics.getDeltaTime();
        }
    }

    public void updateDeathStatus() {
        if (dead) {
            timeOfDeath = System.currentTimeMillis();
            state = PlayerState.DEAD_WAIT;
        }
    }

    public void draw(ShapeRenderer renderer) {
        if (renderer.isDrawing()) {
            if (dead)
                renderer.setColor(Color.RED);
            else
                renderer.setColor(Color.BLACK);
            if (gameScreen.isEventHappening("RAINBOW PLAYER")) {
                renderer.setColor(gameScreen.rainbows.get(2));
            }
            renderer.circle(x(), y(), getRadius(), 128);
        }

    }

    public void setRadius(float radius) {
        this.radius = radius;
        this.collisionDistance = radius + GameScreen.WORLDHEIGHT;
    }

    public float getRadius() { return (float)radius; }

    public float getAngle() { return (float)angle; }

    public double getDistance() {
        return distance;
    }

    public Vector2 getPosition() {
        return Utils.polarToPos(distance, angle);
    }

    public void kill() {
        dead = true;
    }

    public float getRunningTime() {
        return runningTime;
    }

    public boolean isDead() {
        return dead;
    }
}
