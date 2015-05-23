package de.nerogar.noise.render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import de.nerogar.noise.render.Texture2D.DataType;
import de.nerogar.noise.render.Texture2D.InterpolationType;

public class Texture2DLoader {

	private static HashMap<String, Texture2D> textureMap = new HashMap<String, Texture2D>();

	public static Texture2D loadTexture(String filename) {
		return loadTexture(filename, filename);
	}

	public static Texture2D loadTexture(String filename, String textureName) {
		return loadTexture(filename, filename, InterpolationType.LINEAR);
	}

	public static Texture2D loadTexture(String filename, InterpolationType interpolationType) {
		return loadTexture(filename, filename, interpolationType);
	}

	public static Texture2D loadTexture(String filename, String textureName, InterpolationType interpolationType) {
		Texture2D retTexture = textureMap.get(filename);

		if (retTexture != null) return retTexture;

		try {
			BufferedImage image = ImageIO.read(new File(filename));
			retTexture = loadTexture(image, textureName, interpolationType);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Missing Texture: " + filename);
		}

		textureMap.put(filename, retTexture);
		return retTexture;
	}

	public static Texture2D loadTexture(BufferedImage image, String textureName, InterpolationType interpolationType) {
		IntBuffer buffer = BufferUtils.createIntBuffer(image.getWidth() * image.getHeight());
		int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
		buffer.put(pixels);
		buffer.rewind();
		return new Texture2D(textureName, image.getWidth(), image.getHeight(), buffer, interpolationType, DataType.BGRA_8_8_8_8I);
	}

	protected static void unloadTexture(String filename) {
		textureMap.remove(filename);
	}

}
