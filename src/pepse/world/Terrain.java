package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.ProceduralPerlinMap;
import pepse.util.WorldGridConvertor;

import java.awt.*;
import java.util.*;

/**
 * Responsible for the creation and management of terrain.
 */
public class Terrain {
    public static final String TAG = "Terrain";

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int PERLIN_NOISE_BLOCK_WIDTH = 128;
    private static final int PERLIN_NOISE_AMPLITUDE_FACTOR = 3;
    private static final int PERLIN_NOISE_WAVE_LENGTH = 32;
    private static final int PERLIN_NOISE_OCTAVES = 4;
    private static final int PERLIN_NOISE_DIVISOR = 2;
    private static final float TERRAIN_RENDER_DISTANCE_FACTOR = 1.7f;
    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final Vector2 windowDimensions;
    private final ProceduralPerlinMap heightMap;

    /**
     * This map keeping the ground blocks and their location.
     */
    private final Map<Integer, Set<GameObject>> activeBlocks = new HashMap<>();

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
        this.windowDimensions = WorldGridConvertor.worldToGrid(windowDimensions).add(Vector2.ONES);
        // Add 1 vector because the convertor rounds down
        heightMap = new ProceduralPerlinMap(
                seed, PERLIN_NOISE_BLOCK_WIDTH, this.windowDimensions.y() / 2,
                this.windowDimensions.y() / PERLIN_NOISE_AMPLITUDE_FACTOR,
                PERLIN_NOISE_WAVE_LENGTH, PERLIN_NOISE_OCTAVES, PERLIN_NOISE_DIVISOR);
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
    public void createInRange(int minX, int maxX){
        minX = (int) WorldGridConvertor.worldToGrid(minX, 0).x();
        maxX = (int) WorldGridConvertor.worldToGrid(maxX, 0).x();
       createInGridRange(minX, maxX);
    }

    /**
     * Creates ground blocks in the range between x and y x.coordinates.
     *
     * @param minX - The lower bound of the given range
     * @param maxX - The upper bound of the given range
     */
    public void createInGridRange(int minX, int maxX) {
        for (int i = minX; i <= maxX; i++) {
            float baseGroundHeight = groundGridHeightAt(i);
            if (activeBlocks.containsKey(i)) continue;
            Set<GameObject> groundBlocks = new HashSet<>();
            for (int j = (int) baseGroundHeight;
                 j <= baseGroundHeight + windowDimensions.y() / TERRAIN_RENDER_DISTANCE_FACTOR;
                 j++) {
                RectangleRenderable groundBlockColor = new RectangleRenderable(
                        ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Block newBlock = new Block(WorldGridConvertor.gridToWorld(i, j), groundBlockColor);
                newBlock.setTag(TAG);
                groundBlocks.add(newBlock);
                // Only put the top two blocks in the colliding layer
                int layer = j <= baseGroundHeight + 1 ? groundLayer : groundLayer + 1;
                gameObjects.addGameObject(newBlock, layer);
            }
            activeBlocks.put(i, groundBlocks);
        }
        dropBlocksOutsideRange(minX, maxX);
    }

    /**
     *  Removes Blocks that are not in the range given.
     * @param minX left edge on the screen.
     * @param maxX right edge on the screen.
     */
    private void dropBlocksOutsideRange(int minX, int maxX) {
        for (Iterator<Integer> it = activeBlocks.keySet().iterator(); it.hasNext() ;){
            Integer coordinate = it.next();
            if ( coordinate < minX || coordinate > maxX){
                for (GameObject block: activeBlocks.get(coordinate)) {
                    gameObjects.removeGameObject(block, groundLayer);
                }
                it.remove();
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
        return windowDimensions.y() - heightMap.get((int)x);
    }
}
