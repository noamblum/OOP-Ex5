package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;

/**
 * Responsible for the creation and management of terrain.
 */
public class Terrain {
    public static final String TAG = "Terrain";

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private GameObjectCollection gameObjects;
    private int groundLayer;
    private float groundHeightAtX0;
    private Vector2 windowDimensions;

    /**
     *Constructor
     *@param  gameObjects - The collection of all participating game objects.
     *@param groundLayer - The number of the layer to which the created ground objects should be added.
     *@param windowDimensions - The dimensions of the windows.
     *@param seed - A seed for a random number generator.
     */
    public Terrain(GameObjectCollection gameObjects,
                   int groundLayer,
                   Vector2 windowDimensions,
                   int seed) {

        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = (1 - 0.25f)* windowDimensions.y(); // need to check about the seed thing
        this.windowDimensions = windowDimensions;
    }

    /**
     * This method return the ground height at a given location.
     * @param x A number.
     * @return The ground height at the given location.
     */
    public float groundHeightAt(float x){
        return this.groundHeightAtX0;
    }

    /**
     * Creates ground blocks in the range between x and y x.coordinates.
     * @param minX - The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param  maxX - The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX) {
        RectangleRenderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
        float roundedStartingPoint = (float) (Math.floor(minX / Block.SIZE)*Block.SIZE);
        float roundedEndingPoint = (float) (Math.ceil(maxX / Block.SIZE)*Block.SIZE);
        for (int i = (int)roundedStartingPoint; i <= (int) roundedEndingPoint ; i+= Block.SIZE) {
            float groundHeight = groundHeightAt(i);
            for (int j = 0;  groundHeight+ j* Block.SIZE< windowDimensions.y() ; j++) {
                Block newBlock = new Block(new Vector2(i,groundHeight + (j*Block.SIZE)),renderable);
                newBlock.setTag(TAG);
                gameObjects.addGameObject(newBlock);
            }

        }


    }
}
