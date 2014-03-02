package ca.jlamont.palettecalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RGBPaletteCalculator extends PaletteCalculator {
	public static int[] calculatePalette(int[] pixels, int n) {
		if (n == 0)
			return null;

		HSVBuckets buckets = new HSVBuckets(32, 25, 25);

		for (int pixel : pixels) {
			buckets.addColour(pixel);
		}

		int backgroundColour = buckets.getMostPrevalentShade();

		int[] contrastingColours = buckets
				.getContrastingShades(backgroundColour);

		System.out.println(contrastingColours.length + " contrasting pixels");

		for (int c : contrastingColours) {
			System.out
					.println(Arrays.toString(ColourUtils.getRGBFromColour(c)));
		}

		int[] result = new int[1];

		result[0] = backgroundColour;

		int[] kMeansResult = getKMeans(contrastingColours, n - 1);

		return CustomArrayUtils.addAll(result, kMeansResult);

		// return getKMeans(pixels, n);
	}

	protected static int[] getKMeans(int[] values, int k) {
		float[][] rgbValues = new float[values.length][];

		for (int i = 0; i < rgbValues.length; ++i) {

			rgbValues[i] = CustomArrayUtils.toFloat(ColourUtils
					.getRGBFromColour(values[i]));

			// ColourUtils.convertHSVToEuclideanHSV(rgbValues[i]);
			// System.out.println(Arrays.toString(hsvValues[i]));
		}

		System.out.println("Index 0 after value: "
				+ Arrays.toString(rgbValues[0]));

		float[][] centroids = new float[k][rgbValues[0].length];

		// int validCentroids = k;

		// int randomIndexes [][] = new int[k][3];

		for (int i = 0; i < 3; ++i) {
			int[] randomDimensionValues = RandomUtils.getRandomInts(0, 255, k);

			for (int j = 0; j < k; ++j) {
				centroids[j][i] = randomDimensionValues[j];
			}
		}

		for (int i = 0; i < 100; ++i) {
			List<float[]>[] groups = new List[centroids.length];

			for (int j = 0; j < groups.length; ++j) {
				groups[j] = new ArrayList<float[]>();
			}

			for (float[] rgb : rgbValues) {
				float minDistance = Float.MAX_VALUE;
				int minIndex = -1;

				for (int j = 0; j < groups.length; ++j) {
					/*
					 * float distance = ColourUtils
					 * .calculateEuclideanDistanceForHSV(hsv, centroids[j]);
					 */
					float distance = EuclidUtils.calculateEuclideanDistance(
							rgb, centroids[j]);

					// System.out.println("Distance " + distance + " vs. min "
					// + minDistance);

					if (distance < minDistance) {
						minDistance = distance;
						minIndex = j;
					}
				}

				groups[minIndex].add(rgb);
			}

			System.out.println("====================");
			System.out.println("Group report for iteration " + i);
			for (int j = 0; j < groups.length; ++j) {
				System.out.println("Group " + j + ": " + groups[j].size());
			}
			System.out.println("====================");

			for (int j = 0; j < centroids.length; ++j) {
				if (groups[j].size() != 0) {
					centroids[j] = getAverageShade(groups[j]);
					System.out.println("Centroid " + j + ": "
							+ Arrays.toString(centroids[j]));
				} else {
					int[] coords = RandomUtils.getRandomInts(0, 255, 3);
					for (int l = 0; l < coords.length; ++l) {
						centroids[j][l] = coords[l];
					}
				}
			}
			System.out.println("====================");
		}

		int[] result = new int[k];

		for (int i = 0; i < k; ++i) {
			System.out.println(Arrays.toString(centroids[i]));

			int intRGB[] = CustomArrayUtils.toInt(centroids[i]);

			result[i] = ColourUtils.getColourFromHSV(centroids[i]);
		}

		return result;
	}

	protected static float[] getAverageShade(List<float[]> shades) {
		float[] result = null;

		for (float[] shade : shades) {
			if (result == null)
				result = new float[shade.length];

			for (int i = 0; i < result.length; ++i)
				result[i] += shade[i];
		}

		for (int i = 0; i < result.length; ++i) {
			result[i] = result[i] / shades.size();
		}

		return result;
	}

	protected static int getClosestCentroid(float[] value, float[][] centroids) {
		float minDistance = -1;
		int indexOfClosest = 0;

		for (int i = 0; i < centroids.length; ++i) {
			// float distance = EuclidUtils.calculateEuclideanDistance(value,
			// centroids[i]);

			float distance = ColourUtils.calculateEuclideanDistanceForHSV(
					value, centroids[i]);

			if (minDistance == -1 || distance < minDistance) {
				minDistance = distance;
				indexOfClosest = i;
			}
		}

		return indexOfClosest;
	}
}
