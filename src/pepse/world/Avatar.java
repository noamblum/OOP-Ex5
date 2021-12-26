package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/**
 * The amazing player controlled avatar
 */
public class Avatar extends GameObject {

    public static final String AVATAR_TAG = "Avatar";
    public static final float AVATAR_SIZE = 100;

    private static final float VELOCITY_X = 300;
    private static final float VELOCITY_Y = -300;
    private static final float GRAVITY = 650;
    private static final String IMAGE_RIGHT = "assets/hero_r.png";
    private static final String IMAGE_LEFT = "assets/hero_l.png";

    private UserInputListener inputListener;
    private static Renderable renderableRight;
    private static Renderable renderableLeft;

    private Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
    }

    public static Avatar create(
            GameObjectCollection gameObjects,
            int layer,
            Vector2 topLeftCorner,
            UserInputListener inputListener,
            ImageReader imageReader) {
        renderableRight = imageReader.readImage(IMAGE_RIGHT, true);
        renderableLeft = imageReader.readImage(IMAGE_LEFT, true);
        Avatar avatar = new Avatar(topLeftCorner, Vector2.ONES.mult(AVATAR_SIZE), renderableRight);
        avatar.inputListener = inputListener;
        avatar.setTag(AVATAR_TAG);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            xVel -= VELOCITY_X;
            renderer().setRenderable(renderableLeft);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            xVel += VELOCITY_X;
            renderer().setRenderable(renderableRight);
        }
        transform().setVelocityX(xVel);
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0)
            transform().setVelocityY(VELOCITY_Y);
    }
}
