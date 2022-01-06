package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.util.WorldGridConvertor;
import pepse.world.Avatar;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;
import java.util.Random;

public class PepseGameManager extends GameManager {

    private static final float DAY_NIGHT_CYCLE_TIME = 10;
    private static final int TARGET_FRAMERATE = 60;
    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);

    private static final int SUN_HALO_LAYER = 6;
    private static final int GROUND_LAYER = 7;
    private static final int AVATAR_LAYER = 2;
    private static final int TREE_TRUNK_LAYER = 3;
    private static final int STATIC_LEAF_LAYER = 4;
    private static final int FALLING_LEAF_LAYER = 5;
    private static final int[][] COLLIDING_LAYERS = {
            {GROUND_LAYER, AVATAR_LAYER},
            {GROUND_LAYER,FALLING_LEAF_LAYER},
            {AVATAR_LAYER, TREE_TRUNK_LAYER}
    };

    private Vector2 windowGridDimensions;

    /**
     * The X index of the leftmost rendered block
     */
    private int minTerrainX;

    /**
     * The X index of the rightmost rendered block
     */
    private int maxTerrainX;

    /**
     * How many blocks are rendered on the x-axis
     */
    private int renderWidth;

    private Avatar avatar;
    private Terrain terrain;
    private Tree trees;

    public PepseGameManager(String windowTitle) {
        super(windowTitle);
    }

    public PepseGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
    }

    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        int globalSeed = new Random().nextInt();
        windowController.setTargetFramerate(TARGET_FRAMERATE);
        windowGridDimensions = WorldGridConvertor.worldToGrid(windowController.getWindowDimensions());
        renderWidth = (int)windowGridDimensions.x() * 2;
        Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
        Night.create(gameObjects(), Layer.FOREGROUND,
                windowController.getWindowDimensions(), DAY_NIGHT_CYCLE_TIME);
        GameObject sun = Sun.create(windowController.getWindowDimensions(),DAY_NIGHT_CYCLE_TIME, gameObjects(),
                Layer.BACKGROUND + 1);
        SunHalo.create(gameObjects(),sun, SUN_HALO_COLOR, SUN_HALO_LAYER);


        terrain = new Terrain(gameObjects(),GROUND_LAYER,windowController.getWindowDimensions(),
                globalSeed);
        trees = new Tree(gameObjects(), terrain::groundGridHeightAt,
                TREE_TRUNK_LAYER, STATIC_LEAF_LAYER, FALLING_LEAF_LAYER, globalSeed);
        Vector2 avatarStartingPosition = new Vector2(0, terrain.groundHeightAt(0) - Avatar.AVATAR_SIZE);
        createTerrainInRange(avatarStartingPosition);
        avatar = Avatar.create(gameObjects(),
                AVATAR_LAYER,
                avatarStartingPosition,
                inputListener,
                imageReader);
        setCamera(new Camera(avatar,Vector2.UP.mult(150),windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
        showAvatarEnergy();
        defineLayerCollisions();
    }

    private void defineLayerCollisions() {
        for (int[] pair : COLLIDING_LAYERS){
            // Create a stub object so that layers are not empty when collisions are defined
            GameObject stub = new GameObject(Vector2.ZERO, Vector2.ZERO, null);
            gameObjects().addGameObject(stub, pair[0]);
            gameObjects().addGameObject(stub, pair[1]);
            gameObjects().layers().shouldLayersCollide(pair[0], pair[1], true);
            gameObjects().removeGameObject(stub, pair[0]);
            gameObjects().removeGameObject(stub, pair[1]);
        }
    }

    private void showAvatarEnergy() {
        TextRenderable text = new TextRenderable("0");
        GameObject playerLocation = new GameObject(Vector2.ZERO, Vector2.ONES.mult(30), text);
        playerLocation.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        playerLocation.addComponent((deltaTime) -> text.setString(
                String.format("Energy: %.1f", avatar.getCurrentFlightEnergy())));
        gameObjects().addGameObject(playerLocation, Layer.UI);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector2 avatarGridCoordinates = WorldGridConvertor.worldToGrid(avatar.getCenter());
        int minDistanceFromEdge = (int) Math.min(avatarGridCoordinates.x() - minTerrainX,
                maxTerrainX - avatarGridCoordinates.x());
        if (minDistanceFromEdge < windowGridDimensions.x() / 2){
            createTerrainInRange(avatarGridCoordinates);
        }
    }

    private void createTerrainInRange(Vector2 avatarGridCoordinates) {
        minTerrainX = (int) avatarGridCoordinates.x() - (renderWidth / 2);
        maxTerrainX = (int) avatarGridCoordinates.x() + (renderWidth / 2);
        terrain.createInRange(minTerrainX,maxTerrainX);
        trees.createInRange(minTerrainX,maxTerrainX);

    }


    public static void main(String[] args) {
        new PepseGameManager("PEPSE").run();
    }
}
