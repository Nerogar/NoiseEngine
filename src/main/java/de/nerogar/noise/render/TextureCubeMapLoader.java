package de.nerogar.noise.render;

import de.nerogar.noise.file.FileUtil;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class TextureCubeMapLoader {

	/**
	 * Call this method with 6 filenames of textures in this order:
	 * <ol>
	 * <li>x positive</li>
	 * <li>x negative</li>
	 * <li>y positive</li>
	 * <li>y negative</li>
	 * <li>z positive</li>
	 * <li>z negative</li>
	 * </ol>
	 *
	 * @param filename the filenames
	 * @return The CubeMap
	 */
	public static TextureCubeMap loadTexture(String... filename) {

		BufferedImage[] image = new BufferedImage[6];
		ByteBuffer[] buffer = new ByteBuffer[6];

		for (int i = 0; i < 6; i++) {
			try {
				InputStream inputStream = FileUtil.get(filename[i], FileUtil.TEXTURE_SUBFOLDER).asStream();

				image[i] = ImageIO.read(inputStream);
				buffer[i] = BufferUtils.createByteBuffer(image[i].getWidth() * image[i].getHeight() * Integer.BYTES);

				int[] pixels = image[i].getRGB(0, 0, image[i].getWidth(), image[i].getHeight(), null, 0, image[i].getWidth());

				//invert images
				/*for (int line = 0; line < image[i].getHeight() / 2; line++) {
					for (int x = 0; x < image[i].getWidth(); x++) {
						int index0 = line * image[i].getWidth() + x;
						int index1 = (image[i].getHeight() - line - 1) * image[i].getWidth() + x;

						int tempColor = pixels[index0];
						pixels[index0] = pixels[index1];
						pixels[index1] = tempColor;
					}
				}*/

				for (int column = 0; column < image[i].getWidth() / 2; column++) {
					for (int y = 0; y < image[i].getHeight(); y++) {
						int index0 = y * image[i].getWidth() + column;
						int index1 = y * image[i].getWidth() + (image[i].getWidth() - column - 1);

						int tempColor = pixels[index0];
						pixels[index0] = pixels[index1];
						pixels[index1] = tempColor;
					}
				}

				buffer[i].asIntBuffer().put(pixels);
				buffer[i].rewind();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Missing Texture: " + Arrays.toString(filename));
			}
		}

		return new TextureCubeMap(filename[0], image[0].getWidth(), image[0].getHeight(), buffer);
	}

}
