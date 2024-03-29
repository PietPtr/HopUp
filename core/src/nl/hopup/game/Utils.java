package nl.hopup.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by pieter on 25-4-17.
 */

public class Utils {
    public static Vector2 polarToPos(double distance, double angle) {
        double x  = Math.cos(angle * 0.0174532925) * distance;
        double y = Math.sin(angle * 0.0174532925) * distance;
        return new Vector2((float)x, (float)y);
    }

    public static int randint(int min, int max) {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    public static float getCamAngle(OrthographicCamera cam) {
        return (float)Math.atan2(cam.up.x, cam.up.y)* MathUtils.radiansToDegrees;
    }

    // http://stackoverflow.com/questions/5650032/collision-detection-with-rotated-rectangles
    /** Rectangle To Point. */
    public static boolean testRectangleToPoint(double rectWidth, double rectHeight,
                                               double rectRotation, double rectCenterX,
                                               double rectCenterY, double pointX, double pointY) {
        if(rectRotation == 0)   // Higher Efficiency for Rectangles with 0 rotation.
            return Math.abs(rectCenterX-pointX) < rectWidth/2 && Math.abs(rectCenterY-pointY) < rectHeight/2;

        double tx = Math.cos(rectRotation)*pointX - Math.sin(rectRotation)*pointY;
        double ty = Math.cos(rectRotation)*pointY + Math.sin(rectRotation)*pointX;

        double cx = Math.cos(rectRotation)*rectCenterX - Math.sin(rectRotation)*rectCenterY;
        double cy = Math.cos(rectRotation)*rectCenterY + Math.sin(rectRotation)*rectCenterX;

        return Math.abs(cx-tx) < rectWidth/2 && Math.abs(cy-ty) < rectHeight/2;
    }

    /** Circle To Segment. */
    public static boolean testCircleToSegment(double circleCenterX, double circleCenterY,
                                              double circleRadius, double lineAX, double lineAY,
                                              double lineBX, double lineBY) {
        double lineSize = Math.sqrt(Math.pow(lineAX-lineBX, 2) + Math.pow(lineAY-lineBY, 2));
        double distance;

        if (lineSize == 0) {
            distance = Math.sqrt(Math.pow(circleCenterX-lineAX, 2) + Math.pow(circleCenterY-lineAY, 2));
            return distance < circleRadius;
        }

        double u = ((circleCenterX - lineAX) * (lineBX - lineAX) + (circleCenterY - lineAY) * (lineBY - lineAY)) / (lineSize * lineSize);

        if (u < 0) {
            distance = Math.sqrt(Math.pow(circleCenterX-lineAX, 2) + Math.pow(circleCenterY-lineAY, 2));
        } else if (u > 1) {
            distance = Math.sqrt(Math.pow(circleCenterX-lineBX, 2) + Math.pow(circleCenterY-lineBY, 2));
        } else {
            double ix = lineAX + u * (lineBX - lineAX);
            double iy = lineAY + u * (lineBY - lineAY);
            distance = Math.sqrt(Math.pow(circleCenterX-ix, 2) + Math.pow(circleCenterY-iy, 2));
        }

        return distance < circleRadius;
    }

    /** Rectangle To Circle. */
    public static boolean testRectangleToCircle(double rectWidth, double rectHeight,
                                                double rectRotation, double rectCenterX,
                                                double rectCenterY, double circleCenterX,
                                                double circleCenterY, double circleRadius) {
        double tx, ty, cx, cy;

        if(rectRotation == 0) { // Higher Efficiency for Rectangles with 0 rotation.
            tx = circleCenterX;
            ty = circleCenterY;

            cx = rectCenterX;
            cy = rectCenterY;
        } else {
            tx = Math.cos(rectRotation)*circleCenterX - Math.sin(rectRotation)*circleCenterY;
            ty = Math.cos(rectRotation)*circleCenterY + Math.sin(rectRotation)*circleCenterX;

            cx = Math.cos(rectRotation)*rectCenterX - Math.sin(rectRotation)*rectCenterY;
            cy = Math.cos(rectRotation)*rectCenterY + Math.sin(rectRotation)*rectCenterX;
        }

        return testRectangleToPoint(rectWidth, rectHeight, rectRotation, rectCenterX, rectCenterY, circleCenterX, circleCenterY) ||
                testCircleToSegment(tx, ty, circleRadius, cx-rectWidth/2, cy+rectHeight/2, cx+rectWidth/2, cy+rectHeight/2) ||
                testCircleToSegment(tx, ty, circleRadius, cx+rectWidth/2, cy+rectHeight/2, cx+rectWidth/2, cy-rectHeight/2) ||
                testCircleToSegment(tx, ty, circleRadius, cx+rectWidth/2, cy-rectHeight/2, cx-rectWidth/2, cy-rectHeight/2) ||
                testCircleToSegment(tx, ty, circleRadius, cx-rectWidth/2, cy-rectHeight/2, cx-rectWidth/2, cy+rectHeight/2);
    }
}
