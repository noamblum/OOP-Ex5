package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.WorldGridConvertor;
import pepse.world.Block;

import java.awt.*;
import java.util.*;
import java.util.function.Function;

/**
 * Responsible for the creation and management of trees.
 */
public class Tree {

    private static final Color TREE_TRUNK_COLOR = new Color(100, 50, 20);

    /**
     * Unit of measures in Blocks
     */
    private static final int MIN_TREE_HEIGHT = 8;
    private static final int MAX_TREE_HEIGHT = 13;
    private static final int TREE_GENERATION_CHANCE = 10;
    private static final int LEAF_GENERATION_CHANCE = 5;
    private static final int MIN_LEAF_RADIUS = 1;
    private static final int MAX_LEAF_RADIUS = 3;

    private final int seed;

    private final Map<Integer, Set<GameObject>> activeTrees = new HashMap<>();
    private final Set<Integer> treesInWorld = new HashSet<>();
    private final GameObjectCollection objectCollection;
    private final Function<Float, Float> getTreeBaseHeight;

    /**
     * The different layers
     */
    private final int treeTrunkLayer;
    private final int staticLeafLayer;
    private final int fallingLeafLayer;

    /**
     * The index of the leftmost calculated tree location
     */
    private int minGeneratedX = 0;
    /**
     * The index of the rightmost calculated tree location
     */
    private int maxGeneratedX = 0;

    /**
     * Constructor
     * @param objectCollection - gamObjectCollection
     * @param getTreeBaseHeight - the function which represents the height of the terrain.
     */
    public Tree(GameObjectCollection objectCollection, Function<Float, Float> getTreeBaseHeight,
                int treeTrunkLayer, int staticLeafLayer, int fallingLeafLayer, int seed) {

        this.objectCollection = objectCollection;
        this.getTreeBaseHeight = getTreeBaseHeight;
        this.treeTrunkLayer = treeTrunkLayer;
        this.staticLeafLayer = staticLeafLayer;
        this.fallingLeafLayer = fallingLeafLayer;
        this.seed = seed;
    }

    /**
     * This method creates trees in a given range of x-values.
     *
     * @param minX - The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxX - The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX) {
        Set<Integer> newTrees = new HashSet<>();
        for (int i = minX; i <= maxX; i++) {
            Random rand = new Random(Objects.hash(seed, i));
            if (i >= minGeneratedX && i <= maxGeneratedX) {
                if (treesInWorld.contains(i)) newTrees.add(i);
            }
            else if (rand.nextInt(TREE_GENERATION_CHANCE) == 0) {
                treesInWorld.add(i);
                newTrees.add(i);
            }
        }
        newTrees.forEach(this::addTreeAt);
        Set<Integer> treesToRemove = new HashSet<>();
        for(Integer tree : activeTrees.keySet()){
            if (!newTrees.contains(tree)) treesToRemove.add(tree);
        }
        treesToRemove.forEach(this::removeTreeAt);
        minGeneratedX = Math.min(minX, minGeneratedX);
        maxGeneratedX = Math.max(maxX, maxGeneratedX);
    }

    /**
     * adding trees to object collection
     * @param x - represent a tree in the set
     */
    private void addTreeAt(int x){
        if (activeTrees.containsKey(x)) return;
        Set<GameObject> treeBlockSet = new HashSet<>();
        Random treeGenerationRandom = new Random(Objects.hash(seed, x));
        int treeHeight = treeGenerationRandom.nextInt(MAX_TREE_HEIGHT-MIN_TREE_HEIGHT + 1) + MIN_TREE_HEIGHT;
        int groundHeight = getTreeBaseHeight.apply((float) x).intValue();
        buildTreeTrunk(x, treeBlockSet, treeHeight, groundHeight);
        buildLeaves(x, treeBlockSet, treeGenerationRandom, treeHeight, groundHeight);

        activeTrees.put(x, treeBlockSet);
    }

    private void buildLeaves(int x, Set<GameObject> treeBlockSet, Random treeGenerationRandom, int treeHeight, int groundHeight) {
        for (int i = -MAX_LEAF_RADIUS ; i < MAX_LEAF_RADIUS; i++) {
            for (int j = -MAX_LEAF_RADIUS ; j < MAX_LEAF_RADIUS; j++) {
                boolean leafInMinimalRange = Math.abs(i) <= MIN_LEAF_RADIUS && Math.abs(j) <= MIN_LEAF_RADIUS;
                if (leafInMinimalRange || treeGenerationRandom.nextInt(LEAF_GENERATION_CHANCE) != 0) {
                    Vector2 LeafPos = new Vector2(x + i + 1, groundHeight - treeHeight + j);
                    Leaf newLeaf = new Leaf(LeafPos, objectCollection, staticLeafLayer, fallingLeafLayer);
                    treeBlockSet.add(newLeaf);
                    objectCollection.addGameObject(newLeaf, staticLeafLayer);
                    newLeaf.setActive(true);
                }
            }
        }
    }

    private void buildTreeTrunk(int x, Set<GameObject> treeBlockSet, int treeHeight, int groundHeight) {
        Renderable trunkRenderable = new RectangleRenderable(TREE_TRUNK_COLOR);
        for (int j = groundHeight - treeHeight; j < groundHeight; j++) {
            Block subTreeTrunk = new Block(WorldGridConvertor.gridToWorld(x, j), trunkRenderable);
            treeBlockSet.add(subTreeTrunk);
            objectCollection.addGameObject(subTreeTrunk, treeTrunkLayer);
        }
    }

    /**
     * removing trees from object collection
     * @param x - represent a tree in the set
     */
    private void removeTreeAt(int x){
        Set<GameObject> blockSet = activeTrees.get(x);
        for(GameObject block : blockSet){
            boolean isLeaf = block.getTag().equals(Leaf.LEAF_TAG);
            if (isLeaf){
                Leaf leaf = (Leaf) block;
                leaf.setActive(false);
                objectCollection.removeGameObject(leaf, staticLeafLayer);
                objectCollection.removeGameObject(leaf, fallingLeafLayer);
            }
            else objectCollection.removeGameObject(block, treeTrunkLayer);
        }
        activeTrees.remove(x);
    }
}

