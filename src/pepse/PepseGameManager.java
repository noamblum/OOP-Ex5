package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;

import java.awt.*;

public class PepseGameManager extends GameManager {

    private static final float DAY_NIGHT_CYCLE_TIME = 10;
    private static final int TARGET_FRAMERATE = 60;
    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);

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
        Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
        Night.create(gameObjects(), Layer.FOREGROUND,
                windowController.getWindowDimensions(), DAY_NIGHT_CYCLE_TIME);
        Terrain terrain = new Terrain(gameObjects(),3,windowController.getWindowDimensions(),1);
        terrain.createInRange(0,30);
        GameObject sun = Sun.create(windowController.getWindowDimensions(),3, gameObjects(), Layer.BACKGROUND+1);
        SunHalo.create(gameObjects(),sun, SUN_HALO_COLOR, Layer.BACKGROUND + 10);
    }


    public static void main(String[] args) {
        new PepseGameManager("PEPSE").run();
    }
}
