package ca.jlamont.palettecalculator;

public interface ImagePaletteCalculator {

	public static enum ColourSpace {
		sRGB
	}

	public int[] calculateImagePalette(int[] pixels);


	public int[] calculateImagePalette(int[] pixels, int requestedPaletteSize);


}
