package ca.jlamont.palettecalculator;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tester {

	public static void test(File[] files) {
		int[][] colours = new int[files.length][];

		for (int i = 0; i < files.length; ++i) {
			int[] pixels = getPixelsFromFile(files[i]);

			int[] result = PaletteCalculator.calculatePalette(pixels, 3);

			colours[i] = result;
		}

		PageCreator.createPage(new File(
				"/Users/jaylamont/Documents/paletteDoc.html"), files, colours);
	}

	private static int[] getPixelsFromFile(File file) {
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

			img = ImageIO.read(file);

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

				return intPixels;

			}
		} catch (IOException e) {
			System.err.println("Error reading file!");
		}
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File[] files = {
				new File("/Users/jaylamont/Documents/metric-fantasies.jpg"),
				new File("/Users/jaylamont/Documents/franz.jpg"),
				new File("/Users/jaylamont/Documents/fleetwoodmac.jpg"),
				new File("/Users/jaylamont/Documents/letitbleed.jpg"),
				new File("/Users/jaylamont/Documents/blackkeysbrothers.jpg"),
				new File("/Users/jaylamont/Documents/hightwilight.jpg") };

		long start = System.currentTimeMillis();

		Tester.test(files);

		long end = System.currentTimeMillis();

		System.out.println("Run time:" + (end - start) + "ms");
	}

}
