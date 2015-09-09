package de.nerogar.noise.render.fontRenderer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import de.nerogar.noise.render.*;
import de.nerogar.noise.render.Texture2D.InterpolationType;

public class Font {
	private static int CHAR_COUNT = 16;

	private Texture2D texture;
	private int textureSize;
	private int size;

	private int padding;
	private int charCellSize;
	private int minCharRight;

	private int[] rightBorder;

	public Font(String fontName, int size) {
		this.size = size;

		padding = Math.max(3, (size + 1) / 2);
		charCellSize = size + 2 * padding;
		minCharRight = padding + size / 8;
		rightBorder = new int[CHAR_COUNT * CHAR_COUNT];

		createTexture(fontName, size);
	}

	private void createTexture(String fontName, int size) {
		textureSize = CHAR_COUNT * (size + padding * 2);

		BufferedImage fontRendered = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = fontRendered.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		//g.setColor(java.awt.Color.BLACK);
		//g.fillRect(0, 0, fontRendered.getWidth(), fontRendered.getHeight());

		java.awt.Font font = new java.awt.Font(fontName, java.awt.Font.PLAIN, size);
		g.setFont(font);

		g.setColor(java.awt.Color.WHITE);

		for (int x = 0; x < CHAR_COUNT; x++) {
			for (int y = 0; y < CHAR_COUNT; y++) {
				char c = (char) (x + CHAR_COUNT * y);

				g.drawString(String.valueOf(c), x * charCellSize + padding, y * charCellSize + charCellSize - padding);

				rightBorder[c] = getRightBorder(fontRendered, x * charCellSize, y * charCellSize, charCellSize, charCellSize);
			}
		}

		texture = Texture2DLoader.loadTexture(fontRendered, "font", InterpolationType.LINEAR);
	}

	private int getRightBorder(BufferedImage fontRendered, int x, int y, int width, int height) {

		int right = x + minCharRight;

		for (int i = right; i < x + width; i++) {
			for (int j = y; j < y + height; j++) {

				if (fontRendered.getRGB(i, j) != 0) right = Math.max(right, i);

			}
		}

		right += Math.max(size / 6, 1);

		return right - x;

	}

	public Texture2D getTexture() {
		return texture;
	}

	public int getCharPixelWidth(char c) {
		return rightBorder[c] - padding;
	}

	public float getCharWidth(char c) {
		return 1.0f / CHAR_COUNT;
	}

	public float getCharHeight(char c) {
		return 1.0f / CHAR_COUNT;
	}

	public float getCharLeft(char c) {
		return (float) (c % CHAR_COUNT) / CHAR_COUNT;
	}

	public float getCharBottom(char c) {
		return (float) (CHAR_COUNT - (c / CHAR_COUNT) - 1) / CHAR_COUNT;
	}

	public int getSize() {
		return charCellSize;
	}

	public int getPointSize() {
		return size;
	}

	public int getBaseline() {
		return padding;
	}

	public int getLineSpace() {
		return size + size / 3;
	}

	public int getTexturePadding() {
		return padding;
	}
}
