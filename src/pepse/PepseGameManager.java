package pepse;

import danogl.GameManager;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
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

    private int minTerrainX;
    private int maxTerrainX;

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
        minTerrainX = (int)(windowGridDimensions.x() * -1.5f);
        maxTerrainX = (int)(windowGridDimensions.x() * 1.5f);
        Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
        Night.create(gameObjects(), Layer.FOREGROUND,
                windowController.getWindowDimensions(), DAY_NIGHT_CYCLE_TIME);
        Terrain terrain = new Terrain(gameObjects(),Layer.STATIC_OBJECTS,windowController.getWindowDimensions(),1);
        terrain.createInRange(minTerrainX,maxTerrainX);
        Avatar avatar = Avatar.create(gameObjects(), Layer.DEFAULT, new Vector2(0,
                        terrain.groundHeightAt(0) - Avatar.AVATAR_SIZE),
                inputListener,
                imageReader);
        setCamera(new Camera(avatar,Vector2.UP.mult(150),windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
    }

    public static void main(String[] args) {
        new PepseGameManager("PEPSE").run();
    }
}
