package ca.jlamont.palettecalculator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

public class HSVBuckets implements ShadeBuckets {

	protected int hBuckets;
	protected int sBuckets;
	protected int vBuckets;

	protected final float hGranularity;
	protected final float sGranularity;
	protected final float vGranularity;

	protected final float maxH = 360;
	protected final float maxS = 1;
	protected final float maxV = 1;

	protected float minLuminance = Float.MAX_VALUE;
	protected float maxLuminance = Float.MIN_VALUE;

	protected List<Integer>[][][] buckets;

	public HSVBuckets(int hBuckets, int sBuckets, int vBuckets) {
		this.hBuckets = hBuckets;
		this.sBuckets = sBuckets;
		this.vBuckets = vBuckets;

		this.hGranularity = maxH / ((float) hBuckets);
		this.sGranularity = maxS / ((float) sBuckets);
		this.vGranularity = maxV / ((float) vBuckets);

		buckets = new List[hBuckets + 1][sBuckets + 1][vBuckets + 1];
	}

	public void addColour(int colour) {
		int[] rgb = ColourUtils.getRGBFromColour(colour);

		float[] hsv = new float[3];

		Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsv);

		int targetHBucket = (int) (hsv[0] / hGranularity);
		int targetSBucket = (int) (hsv[1] / sGranularity);
		int targetVBucket = (int) (hsv[2] / vGranularity);

		if (buckets[targetHBucket][targetSBucket][targetVBucket] == null)
			buckets[targetHBucket][targetSBucket][targetVBucket] = new ArrayList<Integer>();

		buckets[targetHBucket][targetSBucket][targetVBucket].add(colour);

		float luminance = ColourUtils.getLuminance(colour);

		if (luminance > maxLuminance)
			maxLuminance = luminance;

		if (luminance < minLuminance)
			minLuminance = luminance;
	}

	public void addColor(int color) {
		this.addColour(color);
	}

	public int[] getMostPrevalentShades() {

		List<List<Integer>> bucketsWithItems = new ArrayList<List<Integer>>();

		for (List<Integer>[][] a : buckets) {
			for (List<Integer>[] b : a) {
				for (List<Integer> c : b) {
					if (c != null)
						bucketsWithItems.add(c);
				}
			}
		}

		int[] result = new int[bucketsWithItems.size()];

		Collections.sort(bucketsWithItems, new Comparator<List<Integer>>() {

			public int compare(List<Integer> arg0, List<Integer> arg1) {
				return arg0.size() - arg1.size();
			}
		});

		for (int i = 0; i < bucketsWithItems.size(); ++i) {
			result[i] = calculateAverageShadeOfBucket(bucketsWithItems
					.get(bucketsWithItems.size() - 1 - i));
		}

		return result;
	}

	public int getMostPrevalentShade() {
		List<Integer> biggestBucket = null;

		int biggestBucketSize = 0;

		for (List<Integer>[][] a : buckets) {
			for (List<Integer>[] b : a) {
				for (List<Integer> c : b) {
					if (c != null && c.size() > biggestBucketSize) {
						biggestBucketSize = c.size();
						biggestBucket = c;
					}
				}
			}
		}

		return calculateRGBAverageShadeOfBucket(biggestBucket);
	}

	public int[] getContrastingShades(int colour) {
		List<Integer> contrastingShades = new ArrayList<Integer>();

		float providedColourLuminance = getRelativeLuminance(ColourUtils
				.getLuminance(colour));

		for (List<Integer>[][] a : buckets) {
			for (List<Integer>[] b : a) {
				for (List<Integer> c : b) {
					if (c != null) {
						for (int currentShade : c) {
							float currentShadeLuminance = getRelativeLuminance(ColourUtils
									.getLuminance(currentShade));

							float contrastRatio = ColourUtils.getContrastRatio(
									currentShadeLuminance,
									providedColourLuminance);

							if (contrastRatio >= 4.5) {
								contrastingShades.add(currentShade);
							}
						}
					}
					// if (c != null) {
					// int averageShade = calculateRGBAverageShadeOfBucket(c);
					// float averageShadeLuminance =
					// getRelativeLuminance(ColourUtils
					// .getLuminance(averageShade));
					//
					// float contrastRatio = ColourUtils.getContrastRatio(
					// averageShadeLuminance, providedColourLuminance);
					//
					// if (contrastRatio >= 4.5)
					// contrastingShades.addAll(c);
					// }
				}
			}
		}

		int[] result = new int[contrastingShades.size()];

		for (int i = 0; i < contrastingShades.size(); ++i) {
			result[i] = contrastingShades.get(i);
		}

		return result;
	}

	protected int calculateRGBAverageShadeOfBucket(List<Integer> bucket) {
		int rSum = 0, gSum = 0, bSum = 0;

		for (int i = 0; i < bucket.size(); ++i) {
			int[] rgb = ColourUtils.getRGBFromColour(bucket.get(i));

			rSum += rgb[0];
			gSum += rgb[1];
			bSum += rgb[2];
		}

		return ColourUtils.getColourFromRGB(rSum / bucket.size(),
				gSum / bucket.size(), bSum / bucket.size());
	}

	protected int calculateAverageShadeOfBucket(List<Integer> bucket) {
		float[] floatSum = new float[3];

		for (int i : bucket) {
			int[] rgb = ColourUtils.getRGBFromColour(i);
			float[] hsv = new float[3];

			Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsv);

			floatSum[0] += hsv[0];
			floatSum[1] += hsv[1];
			floatSum[2] += hsv[2];
		}

		floatSum[0] /= (float) bucket.size();
		floatSum[1] /= (float) bucket.size();
		floatSum[2] /= (float) bucket.size();

		return Color.HSBtoRGB(floatSum[0], floatSum[1], floatSum[2]);
	}

	protected float getRelativeLuminance(float luminance) {
		return (luminance - minLuminance) / (maxLuminance - minLuminance);
	}

}
