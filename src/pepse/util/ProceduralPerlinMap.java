package pepse.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A class for procedurally generating infinite perlin noise maps
 */
public class ProceduralPerlinMap {

    private final PerlinNoiseGenerator perlinNoiseGenerator;
    private final int blockWidth;
    private final Map<Integer, float[]> perlinMaps = new HashMap<>();
    private final float maxHeight;
    private final float amplitude;
    private final int waveLength;
    private final int octaves;
    private final int divisor;

    public ProceduralPerlinMap(int seed,
                               int blockWidth,
                               float maxHeight,
                               float amplitude,
                               int waveLength,
                               int octaves,
                               int divisor) {
        perlinNoiseGenerator = new PerlinNoiseGenerator(seed);
        this.blockWidth = blockWidth;
        this.maxHeight = maxHeight;
        this.amplitude = amplitude;
        this.waveLength = waveLength;
        this.octaves = octaves;
        this.divisor = divisor;
    }

    /**
     * Get the value at the specified coordinate
     *
     * @param xCoordinate The x coordinate
     * @return The noise value at the specified coordinate
     */
    public float get(int xCoordinate) {
        int blockCoordinate = Math.floorDiv(xCoordinate, blockWidth);
        int prevBlockCoordinate = blockCoordinate - 1;
        int coordinateInBlock = xCoordinate - (blockCoordinate * blockWidth);
        int coordinateInPrevBlock = blockWidth + coordinateInBlock;

        if (!perlinMaps.containsKey(blockCoordinate)){
            perlinMaps.put(blockCoordinate,
                    perlinNoiseGenerator.generateNoise(blockCoordinate,blockWidth * 2,
                    maxHeight,amplitude,waveLength,octaves,divisor));
        }
        if (!perlinMaps.containsKey(prevBlockCoordinate)){
            perlinMaps.put(prevBlockCoordinate,
                    perlinNoiseGenerator.generateNoise(prevBlockCoordinate,blockWidth * 2,
                    maxHeight,amplitude,waveLength,octaves,divisor));
        }
        return (perlinMaps.get(prevBlockCoordinate)[coordinateInPrevBlock]
                + perlinMaps.get(blockCoordinate)[coordinateInBlock]) / 2;
    }
}
