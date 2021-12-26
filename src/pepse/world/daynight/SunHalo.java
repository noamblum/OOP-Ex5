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
    private static GameObject sunHalo;
    private static GameObject sunToFollow;
    /**
     * This function creates a halo around a given object that represents the sun.
     * The halo will be tied to the given sun, and will always move with it.
     * @param gameObjects - The collection of all participating game objects.
     * @param sun - A game object representing the sun (it will be followed by the created game object).
     * @param color - The color of the halo.
     * @param layer - The number of the layer to which the created halo should be added.
     * @return  A new game object representing the sun's halo.
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            GameObject sun,
            Color color,
            int layer){
        sunToFollow = sun;
        sunHalo = new GameObject(
                Vector2.ZERO,
                Sun.sunDimensions.mult(1.2f),
                new OvalRenderable(color));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sunHalo, layer);
        sun.addComponent(SunHalo::followSun);
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
