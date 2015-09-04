package de.nerogar.noise.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;

import de.nerogar.noise.Noise;
import de.nerogar.noise.debug.RessourceProfiler;
import de.nerogar.noise.util.Logger;

public class Texture2D extends Texture {

	public enum InterpolationType {
		LINEAR(GL_LINEAR, GL_LINEAR),
		NEAREST(GL_NEAREST, GL_NEAREST);

		public final int openglConstantMin;
		public final int openglConstantMag;

		InterpolationType(int openglConstantMin, int openglConstantMag) {
			this.openglConstantMin = openglConstantMin;
			this.openglConstantMag = openglConstantMag;
		}

	}

	public enum DataType {
		/**1 component, 8 bit, range is [0, 1]*/
		BGRA_8I(GL_R8, GL_BGRA, GL_UNSIGNED_BYTE),

		/**2 components, 8 bit each, range is [0, 1]*/
		BGRA_8_8I(GL_RG8, GL_BGRA, GL_UNSIGNED_BYTE),

		/**3 components, 8 bit each, range is [0, 1]*/
		BGRA_8_8_8I(GL_RGB8, GL_BGRA, GL_UNSIGNED_BYTE),

		/**4 components, 8 bit each, range is [0, 1]*/
		BGRA_8_8_8_8I(GL_RGBA8, GL_BGRA, GL_UNSIGNED_BYTE),

		/**1 component, 16 bit, half floating point precision*/
		BGRA_16F(GL_R16F, GL_BGRA, GL_HALF_FLOAT),

		/**2 components, 16 bit each, half floating point precision*/
		BGRA_16_16F(GL_RG16F, GL_BGRA, GL_HALF_FLOAT),

		/**3 components, 16 bit each, half floating point precision*/
		BGRA_16_16_16F(GL_RGB16F, GL_BGRA, GL_HALF_FLOAT),

		/**4 components, 16 bit each, half floating point precision*/
		BGRA_16_16_16_16F(GL_RGBA16F, GL_BGRA, GL_HALF_FLOAT),

		/**1 components, 32 bit, floating point precision*/
		BGRA_32F(GL_R32F, GL_BGRA, GL_FLOAT),

		/**2 components, 32 bit each, floating point precision*/
		BGRA_32_32F(GL_RG32F, GL_BGRA, GL_FLOAT),

		/**3 components, 32 bit each, floating point precision*/
		BGRA_32_32_32F(GL_RGB32F, GL_BGRA, GL_FLOAT),

		/**4 components, 32 bit each, floating point precision*/
		BGRA_32_32_32_32F(GL_RGBA32F, GL_BGRA, GL_FLOAT),

		/**1 component, 32 bits, only used for depth textures*/
		DEPTH(GL_DEPTH_COMPONENT32, GL_DEPTH_COMPONENT, GL_FLOAT);

		public final int internal;
		public final int format;
		public final int type;

		DataType(int internal, int format, int type) {
			this.internal = internal;
			this.format = format;
			this.type = type;
		}

	}

	private int id;
	private String name;
	private int width;
	private int height;
	private InterpolationType interpolationType;
	private DataType dataType;

	private boolean initialized;

	/**
	 * Creates an empty Texture
	 * 
	 * @param name the name of the texture
	 * @param width the width of the texture. 
	 * @param height the height of the texture
	 */
	public Texture2D(String name, int width, int height) {
		this(name, width, height, null, InterpolationType.LINEAR, DataType.BGRA_8_8_8_8I);
	}

	/**
	 * Creates a Texture with initial content
	 * 
	 * @param name the name of the texture
	 * @param width the width of the texture.
	 * @param height the height of the texture.
	 * @param colorBuffer initial content of the texture as an <code>IntBuffer</code>
	 * @param interpolationType method used to interpolate pixels
	 * @param dataType representation of the texture in memory
	 */
	public Texture2D(String name, int width, int height, ByteBuffer colorBuffer, InterpolationType interpolationType, DataType dataType) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.interpolationType = interpolationType;
		this.dataType = dataType;

		id = glGenTextures();
		createTexture(colorBuffer);

		Noise.getRessourceProfiler().incrementValue(RessourceProfiler.TEXTURE_COUNT);
	}

	protected void createTexture(ByteBuffer colorBuffer) {
		bind(0);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, interpolationType.openglConstantMin);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, interpolationType.openglConstantMin);

		glTexImage2D(GL_TEXTURE_2D, 0, dataType.internal, width, height, 0, dataType.format, dataType.type, colorBuffer);

		initialized = true;

		Noise.getRessourceProfiler().incrementValue(RessourceProfiler.TEXTURE_UPLOAD_COUNT);
		if (colorBuffer != null) Noise.getRessourceProfiler().addValue(RessourceProfiler.TEXTURE_UPLOAD_SIZE, colorBuffer.remaining());
	}

	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	protected void setWidth(int width) {
		this.width = width;
	}

	protected void setHeight(int height) {
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
		glBindTexture(GL_TEXTURE_2D, id);

		Noise.getRessourceProfiler().incrementValue(RessourceProfiler.TEXTURE_BINDS);
	}

	@Override
	public void cleanup() {
		glDeleteTextures(id);
		Texture2DLoader.unloadTexture(name);
		initialized = false;

		Noise.getRessourceProfiler().decrementValue(RessourceProfiler.TEXTURE_COUNT);
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Texture2D) return ((Texture2D) obj).id == id;

		return super.equals(obj);
	}

	@Override
	protected void finalize() throws Throwable {
		if (initialized) Logger.log(Logger.WARNING, "Texture not cleaned up. name: " + name);
	}

}
