package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the sun - moves across the sky in an elliptical path.
 */
public class Sun {

    public static final String TAG = "Sun";

    private static GameObject sun;
    private static Vector2 windowDimensions;

    /**
     * Creates the Object Sun and sets its movement over the screen
     * @param windowDimensions - screen dimensions
     * @param cycleLength - Total time for a cycle
     * @param gameObjects - gameObject
     * @param layer - represents the Layer which the sun will be placed.
     * @return - An object of Sun.
     */

    public static GameObject create(
            Vector2 windowDimensions,
            float cycleLength,
            GameObjectCollection gameObjects,
            int layer){
        Sun.windowDimensions = windowDimensions;
        sun = new GameObject(
                Vector2.ZERO,
                windowDimensions,
                new OvalRenderable(Color.YELLOW));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sun, layer);
        new Transition<>(
                sun,
                Sun::setSunPosition,
                0f,
                360f,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null
        );
        sun.setTag(TAG);
    return sun;
    }

    /**
     * Calculate the changing sun position coordinates for the cycle of the sun.
     * @param angleInSky - represents the angle of the rotation.
     */
    private static void setSunPosition(float angleInSky){
     sun.setCenter(windowDimensions.mult(0.5f).add(Vector2.UP.mult(100).rotated(angleInSky)));
    }


}
