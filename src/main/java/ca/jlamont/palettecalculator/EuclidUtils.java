package ca.jlamont.palettecalculator;

import java.util.Arrays;

public class EuclidUtils {

	public static float[][] getDistributedPoints(float[][] ranges, int numPoints) {
		float[][] result = new float[numPoints][ranges.length];

		// float[] intervals = new float[ranges.length];

		for (int i = 0; i < ranges.length; ++i) {
			float interval = Math.abs(ranges[i][0] - ranges[i][1])
					/ (numPoints + 1);

			for (int j = 0; j < numPoints; ++j) {
				result[j][i] = ranges[i][0] + interval * (j + 1);
			}

			// intervals[i] = Math.abs(ranges[i][0] - ranges[i][0]);
		}

		return result;
	}

	public static float calculateEuclideanDistance(float[] a, float[] b) {
		float sum = 0;

		for (int i = 0; i < a.length; ++i) {
			sum += Math.pow(b[i] - a[i], 2);
		}

		return (float) Math.sqrt(sum);
	}

	public static void main(String args[]) {
		float[][] r = { { -90, 90 }, { 0, 128 }, { 0, 255 } };

		float[][] result = EuclidUtils.getDistributedPoints(r, 2);

		for (float[] f : result)
			System.out.println(Arrays.toString(f));
	}

}
