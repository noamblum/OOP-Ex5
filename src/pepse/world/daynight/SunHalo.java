package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the halo of sun.
 */
public class SunHalo{
    public static final String TAG = "SunHalo";
    private static final float HALO_SIZE_FACTOR = 1.9f;
    private static GameObject sunHalo;
    private static GameObject sunToFollow;
    /**
     * This function creates a halo around a given object that represents the sun.
     * The halo will be tied to the given sun, and will always move with it.
     * @param gameObjects - The collection of all participating game objects.
     * @param layer - The number of the layer to which the created halo should be added.
     * @param sun - A game object representing the sun (it will be followed by the created game object).
     * @param color - The color of the halo.
     * @return  A new game object representing the sun's halo.
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer, GameObject sun,
            Color color){
        sunToFollow = sun;
        sunHalo = new GameObject(
                Vector2.ZERO,
                Sun.sunDimensions.mult(HALO_SIZE_FACTOR),
                new OvalRenderable(color));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sunHalo, layer);
        sun.addComponent(SunHalo::followSun);
        sunHalo.setTag(TAG);
        return sunHalo;
    }

    /**
     * Sets the center of sunHalo to the center of Sun object.
     * @param deltaTime - Time between frames.
     */
    public static void followSun(float deltaTime){
        sunHalo.setCenter(sunToFollow.getCenter());

    }

}
