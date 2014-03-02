package ca.jlamont.palettecalculator;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.ArrayUtils;

import ca.jlamont.palettecalculator.ImagePaletteCalculator.ColourSpace;

public class PaletteCalculator {

	public static int[] calculatePalette(int[] pixels, int n) {

		System.out.println("=====calculatePalette START");

		long startTime = System.nanoTime();

		if (n == 0)
			return null;

		HSVBuckets buckets = new HSVBuckets(8, 8, 8);

		for (int pixel : pixels) {
			buckets.addColour(pixel);
		}

		int backgroundColour = buckets.getMostPrevalentShade();

		int[] contrastingColours = buckets
				.getContrastingShades(backgroundColour);

		System.out.println(contrastingColours.length + " contrasting pixels");

		int rangeCount = 0;

		for (int c : contrastingColours) {
			float[] hsvValue = ColourUtils.getHSVFromColour(c);
			// System.out
			// .println(Arrays.toString(ColourUtils.getRGBFromColour(c)));
		}

		int[] result = new int[1];

		result[0] = backgroundColour;

		int[] kMeansResult = getKMeans(contrastingColours, n - 1);

		long endTime = System.nanoTime();

		long duration = (endTime - startTime) / 1000000;

		System.out.println("=====calculatePalette END " + duration + "ms");

		return ArrayUtils.addAll(result, kMeansResult);

		// return getKMeans(pixels, n);
	}

	protected static int[] getKMeans(int[] values, int k) {
		float[][] hsvValues = new float[values.length][];

		for (int i = 0; i < hsvValues.length; ++i) {
			// if (i == 0) {
			// System.out.println("Input 0 " + values[0]);
			// }

			hsvValues[i] = ColourUtils.getHSVFromColour(values[i]);

			// if (i == 0)
			// System.out.println("Index 0 before value: "
			// + Arrays.toString(hsvValues[0]));

			ColourUtils.convertHSVToEuclideanHSV(hsvValues[i]);
			// System.out.println(Arrays.toString(hsvValues[i]));
		}

		// System.out.println("Index 0 after value: "
		// + Arrays.toString(hsvValues[0]));

		float[][] centroids = new float[k][hsvValues[0].length];

		// int validCentroids = k;

		// int randomIndexes [][] = new int[k][3];

		int[] randomHValues = RandomUtils.getRandomInts(0, 360, k);
		for (int j = 0; j < k; ++j) {
			centroids[j][0] = randomHValues[j];
		}

		int[] randomSValues = RandomUtils.getRandomInts(0,
				(int) ColourUtils.EUCLIDEAN_SATURATION_FACTOR, k);
		for (int j = 0; j < k; ++j) {
			centroids[j][1] = randomSValues[j];
		}

		int[] randomVValues = RandomUtils.getRandomInts(0,
				(int) ColourUtils.EUCLIDEAN_VALUE_FACTOR, k);
		for (int j = 0; j < k; ++j) {
			centroids[j][2] = randomVValues[j];
		}

		for (int i = 0; i < 100; ++i) {
			List<float[]>[] groups = new List[centroids.length];

			for (int j = 0; j < groups.length; ++j) {
				groups[j] = new ArrayList<float[]>();
			}

			for (float[] hsv : hsvValues) {
				float minDistance = Float.MAX_VALUE;
				int minIndex = -1;

				for (int j = 0; j < groups.length; ++j) {
					float distance = ColourUtils
							.calculateEuclideanDistanceForHSV(hsv, centroids[j]);

					// System.out.println("Distance " + distance + " vs. min "
					// + minDistance);

					if (distance < minDistance) {
						minDistance = distance;
						minIndex = j;
					}
				}

				groups[minIndex].add(hsv);
			}

			// System.out.println("====================");
			// System.out.println("Group report for iteration " + i);
			// for (int j = 0; j < groups.length; ++j) {
			// System.out.println("Group " + j + ": " + groups[j].size());
			// }
			// System.out.println("====================");

			for (int j = 0; j < centroids.length; ++j) {
				if (groups[j].size() != 0) {
					centroids[j] = getAverageShade(groups[j]);
					// System.out.println("Centroid " + j + ": "
					// + Arrays.toString(centroids[j]));
				} else {
					int[] coords = RandomUtils.getRandomInts(0, 360, 3);
					for (int l = 0; l < coords.length; ++l) {
						centroids[j][l] = coords[l];
					}
				}
			}
			// System.out.println("====================");
		}

		int[] result = new int[k];

		for (int i = 0; i < k; ++i) {
			System.out.println(Arrays.toString(centroids[i]));
			ColourUtils.convertEuclideanHSVToHSV(centroids[i]);
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

	public static void main(String args[]) {
		ImagePaletteCalculator ipc = new ImagePaletteCalculatorImpl(
				ColourSpace.sRGB);

		BufferedImage img = null;

		try {

			// img = ImageIO.read(new File(
			// "C:\\Users\\jlamont\\Downloads\\fleetwoodmac.jpg"));

			// img = ImageIO.read(new File(
			// "C:\\Users\\jlamont\\Downloads\\foos.jpg"));

			// img = ImageIO.read(new File(
			// "C:\\Users\\jlamont\\Downloads\\franz.jpg"));

			// img = ImageIO.read(new File(
			// "C:\\Users\\jlamont\\Downloads\\MetricFantasies.jpg"));

			img = ImageIO.read(new File(
					"/Users/jaylamont/Downloads/metric-fantasies.jpg"));

			// img = ImageIO.read(new File(
			// "/Users/jaylamont/Downloads/fleetwoodmac.jpg"));

			// img = ImageIO.read(new File(
			// "/Users/jaylamont/Downloads/franz.jpg"));

			final byte[] pixels = ((DataBufferByte) img.getRaster()
					.getDataBuffer()).getData();

			final int pixelCount = img.getWidth() * img.getHeight();

			int[] intPixels = new int[pixelCount];

			System.out.println("Image has " + pixelCount + " pixels");

			if (img.getAlphaRaster() == null) {
				for (int i = 0; i < intPixels.length; ++i) {
					int rgb = 0;

					rgb += ((int) pixels[i * 3] & 0xFF);
					rgb += ((int) pixels[i * 3 + 1] & 0xFF) << 8;
					rgb += ((int) pixels[i * 3 + 2] & 0xFF) << 16;

					intPixels[i] = rgb;
				}

				// int[] result = PaletteCalculator.calculatePalette(intPixels,
				// 3);
				int[] result = PaletteCalculator.calculatePalette(intPixels, 3);

				for (int r : result)
					System.out.println(Arrays.toString(ColourUtils
							.getRGBFromColour(r))
							+ " or "
							+ Arrays.toString(ColourUtils.getHSVFromColour(r)));

				/*
				 * for (int pixel : result) {
				 * System.out.println(Arrays.toString(ColourUtils
				 * .getRGBFromColour(pixel))); }
				 */

				new PalettePageWriter(new File(
						"/Users/jaylamont/Documents/paletteDoc.html"))
						.writeFile(result);

				// new PalettePageWriter(new File(
				// "C:\\Users\\jlamont\\Documents\\paletteDoc.html"))
				// .writeFile(result);
			}
		} catch (IOException e) {
			System.err.println("Error loading image!");
		}
	}
}
