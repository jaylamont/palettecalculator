package ca.jlamont.palettecalculator;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class KMeansTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		BufferedImage img;

		try {
			// img = ImageIO.read(new File(
			// "C:\\Users\\jlamont\\Downloads\\franz.jpg"));

			// img = ImageIO.read(new File(
			// "C:\\Users\\jlamont\\Downloads\\MetricFantasies.jpg"));

			img = ImageIO.read(new File(
					"/Users/jaylamont/Downloads/metric-fantasies.jpg"));

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

				//System.out.println(Arrays.toString(intPixels));

				int[] palette = new KColoursCalculator().findKMeans(intPixels,
						3);

				for (int colour : palette) {
					System.out.println(Arrays.toString(ColourUtils
							.getRGBFromColour(colour)));
				}
			}

		} catch (IOException e) {

		}

	}

}
