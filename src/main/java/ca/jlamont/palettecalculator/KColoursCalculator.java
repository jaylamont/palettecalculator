package ca.jlamont.palettecalculator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class KColoursCalculator implements KMeansCalculator {

	public int[] findKMeans(int[] pixels, int k) {

		float[][] eYUVPixels = new float[pixels.length][];

		for (int i = 0; i < pixels.length; ++i) {
			eYUVPixels[i] = ColourUtils.getEuclideanYUVFromColour(pixels[i]);
			System.out.println(Arrays.toString(eYUVPixels[i]));
		}

		float[][] ranges = { { 0, 255 }, { 0, 255 }, { 0, 255 } };

		// float[][] startingValues = EuclidUtils.getDistributedPoints(ranges,
		// k);

		float[][] startingValues = new float[k][];

		Random random = new Random();

		for (int i = 0; i < startingValues.length; ++i) {
			startingValues[i] = eYUVPixels[random
					.nextInt(eYUVPixels.length - 1)];
		}

		for (int i = 0; i < 50; ++i) {
			System.out.println("==========\nSTARTING VALUES:");
			for (float[] f : startingValues) {
				System.out.println(Arrays.toString(f));
			}
			System.out.println("==========");

			float[][][] sets = getSets(eYUVPixels, k, startingValues);

			for (int j = 0; j < sets.length; ++j) {
				startingValues[j] = findCentroidFromSet(sets[j]);
			}
		}

		int[] result = new int[k];

		for (int i = 0; i < k; ++i) {
			result[i] = ColourUtils.getColourFromYUV(ColourUtils
					.getYUVFromEuclideanYUV(startingValues[i]));
		}

		return result;
	}

	protected float[][][] getSets(final float[][] pixels, int k,
			float[][] initValues) {
		Set<float[]>[] resultSets = new HashSet[k];

		/*
		 * for (Set<float[]> s : resultSets) { s = new HashSet<float[]>(); }
		 */

		for (int i = 0; i < resultSets.length; ++i) {
			resultSets[i] = new HashSet<float[]>();
		}

		for (float[] pixel : pixels) {
			float minDistance = -1;
			int closestPoint = 0;

			for (int i = 0; i < k; ++i) {
				float calculatedDistance = EuclidUtils
						.calculateEuclideanDistance(pixel, initValues[i]);

				if (calculatedDistance < minDistance || minDistance == -1) {
					minDistance = calculatedDistance;
					closestPoint = i;
				}
			}

			resultSets[closestPoint].add(pixel);
		}

		float[][][] result = new float[k][][];

		for (int i = 0; i < k; ++i) {
			result[i] = new float[resultSets[i].size()][];
			resultSets[i].toArray(result[i]);
		}

		return result;
	}

	protected float[] findCentroidFromSet(float[][] set) {
		float[] result = new float[set[0].length];

		for (float[] s : set) {
			for (int i = 0; i < s.length; ++i) {
				result[i] += s[i];
			}
		}

		for (int i = 0; i < result.length; ++i) {
			result[i] = result[i] / set.length;
		}

		return result;
	}
	// private int[]

}
