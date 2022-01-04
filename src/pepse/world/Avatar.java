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

    private static final int MAX_FLIGHT_ENERGY = 100;
    private static final int MIN_FLIGHT_ENERGY = 0;
    private static final float VELOCITY_X = 300;
    private static final float VELOCITY_Y = -300;
    private static final float MAX_VELOCITY_Y = 1000;
    private static final float GRAVITY = 500;
    private static final String IMAGE_RIGHT = "assets/hero_r.png";
    private static final String IMAGE_LEFT = "assets/hero_l.png";

    private UserInputListener inputListener;
    private static Renderable renderableRight;
    private static Renderable renderableLeft;
    private float currentFlightEnergy = MAX_FLIGHT_ENERGY;
    private boolean flightMode = false;

    private Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
    }

    /**
     * Creates a new avatar
     * @param gameObjects The global game object collection
     * @param layer The layer to put the avatar on
     * @param topLeftCorner The top left corner of the avatar
     * @param inputListener The listener for keyboard presses
     * @param imageReader The object used to load avatar graphics
     * @return A newly generated avatar
     */
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

        handleArrowKeyMovement();
        handleJumpAndFlight();
    }

    /**
     *
     * @return The current amount of energy left for flight
     */
    public float getCurrentFlightEnergy(){return currentFlightEnergy;}

    private void handleJumpAndFlight() {
        if (getVelocity().y() > MAX_VELOCITY_Y) {
            Vector2 maxVelocity = new Vector2(getVelocity().x(), MAX_VELOCITY_Y);
            setVelocity(maxVelocity);
        }

        boolean isOnGround = getVelocity().y() == 0;
        if (isOnGround) flightMode = false;

        if (flightMode) currentFlightEnergy = Math.max(MIN_FLIGHT_ENERGY, currentFlightEnergy - 0.5f);
        else currentFlightEnergy = Math.min(MAX_FLIGHT_ENERGY, currentFlightEnergy + 0.5f);

        // Jump
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0)
            transform().setVelocityY(VELOCITY_Y);

        // Fly
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && inputListener.isKeyPressed(KeyEvent.VK_SHIFT) &&
            currentFlightEnergy > MIN_FLIGHT_ENERGY) {
            flightMode = true;
            transform().setVelocityY(VELOCITY_Y);
        }
    }

    /**
     * Handle left and right movement
     */
    private void handleArrowKeyMovement() {
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
    }
}
