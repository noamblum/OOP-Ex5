package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.WorldGridConvertor;

import java.awt.*;
import java.util.Random;

/**
 * Responsible for the creation and management of leaves.
 */
public class Leaf extends GameObject {

    public static final String LEAF_TAG = "Leaf";

    private static final Color LEAF_COLOR = new Color(50, 200, 30);

    /**
     * Animation constants
     */
    private static final int MAX_LIFE_TIME = 120;
    private static final int MIN_LIFE_TIME = 0;
    private static final float FADE_IN_TIME = 0.4f;
    private static final int MIN_FADE_OUT_TIME = 5;
    private static final int MAX_FADE_OUT_TIME = 10;
    private static final float FALLING_VELOCITY = 50;
    private static final float FALLING_TRANSITION_TIME = 1.2f;
    private static final float STATIC_WIND_ANGLE = 20;
    private static final float STATIC_WIND_TRANSITION_TIME = 2;


    /**
     * The leaf's position on the tree
     */
    private final Vector2 initialPosition;

    /**
     * The global game object collection
     */
    private final GameObjectCollection objectCollection;

    /**
     * The layer in which the leaves are in while on the tree
     */
    private final int staticLeafLayer;

    /**
     * The layer of the leaves that can hit the ground
     */
    private final int fallingLeafLayer;

    /**
     * The randomizer used to generate life cycle times
     */
    private final Random rand = new Random();

    /**
     * The leaf's movement in the wind
     */
    private Transition<?> staticLeafMovement;

    /**
     * The leaf's sideways movement while falling down
     */
    private Transition<?> fallingLeafMovement;

    /**
     * A flag specifying whether the leaf should add itself to the game when animations are complete
     */
    private boolean isActive = false;


    /**
     * Constructor
     *
     * @param topLeftCorner    initial position
     * @param objectCollection The global game object collection
     * @param staticLeafLayer The layer for the leaves on the trees
     * @param fallingLeafLayer THe layer for falling leaves
     */
    public Leaf(Vector2 topLeftCorner, GameObjectCollection objectCollection, int staticLeafLayer, int fallingLeafLayer) {
        super(WorldGridConvertor.gridToWorld(topLeftCorner),
                Vector2.ONES.mult(30), new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR)));
        initialPosition = WorldGridConvertor.gridToWorld(topLeftCorner);
        this.objectCollection = objectCollection;
        this.staticLeafLayer = staticLeafLayer;
        this.fallingLeafLayer = fallingLeafLayer;
        this.staticLeafMovement = null;
        this.fallingLeafMovement = null;
        this.setTag(LEAF_TAG);
        startLeafLife();
    }

    /**
     * Stop all leaf movement when hitting the ground
     *
     * @param other The other ground block
     * @param collision The collision object
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (staticLeafMovement != null) {
            removeComponent(staticLeafMovement);
            staticLeafMovement = null;
        }
        if (fallingLeafMovement != null) {
            removeComponent(fallingLeafMovement);
            fallingLeafMovement = null;
        }
        transform().setVelocityY(0);
        transform().setVelocityX(0);
    }

    /**
     * Sets whether the leaf animations can be activated
     *
     * @param active The state
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Makes the leaf fall and fade out
     */
    private void endOfLeafLife() {
        fallingDown();
        this.renderer().fadeOut(getFadeOutTime(rand), this::startLeafLife);
    }

    /**
     * Starts moving in the wind and falling after a set time
     */
    private void startLeafLife() {
        if (isActive) {
            objectCollection.removeGameObject(this, fallingLeafLayer);
            objectCollection.addGameObject(this, staticLeafLayer);
        }
        if (fallingLeafMovement != null) {
            removeComponent(fallingLeafMovement);
            fallingLeafMovement = null;
        }
        this.transform().setVelocityX(0);
        this.transform().setVelocityY(0);
        this.renderer().fadeIn(FADE_IN_TIME);
        this.setCenter(initialPosition);
        if (staticLeafMovement == null) {
            new ScheduledTask(this, rand.nextFloat(), false, this::createWindMovement);
        }

        new ScheduledTask(this,
                rand.nextInt(MAX_LIFE_TIME - MIN_LIFE_TIME + 1) + MIN_LIFE_TIME,
                false,
                this::endOfLeafLife);
    }

    /**
     * Sets the movement of a leaf while it is falling down.
     */
    private void fallingDown() {
        if (isActive) {
            objectCollection.removeGameObject(this, staticLeafLayer);
            objectCollection.addGameObject(this, fallingLeafLayer);
        }
        this.transform().setVelocityY(FALLING_VELOCITY);
        fallingLeafMovement = new Transition<>(
                this,
                this.transform()::setVelocityX,
                -FALLING_VELOCITY,
                FALLING_VELOCITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                FALLING_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
    }

    /**
     * Random fade out time for a leaf.
     *
     * @param random object for random a number
     * @return fade out time.
     */
    private float getFadeOutTime(Random random) {
        return ((MAX_FADE_OUT_TIME - MIN_FADE_OUT_TIME) * random.nextFloat()) + MIN_FADE_OUT_TIME;
    }

    /**
     * Activating Transition function for the movement of the leaf while it is on the tree.
     */
    private void createWindMovement() {
        staticLeafMovement = new Transition<>(
                this,
                this.renderer()::setRenderableAngle,
                -STATIC_WIND_ANGLE,
                STATIC_WIND_ANGLE,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                STATIC_WIND_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
    }
}
