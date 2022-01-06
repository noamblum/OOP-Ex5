package pepse.world.trees;

import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.WorldGridConvertor;
import pepse.world.Block;

import java.awt.*;
import java.util.Random;

/**
 * Responsible for the creation and management of leaves.
 */
public class Leaf extends Block {

    public static final String LEAF_TAG = "Leaf";
    private final Vector2 initialPosition;
    private final Random rand = new Random();


    /**
     * Creates a leaf and determine if the leaf will fall from the tree during the game.
     * @param topLeftCorner initial position
     */
    public Leaf(Vector2 topLeftCorner){
        super(WorldGridConvertor.gridToWorld(topLeftCorner), new RectangleRenderable( new Color(50, 200, 30)));
        initialPosition = WorldGridConvertor.gridToWorld(topLeftCorner);
         this.physics().preventIntersectionsFromDirection(Vector2.UP);
        this.physics().preventIntersectionsFromDirection(Vector2.LEFT);
        this.physics().preventIntersectionsFromDirection(Vector2.RIGHT);
        physics().setMass(1);
        new ScheduledTask(this, rand.nextFloat(),false, this::wrapperForCreatingTransition);
        this.setTag(LEAF_TAG);
        new ScheduledTask(this, rand.nextInt(30-10)+10, false, this::endOfLeafLife);
    }

    /**
     * Responsible for the behavior of the leaf if it falls during the game.
     */
    private void endOfLeafLife(){
            fallingDown();
            this.renderer().fadeOut(setFadeOutTime(rand),this::reposition);
    }

    /**
     * Sets the falling leaf at initial position after it touching the ground
     */
    private void reposition(){
       this.transform().setVelocityX(0);
        this.transform().setVelocityY(0);
        this.renderer().fadeIn(0.1f);
        this.setCenter(initialPosition);
        new ScheduledTask(this, rand.nextInt(30-10)+10, false, this::endOfLeafLife);
    }

    /**
     * Sets the movement of a leaf while it is falling down.
     */
    private void fallingDown() {
        this.transform().setVelocityY(50f);
        new Transition<>(
                this,
                this.transform()::setVelocityX,
                -50f,
                50f,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                1.2f,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH ,
                null
                        );
    }

    /**
     * Random fade out time for a leaf.
     * @param random object for random a number
     * @return fade out time.
     */
    private float setFadeOutTime(Random random) {
        return  (float) random.nextInt(20-10)+10;
    }

    /**
     * Activating Transition function for the movement of the leaf while it is falling down.
     */
    private void wrapperForCreatingTransition(){
        new Transition<>(
                this,
                this.renderer()::setRenderableAngle,
                -20f,
                20f,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                2,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
    }
}
