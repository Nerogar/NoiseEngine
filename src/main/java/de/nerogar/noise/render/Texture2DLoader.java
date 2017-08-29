package de.nerogar.noise.render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import de.nerogar.noise.Noise;
import de.nerogar.noise.util.FileUtil;
import org.lwjgl.BufferUtils;

import de.nerogar.noise.render.Texture2D.DataType;
import de.nerogar.noise.render.Texture2D.InterpolationType;
import de.nerogar.noise.util.Logger;

public class Texture2DLoader {

	private static HashMap<String, Texture2D> textureMap = new HashMap<>();

	public static Texture2D loadTexture(String filename) {
		return loadTexture(filename, filename);
	}

	public static Texture2D loadTexture(String filename, String textureName) {
		return loadTexture(filename, textureName, InterpolationType.LINEAR);
	}

	public static Texture2D loadTexture(String filename, InterpolationType interpolationType) {
		return loadTexture(filename, filename, interpolationType);
	}

	public static Texture2D loadTexture(String filename, String textureName, InterpolationType interpolationType) {
		filename = FileUtil.decodeFilename(null, FileUtil.TEXTURE_SUBFOLDER, filename);

		Texture2D retTexture = textureMap.get(filename);

		if (retTexture != null) return retTexture;

		try {
			BufferedImage image = ImageIO.read(new File(filename));
			retTexture = loadTexture(image, textureName, interpolationType);
		} catch (IOException e) {
			e.printStackTrace();
			Noise.getLogger().log(Logger.ERROR, "Missing Texture: " + filename);
		}

		textureMap.put(filename, retTexture);

		Noise.getLogger().log(Logger.INFO, "loaded texture: " + filename);

		return retTexture;
	}

	public static Texture2D loadTexture(BufferedImage image, String textureName, InterpolationType interpolationType) {
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * Integer.BYTES);
		int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

		//invert image
		for (int line = 0; line < image.getHeight() / 2; line++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int tempColor = pixels[line * image.getWidth() + x];
				pixels[line * image.getWidth() + x] = pixels[(image.getHeight() - line - 1) * image.getWidth() + x];
				pixels[(image.getHeight() - line - 1) * image.getWidth() + x] = tempColor;
			}
		}

		buffer.asIntBuffer().put(pixels);
		buffer.rewind();
		return new Texture2D(textureName, image.getWidth(), image.getHeight(), buffer, interpolationType, DataType.BGRA_8_8_8_8I);
	}

	protected static void unloadTexture(String filename) {
		textureMap.remove(filename);
	}

}
