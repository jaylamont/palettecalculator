package ca.jlamont.palettecalculator;

public class YUVKMeansCalculator implements KMeansCalculator {

	public int[] findKMeans(int[] values, int k) {
		float[][] yuvValues = new float[values.length][3];

		for (int i = 0; i < values.length; ++i) {
			yuvValues[i] = ColourUtils.getYUVFromColour(values[i]);
		}

		return null;
	}

}
