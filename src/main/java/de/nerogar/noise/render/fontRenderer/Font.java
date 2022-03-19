package de.nerogar.noise.render.fontRenderer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import de.nerogar.noise.render.*;
import de.nerogar.noise.render.Texture.InterpolationType;

/**
 * a font class used for creating {@link FontRenderableString FontRenderableString}
 */
public class Font {
	private static int CHAR_COUNT = 16;

	private String fontName;

	private Texture2D texture;
	private int textureSize;
	private int pointSize;

	private int padding;
	private int charCellSize;
	private int minCharRight;

	private int[] rightBorder;

	/**
	 * A new Font sheet texture is rendered. All variables are set as in
	 * {@link java.awt.Font#Font(String, int, int) java.awt.Font.Font(String, int, int)}.
	 * The style is set to {@link java.awt.Font#PLAIN PLAIN}.
	 * 
	 * @param fontName the name of the font
	 * @param pointSize the point size of the font
	 */
	public Font(String fontName, int pointSize) {
		this(fontName, java.awt.Font.PLAIN, pointSize);
	}

	/**
	 * A new Font sheet texture is rendered. All variables are set as in
	 * {@link java.awt.Font#Font(String, int, int) java.awt.Font.Font(String, int, int)}.
	 * 
	 * @param fontName the name of the font
	 * @param style the style of the font
	 * @param pointSize the point size of the font
	 */
	public Font(String fontName, int style, int pointSize) {
		this.fontName = fontName;
		this.pointSize = pointSize;

		padding = Math.max(3, (pointSize + 1) / 2);
		charCellSize = pointSize + 2 * padding;
		minCharRight = padding + pointSize / 8;
		rightBorder = new int[CHAR_COUNT * CHAR_COUNT];

		createTexture(fontName, style, pointSize);
	}

	private void createTexture(String fontName, int style, int size) {
		textureSize = CHAR_COUNT * (size + padding * 2);

		BufferedImage fontRendered = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = fontRendered.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		java.awt.Font font = new java.awt.Font(fontName, style, size);
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

		right += Math.max(pointSize / 6, 1);

		return right - x;

	}

	/**
	 * @return the font sheet as a {@link Texture2D Texture2D}
	 */
	public Texture2D getTexture() {
		return texture;
	}

	/**
	 * @param c the character
	 * @return the width of the character in pixels
	 */
	public int getCharPixelWidth(char c) {
		return rightBorder[c] - padding;
	}

	/**
	 * @param c the character
	 * @return the complete width reserved for this character on the texture
	 */
	protected float getCharWidth(char c) {
		return 1.0f / CHAR_COUNT;
	}

	/**
	 * @param c the character
	 * @return the complete height reserved for this character on the texture
	 */
	protected float getCharHeight(char c) {
		return 1.0f / CHAR_COUNT;
	}

	/**
	 * @param c the character
	 * @return the left border for this character on the texture
	 */
	protected float getCharLeft(char c) {
		return (float) (c % CHAR_COUNT) / CHAR_COUNT;
	}

	/**
	 * @param c the character
	 * @return the bottom border for this character on the texture
	 */
	protected float getCharBottom(char c) {
		return (float) (CHAR_COUNT - (c / CHAR_COUNT) - 1) / CHAR_COUNT;
	}

	/**
	 * @return the complete size of the character in pixels
	 */
	protected int getSize() {
		return charCellSize;
	}

	/**
	 * @return the font name of this font
	 */
	public String getFontName() {
		return fontName;
	}

	/**
	 * @return the point size of this font
	 */
	public int getPointSize() {
		return pointSize;
	}

	/**
	 * @return the height of the baseline of each character in pixels
	 */
	protected int getBaseline() {
		return padding;
	}

	/**
	 * @return the distance between the baseline of two rendered lines in pixels
	 */
	public int getLineSpace() {
		return pointSize + pointSize / 3;
	}

	/**
	 * @return the padding each character is given on the font sheet in pixels
	 */
	protected int getTexturePadding() {
		return padding;
	}

	public void cleanup(){
		texture.cleanup();
	}

}
