package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.WorldGridConvertor;
import pepse.world.Block;

import java.awt.*;

/**
 * Responsible for the creation and management of leaves.
 */
public class Leaf {

    public static final String LEAF_TAG = "Leaf";
    private static final Color LEAF_COLOR = new Color(50, 200, 30);

    /**
     * Creates a Leaf
     *
     * @param topLeftCorner - topLeftCorner of a leaf
     * @return newLeaf
     */
    public static GameObject create(Vector2 topLeftCorner) {
        RectangleRenderable renderable = new RectangleRenderable(LEAF_COLOR);
        Block newLeaf = new Block(WorldGridConvertor.gridToWorld(topLeftCorner), renderable);
        newLeaf.physics().preventIntersectionsFromDirection(null);
        newLeaf.setTag(LEAF_TAG);
        return newLeaf;

    }
}
