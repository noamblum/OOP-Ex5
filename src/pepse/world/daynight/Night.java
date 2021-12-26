package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * A class handling the day/night cycle
 */
public class Night {

    public static final String DAY_NIGHT_TAG = "DayNightCycle";

    private static final Color DARKNESS_COLOR = Color.BLACK;
    private static final float DAY_OPACITY = 0;
    private static final float NIGHT_OPACITY = 0.5f;

    /**
     * Create a new day/night cycle
     *
     * @param gameObjects      The main game object collection
     * @param layer            The layer to add the darkness modifier upon
     * @param windowDimensions The window's dimensions
     * @param cycleLength      The length of the day/night cycle, in seconds
     * @return An object handling the day/night cycle.
     */
    public static danogl.GameObject create(
            danogl.collisions.GameObjectCollection gameObjects,
            int layer,
            danogl.util.Vector2 windowDimensions,
            float cycleLength) {
        GameObject night = new GameObject(
                Vector2.ZERO,
                windowDimensions,
                new RectangleRenderable(DARKNESS_COLOR));

        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(night, layer);
        new Transition<>(
                night,
                night.renderer()::setOpaqueness,
                DAY_OPACITY,
                NIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                cycleLength / 2,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
        night.setTag(DAY_NIGHT_TAG);
        return night;
    }
}
