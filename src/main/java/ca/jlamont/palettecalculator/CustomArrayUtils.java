package ca.jlamont.palettecalculator;

import org.apache.commons.lang3.ArrayUtils;

public class CustomArrayUtils extends ArrayUtils {

	public static float[] toFloat(int[] input) {
		float[] result = new float[input.length];

		for (int i = 0; i < result.length; ++i)
			result[i] = input[i];

		return result;
	}

	public static int[] toInt(float[] input) {
		int[] result = new int[input.length];

		for (int i = 0; i < result.length; ++i) {
			result[i] = Math.round(input[i]);
		}
		
		return result;
	}

}
