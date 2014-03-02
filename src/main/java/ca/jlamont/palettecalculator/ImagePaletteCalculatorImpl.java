package ca.jlamont.palettecalculator;

public class ImagePaletteCalculatorImpl implements ImagePaletteCalculator {

	ShadeBuckets buckets;

	public ImagePaletteCalculatorImpl(ColourSpace bucketType) {
		this.buckets = new HSVBuckets(360, 100, 100);
	}

	public int[] calculateImagePalette(int[] pixels) {
		return this.calculateImagePalette(pixels, 3);
	}

	public int[] calculateImagePalette(int[] pixels, int requestedPaletteSize) {
		for (int p : pixels) {
			this.buckets.addColor(p);
		}

		int[] shades = this.buckets.getMostPrevalentShades();

		int[] result = new int[requestedPaletteSize];

		result[0] = shades[0];

		int count = 1;
		float backgroundLuminance = getLuminance(result[0]);

		for (int i = 0; i < shades.length && count < result.length; ++i) {
			float currentLuminance = getLuminance(shades[i]);

			float contrastRatio = (float) (currentLuminance > backgroundLuminance ? (currentLuminance + .05)
					/ (backgroundLuminance + .05)
					: (backgroundLuminance + .05) / (currentLuminance + .05));

			if (contrastRatio >= 4.5) {
				//System.out.println("Found contrasting colour! " + shades[i]);
				result[count++] = shades[i];
			}
		}

		return result;
	}

	protected float getLuminance(int colour) {
		int[] rgb = ColourUtils.getRGBFromColour(colour);

		float[] floatRGB = new float[3];

		floatRGB[0] = (float) rgb[0] / 255f;
		floatRGB[1] = (float) rgb[1] / 255f;
		floatRGB[2] = (float) rgb[2] / 255f;

		float calcR, calcG, calcB;

		calcR = (float) (floatRGB[0] <= 0.03928 ? floatRGB[0] / 12.92 : Math
				.pow((floatRGB[0] + 0.055) / 1.055, 2.4));

		calcG = (float) (floatRGB[1] <= 0.03928 ? floatRGB[1] / 12.92 : Math
				.pow((floatRGB[1] + 0.055) / 1.055, 2.4));

		calcB = (float) (floatRGB[2] <= 0.03928 ? floatRGB[2] / 12.92 : Math
				.pow((floatRGB[2] + 0.055) / 1.055, 2.4));

		return (float) (.2126 * calcR + .7152 * calcG + .0722 * calcB);
	}
}
