package ca.jlamont.palettecalculator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PalettePageWriter {

	File file;

	public PalettePageWriter(File file) {
		this.file = file;
	}

	public void writeFile(int[] palette) {
		int[][] rgbPalette = new int[palette.length][3];

		for (int i = 0; i < palette.length; ++i) {
			int[] rgb = ColourUtils.getRGBFromColour(palette[i]);
			rgbPalette[i] = rgb;
		}

		StringBuilder builder = new StringBuilder();

		builder.append("<html><head><title>palette</title>"
				+ "<style>.container{padding: 2em; background-color: rgb(");

		builder.append(rgbPalette[0][0]);
		builder.append(',');
		builder.append(rgbPalette[0][1]);
		builder.append(',');
		builder.append(rgbPalette[0][2]);

		builder.append(");} .text{font-weight: bold; font-family:sans-serif;}</style></head>"
				+ "<body><div class='container'>");

		for (int i = 1; i < rgbPalette.length; ++i) {
			builder.append("<p class='text' style='color: rgb("
					+ rgbPalette[i][0] + "," + rgbPalette[i][1] + ","
					+ rgbPalette[i][2] + "'>Shade ");
			builder.append(i);
			builder.append("</p>");
		}

		builder.append("</div></body></html>");

		String document = builder.toString();

		try {
			BufferedWriter bfw = new BufferedWriter(new FileWriter(file));

			bfw.write(document);

			bfw.close();
		} catch (IOException e) {
			System.err.println("Error writing file.");
			e.printStackTrace();
		}
	}
	
	protected String getCSSRGBString(int r, int g, int b){
		StringBuilder sb = new StringBuilder("rgb(");
		
		sb.append(r);
		sb.append(',');
		sb.append(g);
		sb.append(',');
		sb.append(b);
		sb.append(')');
		
		return sb.toString();
	}
}
