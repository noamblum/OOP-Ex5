package pepse.util;

import danogl.util.Vector2;
import pepse.world.Block;

/**
 * A utility class used to convert between continuous world coordinates and discrete block grid coordinates
 */
public class WorldGridConvertor {

    /**
     * Convert the specified world coordinate to a grid coordinate
     * @param worldX The X coordinate
     * @param worldY The Y coordinate
     * @return The corresponding grid coordinate
     */
    public static Vector2 worldToGrid(float worldX, float worldY){
        int gridX = (int)(worldX / Block.SIZE);
        int gridY = (int)(worldY / Block.SIZE);

        return new Vector2(gridX, gridY);
    }

    /**
     * Convert the specified world coordinate to a grid coordinate
     * @param worldCoordinates The world coordinates
     * @return The corresponding grid coordinate
     */
    public static Vector2 worldToGrid(Vector2 worldCoordinates){
        return worldToGrid(worldCoordinates.x(), worldCoordinates.y());
    }

    /**
     * Convert the specified grid coordinates into world coordinates
     * @param gridX The X coordinate on the grid
     * @param gridY The Y coordinate on the grid
     * @return The corresponding world coordinate
     */
    public static Vector2 gridToWorld(float gridX, float gridY){
        float worldX = gridX * Block.SIZE;
        float worldY = gridY * Block.SIZE;

        return new Vector2(worldX, worldY);
    }

    /**
     * Convert the specified grid coordinates into world coordinates
     * @param gridCoordinates The grid coordinates
     * @return The corresponding world coordinate
     */
    public static Vector2 gridToWorld(Vector2 gridCoordinates){
        return gridToWorld(gridCoordinates.x(), gridCoordinates.y());
    }
}
