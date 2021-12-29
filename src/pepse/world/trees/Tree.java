package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.WorldGridConvertor;
import pepse.world.Block;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * Responsible for the creation and management of trees.
 */
public class Tree {

    private final TreeMap<Integer, Set<GameObject>> treesInWorld = new TreeMap<>();
    private Set<Integer> activeTrees = new HashSet<>();
    private final GameObjectCollection objectCollection;
    private final Function<Float, Float> getTreeBaseHeight;

    /**
     * Unit of measures in Blocks
     */
    private static final int TREE_HEIGHT = 10;
    private static final Color TREE_COLOR = new Color(100, 50, 20);

    /**
     * Constructor
     * @param objectCollection - gamObjectCollection
     * @param getTreeBaseHeight - the function which represents the height of the terrain.
     */
    public Tree(GameObjectCollection objectCollection, Function<Float, Float> getTreeBaseHeight) {

        this.objectCollection = objectCollection;
        this.getTreeBaseHeight = getTreeBaseHeight;
    }

    /**
     * This method creates trees in a given range of x-values.
     *
     * @param minX - The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxX - The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX) {
        Random rand = new Random();
        Set<Integer> newTrees = new HashSet<>();
        RectangleRenderable rectangle = new RectangleRenderable(TREE_COLOR);
        for (int i = minX; i <= maxX; i++) {
            if ((treesInWorld.size() > 0) && i >= treesInWorld.firstKey() && i <= treesInWorld.lastKey()) {
                if (treesInWorld.containsKey(i)) newTrees.add(i);
                continue;
            }
            if (rand.nextInt(10) == 0) {
                treeMapCreator(i,rectangle);
                newTrees.add(i);
            }
        }
        newTrees.forEach(this::addTreeAt);
        activeTrees.removeAll(newTrees);
        activeTrees.forEach(this::removeTreeAt);
        activeTrees = newTrees;
    }

    /**
     * adding trees to object collection
     * @param x - represent a tree in the set
     */
    private void addTreeAt(int x){
        if (activeTrees.contains(x)) return;
        Set<GameObject> blockSet = treesInWorld.get(x);
        for(GameObject block : blockSet){
            int layer = block.getTag().equals(Leaf.LEAF_TAG) ?
                    Layer.STATIC_OBJECTS + 1 : Layer.STATIC_OBJECTS;
            objectCollection.addGameObject(block, layer);
        }
    }

    /**
     * removing trees from object collection
     * @param x - represent a tree in the set
     */
    private void removeTreeAt(int x){
        Set<GameObject> blockSet = treesInWorld.get(x);
        for(GameObject block : blockSet){
            int layer = block.getTag().equals(Leaf.LEAF_TAG) ?
                    Layer.STATIC_OBJECTS + 1 : Layer.STATIC_OBJECTS;
            objectCollection.removeGameObject(block, layer);
        }
    }

    /**
     * Add a tree to the treeHashMap
     * @param index - index of a specific tree
     * @param rectangle - renderer of the tree
     */
        private void treeMapCreator(int index, RectangleRenderable rectangle){
            Set<GameObject> set = new HashSet<>();
            int groundHeight = getTreeBaseHeight.apply((float) index).intValue();
            for (int j = groundHeight - TREE_HEIGHT; j < groundHeight; j++) {
                Block subTreeTrunk = new Block(WorldGridConvertor.gridToWorld(index, j), rectangle);
                set.add(subTreeTrunk);
            }
            for (int j = -2 ; j < 3; j++) {
                for (int k = -2 ; k < 3; k++) {
                    Vector2 LeafPos = new Vector2(index + j, groundHeight - TREE_HEIGHT + k);
                    GameObject newLeaf = Leaf.create(LeafPos);
                    set.add(newLeaf);
                }
            }
            treesInWorld.put(index,set);
        }
}

