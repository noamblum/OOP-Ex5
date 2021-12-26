package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * A class handling the world's sky
 */
public class Sky {

    public static final String SKY_TAG = "Sky";

    private static final Color SKY_COLOR = Color.decode("#80C6E5");

    /**
     * Create the background sky
     * @param gameObjects The main game object collection
     * @param windowDimensions The window's dimensions
     * @param skyLayer The layer in which to add the sky
     * @return A new GameObject representing the sky
     */
    public static danogl.GameObject create(
            danogl.collisions.GameObjectCollection gameObjects,
            danogl.util.Vector2 windowDimensions,
            int skyLayer) {
        GameObject sky = new GameObject(Vector2.ZERO, windowDimensions, new RectangleRenderable(SKY_COLOR));
        sky.setTag(SKY_TAG);
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sky, skyLayer);
        return sky;
    }

}
