package de.nerogar.noise.render;

import de.nerogar.noise.Noise;
import de.nerogar.noise.file.FileUtil;
import de.nerogar.noise.render.Texture.DataType;
import de.nerogar.noise.render.Texture.InterpolationType;
import de.nerogar.noise.util.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static org.lwjgl.stb.STBImage.*;

public class Texture2DLoader {

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
		InputStream inputStream = FileUtil.get(filename, FileUtil.TEXTURE_SUBFOLDER).asStream();
		ByteBuffer imageBuffer = null;

		try {
			try (ReadableByteChannel in = Channels.newChannel(inputStream)) {
				imageBuffer = BufferUtils.createByteBuffer(16 * 1024);
				while (true) {
					int bytes = in.read(imageBuffer);
					if (bytes == -1) {
						break;
					}
					if (imageBuffer.remaining() == 0) {
						ByteBuffer oldBuffer = imageBuffer;
						imageBuffer = BufferUtils.createByteBuffer(imageBuffer.capacity() * 2);
						oldBuffer.flip();
						imageBuffer.put(oldBuffer);
					}
				}
				imageBuffer.flip();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (imageBuffer == null) return null;

		try (MemoryStack stack = MemoryStack.stackPush()) {

			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);

			stbi_set_flip_vertically_on_load(true);
			if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
				Noise.getLogger().log(Logger.ERROR, "Could not read image information: " + filename + ", reason: " + stbi_failure_reason());
			}

			// Decode the image
			ByteBuffer image = stbi_load_from_memory(imageBuffer, w, h, comp, 4);
			if (image == null) {
				Noise.getLogger().log(Logger.ERROR, "Could not load image: " + filename + ", reason: " + stbi_failure_reason());
			}

			Noise.getLogger().log(Logger.INFO, "loaded texture: " + filename);
			return new Texture2D(textureName, w.get(0), h.get(0), image, interpolationType, DataType.BGRA_8_8_8_8I);
		}
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

}
