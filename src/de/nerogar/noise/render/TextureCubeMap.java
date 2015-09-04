package de.nerogar.noise.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;

import java.nio.ByteBuffer;

import de.nerogar.noise.Noise;
import de.nerogar.noise.debug.RessourceProfiler;
import de.nerogar.noise.util.Logger;

public class TextureCubeMap extends Texture {

	private int id;
	private String[] filenames;
	private String name;
	private int width;
	private int height;

	private boolean initialized;

	protected TextureCubeMap(String name, int width, int height, ByteBuffer[] colorBuffer) {
		this.name = name;
		this.width = width;
		this.height = height;

		createTexture(colorBuffer);

		Noise.getRessourceProfiler().incrementValue(RessourceProfiler.TEXTURE_COUNT);
	}

	protected void setFilenames(String[] filenames) {
		this.filenames = filenames;
	}

	protected void createTexture(ByteBuffer[] colorBuffer) {
		bind(0);

		glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GL_RGBA8, width, height, 0, GL_BGRA, GL_UNSIGNED_BYTE, colorBuffer[0]);
		glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GL_RGBA8, width, height, 0, GL_BGRA, GL_UNSIGNED_BYTE, colorBuffer[1]);
		glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GL_RGBA8, width, height, 0, GL_BGRA, GL_UNSIGNED_BYTE, colorBuffer[2]);
		glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GL_RGBA8, width, height, 0, GL_BGRA, GL_UNSIGNED_BYTE, colorBuffer[3]);
		glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GL_RGBA8, width, height, 0, GL_BGRA, GL_UNSIGNED_BYTE, colorBuffer[4]);
		glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GL_RGBA8, width, height, 0, GL_BGRA, GL_UNSIGNED_BYTE, colorBuffer[5]);

		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

		glBindTexture(GL_TEXTURE_CUBE_MAP, 0);

		initialized = true;

		Noise.getRessourceProfiler().incrementValue(RessourceProfiler.TEXTURE_UPLOAD_COUNT);
		for (ByteBuffer buff : colorBuffer) {
			if (buff != null) Noise.getRessourceProfiler().addValue(RessourceProfiler.TEXTURE_UPLOAD_SIZE, buff.remaining());
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

		Noise.getRessourceProfiler().incrementValue(RessourceProfiler.TEXTURE_BINDS);
	}

	@Override
	public void cleanup() {
		glDeleteTextures(id);
		Texture2DLoader.unloadTexture(filenames[0]);
		initialized = false;

		Noise.getRessourceProfiler().decrementValue(RessourceProfiler.TEXTURE_COUNT);
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
		if (initialized) Logger.log(Logger.WARNING, "Texture not cleaned up. name: " + name);
	}

}
