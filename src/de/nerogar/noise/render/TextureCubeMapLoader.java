package de.nerogar.noise.render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

public class TextureCubeMapLoader {

	private static HashMap<String, TextureCubeMap> textureMap = new HashMap<String, TextureCubeMap>();

	/**
	 * Call this method with 6 filenames of textures in this order:
	 * <ol>
	 * <li>right</li>
	 * <li>left</li>
	 * <li>bottom</li>
	 * <li>top</li>
	 * <li>back</li>
	 * <li>front</li>
	 * </ol>
	 * 
	 * @param filename the filenames
	 * @return The CubeMap
	 */
	public static TextureCubeMap loadTexture(String... filename) {
		TextureCubeMap retTexture = textureMap.get(filename[0]);

		if (retTexture != null) return retTexture;

		BufferedImage[] image = new BufferedImage[6];
		IntBuffer[] buffer = new IntBuffer[6];

		for (int i = 0; i < 6; i++) {
			try {

				image[i] = ImageIO.read(new File(filename[i]));
				buffer[i] = BufferUtils.createIntBuffer(image[i].getWidth() * image[i].getHeight());

				int[] pixels = image[i].getRGB(0, 0, image[i].getWidth(), image[i].getHeight(), null, 0, image[i].getWidth());

				//invert images
				for (int line = 0; line < image[i].getHeight() / 2; line++) {
					for (int x = 0; x < image[i].getWidth(); x++) {
						int tempColor = pixels[line * image[i].getWidth() + x];
						pixels[line * image[i].getWidth() + x] = pixels[(image[i].getHeight() - line - 1) * image[i].getWidth() + x];
						pixels[(image[i].getHeight() - line - 1) * image[i].getWidth() + x] = tempColor;
					}
				}

				buffer[i].put(pixels);
				buffer[i].rewind();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Missing Texture: " + Arrays.toString(filename));
			}
		}

		retTexture = new TextureCubeMap(filename[0], image[0].getWidth(), image[0].getHeight(), buffer);
		retTexture.setFilenames(filename);

		textureMap.put(filename[0], retTexture);

		return retTexture;

	}

	protected static void unloadTexture(String filename) {
		textureMap.remove(filename);
	}

}
