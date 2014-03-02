package ca.jlamont.palettecalculator;

import java.util.Arrays;

public class ColourUtils {

	/* Constants for YUV calculations */
	private static final int Y = 0;
	private static final int U = 1;
	private static final int V = 2;
	private static final float YUV_WR = .299f;
	private static final float YUV_WB = .114f;
	private static final float YUV_WG = 1 - YUV_WR - YUV_WB;
	private static final float YUV_UMAX = .436f;
	private static final float YUV_VMAX = .615f;

	private ColourUtils() {

	}

	public static int[] getRGBFromColour(int colour) {
		int[] result = new int[3];

		result[0] = (colour >>> 16) & 0xFF;
		result[1] = (colour >>> 8) & 0xFF;
		result[2] = colour & 0xFF;

		return result;
	}

	public static int getColourFromRGB(int r, int g, int b) {
		return (r << 16) | (g << 8) | b;
	}

	public static int getColourFromRGB(int[] rgb) {
		return getColourFromRGB(rgb[0], rgb[1], rgb[2]);
	}

	public static float[] getYUVFromColour(int colour) {
		int[] rgb = getRGBFromColour(colour);

		float[] weightedRGB = new float[3];

		for (int i = 0; i < 3; ++i) {
			weightedRGB[i] = rgb[i] / 255f;
		}

		float[] result = new float[3];

		// Y calculation
		result[0] = weightedRGB[0] * YUV_WR + weightedRGB[1] * YUV_WG
				+ weightedRGB[2] * YUV_WB;

		// U calculation
		result[1] = YUV_UMAX * ((weightedRGB[2] - result[0]) / (1f - YUV_WB));

		// V calculation
		result[2] = YUV_VMAX * ((weightedRGB[0] - result[0]) / (1f - YUV_WR));

		return result;
	}

	public static float[] getEuclideanYUVFromYUV(float[] yuv) {
		// Assumes range of Y=[0,1], U=[-YUV_UMAX, YUV_UMAX], V=[-YUV_VMAX,
		// YUV_VMAX]

		float[] euclidYUV = new float[3];

		euclidYUV[Y] = yuv[Y] * 255f;

		euclidYUV[U] = (yuv[U] + YUV_UMAX) * (255f / (2f * YUV_UMAX));

		euclidYUV[V] = (yuv[V] + YUV_VMAX) * (255f / (2f * YUV_UMAX));

		return euclidYUV;
	}

	public static float[] getEuclideanYUVFromColour(int colour) {
		return getEuclideanYUVFromYUV(getYUVFromColour(colour));
	}

	public static float[] getYUVFromEuclideanYUV(float[] eYUV) {
		float[] yuv = new float[3];

		yuv[Y] = eYUV[Y] / 255f;

		yuv[1] = eYUV[U] * ((2f * YUV_UMAX) / 255f) - YUV_UMAX;

		yuv[2] = eYUV[V] * ((2f * YUV_VMAX) / 255f) - YUV_VMAX;

		return yuv;
	}

	public static int getColourFromYUV(float[] yuv) {
		return getColourFromRGB(getRGBFromYUV(yuv));
	}

	public static int[] getRGBFromYUV(float[] yuv) {
		float[] fResult = new float[3];

		fResult[0] = (yuv[Y] + yuv[V] * ((1f - YUV_WR) / YUV_VMAX));

		fResult[1] = (yuv[Y] - yuv[U]
				* ((YUV_WB * (1 - YUV_WB)) / (YUV_UMAX * YUV_WG)) - yuv[V]
				* ((YUV_WR * (1 - YUV_WR)) / (YUV_VMAX * YUV_WG)));

		fResult[2] = (yuv[Y] + yuv[U] * ((1f - YUV_WB) / YUV_UMAX));

		int[] result = new int[3];

		result[0] = (int) Math.round((fResult[0] * 255));
		result[1] = (int) Math.round((fResult[1] * 255));
		result[2] = (int) Math.round((fResult[2] * 255));

		return result;
	}

	public static float getLuminance(int colour) {
		return getLuminance(ColourUtils.getRGBFromColour(colour));
	}

	public static float getLuminance(int[] rgb) {
		return getLuminance(rgb[0], rgb[1], rgb[2]);
	}

	public static float getLuminance(int r, int g, int b) {
		float[] floatRGB = new float[3];

		floatRGB[0] = (float) r / 255f;
		floatRGB[1] = (float) g / 255f;
		floatRGB[2] = (float) b / 255f;

		float calcR, calcG, calcB;

		calcR = (float) (floatRGB[0] <= 0.03928 ? floatRGB[0] / 12.92 : Math
				.pow((floatRGB[0] + 0.055) / 1.055, 2.4));

		calcG = (float) (floatRGB[1] <= 0.03928 ? floatRGB[1] / 12.92 : Math
				.pow((floatRGB[1] + 0.055) / 1.055, 2.4));

		calcB = (float) (floatRGB[2] <= 0.03928 ? floatRGB[2] / 12.92 : Math
				.pow((floatRGB[2] + 0.055) / 1.055, 2.4));

		return (float) (.2126 * calcR + .7152 * calcG + .0722 * calcB);
	}

	public static float getContrastRatio(float l1, float l2) {
		return (l1 > l2 ? (l1 + .05f) / (l2 + .05f) : (l2 + .05f) / (l1 + .05f));
	}

