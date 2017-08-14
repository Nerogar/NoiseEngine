package de.nerogar.noise.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;

import java.nio.ByteBuffer;

import de.nerogar.noise.Noise;
import de.nerogar.noise.debug.ResourceProfiler;
import de.nerogar.noise.util.Logger;

import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL32.*;

public class TextureCubeMap extends Texture {

	private int id;
	private String[] filenames;
	private String name;
	private int width;
	private int height;
	private Texture2D.InterpolationType interpolationType;
	private Texture2D.DataType dataType;

	private boolean initialized;

	protected TextureCubeMap(String name, int width, int height, ByteBuffer[] colorBuffer) {
		this(name, width, height, colorBuffer, Texture2D.InterpolationType.LINEAR_MIPMAP, Texture2D.DataType.BGRA_8_8_8_8I);
	}

	public TextureCubeMap(String name, int width, int height, ByteBuffer[] colorBuffer, Texture2D.InterpolationType interpolationType, Texture2D.DataType dataType) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.interpolationType = interpolationType;
		this.dataType = dataType;

		id = glGenTextures();
		createTexture(colorBuffer);

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.TEXTURE_COUNT);
	}

	protected void setFilenames(String[] filenames) {
		this.filenames = filenames;
	}

	protected void createTexture(ByteBuffer[] colorBuffer) {
		bind(0);

		glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, dataType.internal, width, height, 0, dataType.format, dataType.type, colorBuffer[0]);
		glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, dataType.internal, width, height, 0, dataType.format, dataType.type, colorBuffer[1]);
		glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, dataType.internal, width, height, 0, dataType.format, dataType.type, colorBuffer[2]);
		glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, dataType.internal, width, height, 0, dataType.format, dataType.type, colorBuffer[3]);
		glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, dataType.internal, width, height, 0, dataType.format, dataType.type, colorBuffer[4]);
		glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, dataType.internal, width, height, 0, dataType.format, dataType.type, colorBuffer[5]);

		// TODO: use interpolationType

		//if (interpolationType.generateMipMaps) {
			glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
			glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
		//}

		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

		glBindTexture(GL_TEXTURE_CUBE_MAP, 0);

		initialized = true;


		Noise.getResourceProfiler().incrementValue(ResourceProfiler.TEXTURE_UPLOAD_COUNT);
		for (ByteBuffer buff : colorBuffer) {
			if (buff != null) Noise.getResourceProfiler().addValue(ResourceProfiler.TEXTURE_UPLOAD_SIZE, buff.remaining());
		}
	}

	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getFilename() {
		return filenames[0];
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public void bind(int slot) {
		glActiveTexture(texturePositions[slot]);
		glBindTexture(GL_TEXTURE_CUBE_MAP, id);

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.TEXTURE_BINDS);
	}

	@Override
	public void cleanup() {
		glDeleteTextures(id);
		Texture2DLoader.unloadTexture(filenames[0]);
		initialized = false;

		Noise.getResourceProfiler().decrementValue(ResourceProfiler.TEXTURE_COUNT);
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TextureCubeMap) return ((TextureCubeMap) obj).id == id;

		return super.equals(obj);
	}

	@Override
	protected void finalize() throws Throwable {
		if (initialized) Noise.getLogger().log(Logger.WARNING, "Texture not cleaned up. name: " + name);
	}

}
