package ca.jlamont.palettecalculator;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ca.jlamont.palettecalculator.ImagePaletteCalculator.ColourSpace;


public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String args[]) {
		ImagePaletteCalculator ipc = new ImagePaletteCalculatorImpl(
				ColourSpace.sRGB);

		BufferedImage img = null;

		try {
//			img = ImageIO.read(new File(
//					"C:\\Users\\jlamont\\Downloads\\franz.jpg"));

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

				int[] palette = ipc.calculateImagePalette(intPixels, 500);

				System.out.println(palette.length + " shades returned");

				for (int p : palette) {
					int[] rgb = ColourUtils.getRGBFromColour(p);

					System.out.println(rgb[0] + "," + rgb[1] + "," + rgb[2]);
				}

				new PalettePageWriter(new File(
						"/Users/jaylamont/Documents/paletteDoc.html"))
						.writeFile(palette);

				// new PalettePageWriter(new File(
				// "C:\\Users\\jlamont\\Documents\\paletteDoc.html"))
				// .writeFile(palette);
			} else {
				System.out.println("Image has alpha channel");
			}

		} catch (IOException e) {
			System.err.println("Error reading image");
			e.printStackTrace();
		}
	}
}
