package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.WorldGridConvertor;

import java.awt.*;

/**
 * Responsible for the creation and management of terrain.
 */
public class Terrain {
    public static final String TAG = "Terrain";

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final float groundHeightAtX0;
    private final Vector2 windowDimensions;

    /**
     * Constructor
     *
     * @param gameObjects      - The collection of all participating game objects.
     * @param groundLayer      - The number of the layer to which the created ground objects should be added.
     * @param windowDimensions - The dimensions of the windows.
     * @param seed             - A seed for a random number generator.
     */
    public Terrain(GameObjectCollection gameObjects,
                   int groundLayer,
                   Vector2 windowDimensions,
                   int seed) {

        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = WorldGridConvertor.worldToGrid(0, (1 - 0.25f) * windowDimensions.y()).y();
        this.windowDimensions = WorldGridConvertor.worldToGrid(windowDimensions).add(Vector2.ONES);
        // Add 1 vector because the convertor rounds down
    }

    /**
     * This method return the ground height at a given location.
     *
     * @param x A number.
     * @return The ground height at the given location.
     */
    public float groundHeightAt(float x) {
        float gridX = WorldGridConvertor.worldToGrid(x, 0).x();
        return WorldGridConvertor.gridToWorld(0, groundGridHeightAt(gridX)).y();
    }

    /**
     * Creates ground blocks in the range between x and y x.coordinates.
     *
     * @param minX - The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxX - The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX) {
        for (int i = minX; i <= maxX; i++) {
            for (int j = (int) groundGridHeightAt(i); j < windowDimensions.y(); j++) {
                RectangleRenderable groundBlockColor = new RectangleRenderable(
                        ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Block newBlock = new Block(WorldGridConvertor.gridToWorld(i, j), groundBlockColor);
                newBlock.setTag(TAG);
                gameObjects.addGameObject(newBlock);
            }
        }
    }

    /**
     * Returns the ground's grid height at a location
     *
     * @param x The x location
     * @return The grid height at the specified location
     */
    public float groundGridHeightAt(float x) {
        return this.groundHeightAtX0;
    }
}
