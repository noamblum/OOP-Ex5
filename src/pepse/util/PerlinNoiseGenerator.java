package pepse.util;

import java.util.Objects;
import java.util.Random;

/**
 * A class which generates perlin noise based on a seed
 */
public class PerlinNoiseGenerator {

    /**
     * The seed used in every noise generation
     */
    private final int globalSeed;

    /**
     * Constructor
     *
     * @param globalSeed The generator's global seed which affects all random number generation
     */
    public PerlinNoiseGenerator(int globalSeed) {
        this.globalSeed = globalSeed;
    }

    /**
     * Generates a noise map
     *
     * @param localSeed  A seed used together with the global one to generate the map
     * @param mapWidth   The map's required width.
     * @param maxHeight  The map's height
     * @param amplitude  The generated noise's amplitude
     * @param waveLength The generated noise's wave length
     * @param octaves    How many octaves to generate with
     * @param divisor    Dictates how small octaves are
     * @return An array containing the generated noise
     */
    public float[] generateNoise(int localSeed,
                                 int mapWidth,
                                 float maxHeight,
                                 float amplitude,
                                 int waveLength,
                                 int octaves,
                                 int divisor) {

        float[][] octavesNoiseMap = new float[octaves][];
        for (int i = 0; i < octaves; i++) {
            if (waveLength == 0) continue;
            int octaveSeed = Objects.hash(globalSeed, localSeed, i);
            octavesNoiseMap[i] = generateNoiseSingleOctave(octaveSeed,
                    mapWidth, maxHeight, amplitude, waveLength);
            amplitude /= divisor;
            waveLength /= divisor;
        }
        return combineMaps(octavesNoiseMap);
    }

    /**
     * Combines several noise maps into one
     * @param maps The maps to combine
     * @return The combined map
     */
    private float[] combineMaps(float[][] maps){
        float[] combinedMap = new float[maps[0].length];
        for (int i = 0; i < maps[0].length; i++) {
            float combinedVal = 0;
            for (float[] map : maps) {
                if (map == null || map.length == 0) continue;
                combinedVal += map[i];
            }
            combinedMap[i] = combinedVal;
        }
        return combinedMap;
    }

    /**
     * Generates a noise map for a single octave
     *
     * @param localSeed  A seed used to generate the map
     * @param mapWidth   The map's required width.
     * @param maxHeight  The map's height
     * @param amplitude  The generated noise's amplitude
     * @param waveLength The generated noise's wave length
     * @return An array containing the generated noise
     */
    private float[] generateNoiseSingleOctave(int localSeed,
                                              int mapWidth,
                                              float maxHeight,
                                              float amplitude,
                                              int waveLength) {
        float[] noiseMap = new float[mapWidth];
        Random rng = new Random(localSeed);
        float leftEdgeHeight = rng.nextFloat();
        float rightEdgeHeight = rng.nextFloat();
        for (int x = 0; x < mapWidth; x++) {
            if (x % waveLength == 0) {
                leftEdgeHeight = rightEdgeHeight;
                rightEdgeHeight = rng.nextFloat();
                noiseMap[x] = (maxHeight / 2) + (leftEdgeHeight * amplitude);
            } else {
                float calculatedHeight = cosineInterpolate(leftEdgeHeight, rightEdgeHeight,
                        (float) (x % waveLength) / waveLength);
                noiseMap[x] = (maxHeight / 2) + (calculatedHeight * amplitude);
            }
        }
        return noiseMap;
    }

    /**
     * Generates a data point between two given ones to create a continuum
     *
     * @param leftEdgeHeight      The height of the left data point
     * @param rightEdgeHeight     The height of the right data point
     * @param interpolationFactor The percentage of distance between the two points which we generate data for
     * @return The height of the requested point
     */
    private float cosineInterpolate(float leftEdgeHeight, float rightEdgeHeight, float interpolationFactor) {
        float ft = (float) (interpolationFactor * Math.PI);
        float linearFactor = (float) ((1 - Math.cos(ft)) * 0.5f);
        return leftEdgeHeight * (1 - linearFactor) + rightEdgeHeight * linearFactor;
    }
}
