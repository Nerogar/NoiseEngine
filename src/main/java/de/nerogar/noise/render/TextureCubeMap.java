package de.nerogar.noise.render;

import de.nerogar.noise.Noise;
import de.nerogar.noise.debug.ResourceProfiler;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;

public class TextureCubeMap extends Texture {

	private int                         id;
	private String                      name;
	private int                         width;
	private int                         height;
	private Texture2D.InterpolationType interpolationType;
	private Texture2D.DataType          dataType;

	private boolean initialized;

	protected TextureCubeMap(String name, int width, int height, ByteBuffer[] colorBuffer) {
		this(name, width, height, colorBuffer, Texture2D.InterpolationType.LINEAR_MIPMAP, Texture2D.DataType.BGRA_8_8_8_8I);
	}

	public TextureCubeMap(String name, int width, int height, ByteBuffer[] colorBuffer, Texture2D.InterpolationType interpolationType, Texture2D.DataType dataType) {
		super(name);

		this.name = name;
		this.width = width;
		this.height = height;
		this.interpolationType = interpolationType;
		this.dataType = dataType;

		id = glGenTextures();
		createTexture(colorBuffer);

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.TEXTURE_COUNT);
	}

	protected void createTexture(ByteBuffer[] colorBuffer) {
		// TODO: bind(0);

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

	// TODO: support bindless cube textures
	/*
	@Override
	public void bind(int slot) {
		glActiveTexture(texturePositions[slot]);
		glBindTexture(GL_TEXTURE_CUBE_MAP, id);

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.TEXTURE_BINDS);
	}
	*/

	@Override
	public boolean cleanup() {
		if (!super.cleanup()) return false;

		glDeleteTextures(id);
		initialized = false;

		Noise.getResourceProfiler().decrementValue(ResourceProfiler.TEXTURE_COUNT);

		return true;
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

}