	public static float[] getHSVFromColour(int colour) {
		return getHSVFromRGB(getRGBFromColour(colour));
		// int[] rgb = getRGBFromColour(colour);
		//
		// System.out.println("resulting rgb" + Arrays.toString(rgb));
		//
		// float[] hsv = getHSVFromRGB(rgb);
		//
		// System.out.println("resulting hsv" + Arrays.toString(hsv));
		//
		// return hsv;
	}

	public static float[] getHSVFromRGB(int[] rgb) {
		return getHSVFromRGB(rgb[0], rgb[1], rgb[2]);
	}

	public static float[] getHSVFromRGB(int r, int g, int b) {
		float hue, saturation, value;
		float[] hsvvals = new float[3];

		int cmax = (r > g) ? r : g;
		if (b > cmax)
			cmax = b;
		int cmin = (r < g) ? r : g;
		if (b < cmin)
			cmin = b;

		value = ((float) cmax) / 255.0f;
		if (cmax != 0)
			saturation = ((float) (cmax - cmin)) / ((float) cmax);
		else
			saturation = 0;
		if (saturation == 0)
			hue = 0;
		else {
			float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
			float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
			float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
			if (r == cmax)
				hue = bluec - greenc;
			else if (g == cmax)
				hue = 2.0f + redc - bluec;
			else
				hue = 4.0f + greenc - redc;
			hue = hue / 6.0f;
			if (hue < 0)
				hue = hue + 1.0f;
		}
		hsvvals[0] = hue * 360;
		hsvvals[1] = saturation;
		hsvvals[2] = value;
		return hsvvals;
	}

	public static int getColourFromHSV(float[] hsv) {
		return getColourFromRGB(getRGBFromHSV(hsv));
	}

	public static int[] getRGBFromHSV(float[] hsv) {
		return getRGBFromHSV(hsv[0], hsv[1], hsv[2]);
	}

	public static int[] getRGBFromHSV(float h, float s, float v) {
		float[] rgb = new float[3];

		int i;
		float f, p, q, t;

		if (s == 0) {
			rgb[0] = rgb[1] = rgb[2] = v;
		} else {
			h = h / 60;
			i = (int) Math.floor(h);

			f = h - i;

			p = v * (1 - s);
			q = v * (1 - s * f);
			t = v * (1 - s * (1 - f));

			switch (i) {
			case 0:
				rgb[0] = v;
				rgb[1] = t;
				rgb[2] = p;
				break;
			case 1:
				rgb[0] = q;
				rgb[1] = v;
				rgb[2] = p;
				break;
			case 2:
				rgb[0] = p;
				rgb[1] = v;
				rgb[2] = t;
				break;
			case 3:
				rgb[0] = p;
				rgb[1] = q;
				rgb[2] = v;
				break;
			case 4:
				rgb[0] = t;
				rgb[1] = p;
				rgb[2] = v;
				break;
			default:
				rgb[0] = v;
				rgb[1] = p;
				rgb[2] = q;
				break;
			}
		}

		rgb[0] *= 255f;
		rgb[1] *= 255f;
		rgb[2] *= 255f;

		int[] result = new int[3];

		result[0] = Math.round(rgb[0]);
		result[1] = Math.round(rgb[1]);
		result[2] = Math.round(rgb[2]);

		return result;
	}

	protected static final float EUCLIDEAN_HSV_FACTOR = 360f;

	public static float EUCLIDEAN_SATURATION_FACTOR = 360f;
	public static float EUCLIDEAN_VALUE_FACTOR = 10f;

	public static void convertHSVToEuclideanHSV(float[] hsv) {
		hsv[1] *= EUCLIDEAN_SATURATION_FACTOR;
		hsv[2] *= EUCLIDEAN_VALUE_FACTOR;
	}

	public static void convertEuclideanHSVToHSV(float[] hsv) {
		hsv[1] /= EUCLIDEAN_SATURATION_FACTOR;
		hsv[2] /= EUCLIDEAN_VALUE_FACTOR;
	}

	protected static float min(float... f) {
		float min = f[0];

		for (int i = 1; i < f.length; ++i) {
			if (f[i] < min)
				min = f[i];
		}

		return min;
	}

	protected static float max(float... f) {
		float max = f[0];

		for (int i = 1; i < f.length; ++i) {
			if (f[i] > max)
				max = f[i];
		}

		return max;
	}

	public static float calculateEuclideanDistanceForHSV(float[] x, float[] y) {
		float hDelta = Math.abs(x[0] - y[0]);

		// System.out.println(Arrays.toString(x));

		if (hDelta > 180)
			hDelta -= 360;

		float sum = (float) Math.pow(hDelta, 2);

		for (int i = 1; i < 3; ++i) {
			sum += Math.pow(x[i] - y[i], 2);
		}

		// System.out.println("Sum is " + sum);

		return (float) Math.sqrt(sum);
	}

	public static void main(String args[]) {
		float[] hsv = ColourUtils.getHSVFromRGB(62, 142, 179);

		int[] rgb = ColourUtils.getRGBFromHSV(hsv);

		System.out.println(Arrays.toString(hsv));
		System.out.println(Arrays.toString(rgb));

	}
}
