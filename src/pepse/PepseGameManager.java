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

public class PepseGameManager extends GameManager {

    private static final float DAY_NIGHT_CYCLE_TIME = 10;
    private static final int TARGET_FRAMERATE = 60;

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
        windowController.setTargetFramerate(TARGET_FRAMERATE);
        windowGridDimensions = WorldGridConvertor.worldToGrid(windowController.getWindowDimensions());
        renderWidth = (int)windowGridDimensions.x() * 2;
        Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
        Night.create(gameObjects(), Layer.FOREGROUND,
                windowController.getWindowDimensions(), DAY_NIGHT_CYCLE_TIME);


        terrain = new Terrain(gameObjects(),Layer.STATIC_OBJECTS,windowController.getWindowDimensions(),1);
        Vector2 avatarStartingPosition = new Vector2(0, terrain.groundHeightAt(0) - Avatar.AVATAR_SIZE);
        createTerrainInRange(avatarStartingPosition);
        avatar = Avatar.create(gameObjects(),
                Layer.DEFAULT,
                avatarStartingPosition,
                inputListener,
                imageReader);
        setCamera(new Camera(avatar,Vector2.UP.mult(150),windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));

        showPlayerCoordinates();
    }

    private void showPlayerCoordinates() {
        TextRenderable text = new TextRenderable("0");
        GameObject playerLocation = new GameObject(Vector2.ZERO, Vector2.ONES.mult(30), text);
        playerLocation.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        playerLocation.addComponent((deltaTime) -> text.setString(avatar.getCenter().toString()));
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
    }

    public static void main(String[] args) {
        new PepseGameManager("PEPSE").run();
    }
}
