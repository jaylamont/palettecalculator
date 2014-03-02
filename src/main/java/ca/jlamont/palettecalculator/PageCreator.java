package ca.jlamont.palettecalculator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PageCreator {

	public static void createPage(File toWriteTo, File[] files, int[][] colours) {
		StringBuffer buffer = new StringBuffer();

		buffer.append("<html><head><style>.container{padding:2em;} \r\n .container img{width:300; height:300; float:right } \r\n .clear{clear:both; width:100%} \r\n .text{font-weight: bold; font-family:sans-serif;}</style></head><body>");

		for (int i = 0; i < files.length; ++i) {
			buffer.append(getContainer(files[i], colours[i]));
		}

		buffer.append("</body></html>");

		BufferedWriter bfw = null;
		try {
			bfw = new BufferedWriter(new FileWriter(toWriteTo));

			bfw.write(buffer.toString());

		} catch (IOException e) {
			System.err.println("Error writing file");
		} finally {
			try {
				bfw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String getContainer(File file, int[] colours) {
		StringBuffer buffer = new StringBuffer(
				"<div class='container' style='background-color: ");
		buffer.append(getCSSRGBStringFromColour(colours[0]));
		buffer.append("'>");

		buffer.append(getImageElement(file));

		for (int i = 1; i < colours.length; ++i) {
			buffer.append(getTextElement("Shade " + i, colours[i]));
		}
		
		buffer.append("<div class='clear'></div>");

		buffer.append("</div>");
		return buffer.toString();
	}

	private static String getImageElement(File file) {
		StringBuffer buffer = new StringBuffer("<img src='");

		buffer.append(file.getName());
		buffer.append("' />");

		return buffer.toString();
	}

	private static String getTextElement(String text, int colour) {
		String colourString = getCSSRGBStringFromColour(colour);

		StringBuffer buffer = new StringBuffer("<p class='text' style='color: ");
		buffer.append(colourString);
		buffer.append("'>");
		buffer.append(text);
		buffer.append("</p>");

		return buffer.toString();
	}

	private static String getCSSRGBStringFromColour(int colour) {
		int[] rgb = ColourUtils.getRGBFromColour(colour);

		return getCSSRGBStringFromColour(rgb[0], rgb[1], rgb[2]);
	}

	private static String getCSSRGBStringFromColour(int r, int g, int b) {
		StringBuffer buffer = new StringBuffer("rgb(");
		buffer.append(r);
		buffer.append(',');
		buffer.append(g);
		buffer.append(',');
		buffer.append(b);
		buffer.append(')');

		return buffer.toString();
	}

}
