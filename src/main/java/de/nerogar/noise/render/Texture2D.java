package de.nerogar.noise.render;

import de.nerogar.noise.Noise;
import de.nerogar.noise.debug.ResourceProfiler;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL30.*;

public class Texture2D extends Texture {

	public static final float MAX_ANISOTROPIC_FILTERING = glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);

	public enum InterpolationType {
		LINEAR(GL_LINEAR, GL_LINEAR, false),
		NEAREST(GL_NEAREST, GL_NEAREST, false),
		LINEAR_MIPMAP(GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR, true),
		NEAREST_MIPMAP(GL_NEAREST_MIPMAP_NEAREST, GL_NEAREST, true);

		public final int     openglConstantMin;
		public final int     openglConstantMag;
		public final boolean generateMipMaps;

		InterpolationType(int openglConstantMin, int openglConstantMag, boolean generateMipMaps) {
			this.openglConstantMin = openglConstantMin;
			this.openglConstantMag = openglConstantMag;
			this.generateMipMaps = generateMipMaps;
		}

	}

	public enum DataType {
		/** 1 component, 8 bit, range is [0, 1], input is BGRA */
		BGRA_8I(GL_R8, GL_RGBA, GL_UNSIGNED_BYTE),

		/** 2 components, 8 bit each, range is [0, 1], input is BGRA */
		BGRA_8_8I(GL_RG8, GL_RGBA, GL_UNSIGNED_BYTE),

		/** 3 components, 8 bit each, range is [0, 1], input is BGRA */
		BGRA_8_8_8I(GL_RGB8, GL_RGBA, GL_UNSIGNED_BYTE),

		/** 4 components, 8 bit each, range is [0, 1], input is BGRA */
		BGRA_8_8_8_8I(GL_RGBA8, GL_RGBA, GL_UNSIGNED_BYTE),

		/** 4 components, 10 bits for R, G and B, 2 bits for A, range ist [0, 1], input is BGRA */
		BGRA_10_10_10_2(GL_RGB10_A2, GL_RGBA, GL_FLOAT),

		/** 1 component, 16 bit, half floating point precision, input is BGRA */
		BGRA_16F(GL_R16F, GL_RGBA, GL_HALF_FLOAT),

		/** 2 components, 16 bit each, half floating point precision, input is BGRA */
		BGRA_16_16F(GL_RG16F, GL_RGBA, GL_HALF_FLOAT),

		/** 3 components, 16 bit each, half floating point precision, input is BGRA */
		BGRA_16_16_16F(GL_RGB16F, GL_RGBA, GL_HALF_FLOAT),

		/** 4 components, 16 bit each, half floating point precision, input is BGRA */
		BGRA_16_16_16_16F(GL_RGBA16F, GL_RGBA, GL_HALF_FLOAT),

		/** 1 components, 32 bit, floating point precision, input is R (a single float) */
		BGRA_32F(GL_R32F, GL_R, GL_FLOAT),

		/** 2 components, 32 bit each, floating point precision, input is RG (2 floats) */
		BGRA_32_32F(GL_RG32F, GL_RG, GL_FLOAT),

		/** 3 components, 32 bit each, floating point precision, input is RGB (3 floats) */
		BGRA_32_32_32F(GL_RGB32F, GL_RGB, GL_FLOAT),

		/** 4 components, 32 bit each, floating point precision, input is RGBA (4 floats) */
		BGRA_32_32_32_32F(GL_RGBA32F, GL_RGBA, GL_FLOAT),

		/** 1 component, 32 bits, only used for depth textures, input is a single float */
		DEPTH(GL_DEPTH_COMPONENT32, GL_DEPTH_COMPONENT, GL_FLOAT),

		/** 2 components, 24 bits depth, 8 bit stencil */
		DEPTH_STENCIL(GL_DEPTH24_STENCIL8, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8);

		public final int internal;
		public final int format;
		public final int type;

		DataType(int internal, int format, int type) {
			this.internal = internal;
			this.format = format;
			this.type = type;
		}

	}

	private int               id;
	private String            name;
	private int               width;
	private int               height;
	private InterpolationType interpolationType;
	private DataType          dataType;
	private float             anisotropicFiltering;

	private boolean initialized;

	/**
	 * Creates an empty Texture
	 *
	 * @param name   the name of the texture
	 * @param width  the width of the texture.
	 * @param height the height of the texture
	 */
	public Texture2D(String name, int width, int height) {
		this(name, width, height, null, InterpolationType.LINEAR, DataType.BGRA_8_8_8_8I);
	}

	/**
	 * Creates a Texture with initial content
	 *
	 * @param name              the name of the texture
	 * @param width             the width of the texture.
	 * @param height            the height of the texture.
	 * @param colorBuffer       initial content of the texture as an <code>IntBuffer</code>
	 * @param interpolationType method used to interpolate pixels
	 * @param dataType          representation of the texture in memory
	 */
	public Texture2D(String name, int width, int height, ByteBuffer colorBuffer, InterpolationType interpolationType, DataType dataType) {
		this(name, width, height, colorBuffer, interpolationType, dataType, 1);
	}

	/**
	 * Creates a Texture with initial content
	 *
	 * @param name                 the name of the texture
	 * @param width                the width of the texture.
	 * @param height               the height of the texture.
	 * @param colorBuffer          initial content of the texture as an <code>IntBuffer</code>
	 * @param interpolationType    method used to interpolate pixels
	 * @param dataType             representation of the texture in memory
	 * @param anisotropicFiltering the amount of anisotropic filtering
	 */
	public Texture2D(String name, int width, int height, ByteBuffer colorBuffer, InterpolationType interpolationType, DataType dataType, float anisotropicFiltering) {
		super(name);

		this.name = name;
		this.width = width;
		this.height = height;
		this.interpolationType = interpolationType;
		this.dataType = dataType;
		this.anisotropicFiltering = anisotropicFiltering;

		id = glGenTextures();
		createTexture(colorBuffer);

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.TEXTURE_COUNT);
	}

	protected void createTexture(ByteBuffer colorBuffer) {
		bind(0);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, interpolationType.openglConstantMin);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, interpolationType.openglConstantMag);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		glTexImage2D(GL_TEXTURE_2D, 0, dataType.internal, width, height, 0, dataType.format, dataType.type, colorBuffer);

		if (interpolationType.generateMipMaps) {
			glGenerateMipmap(GL_TEXTURE_2D);
		}

		initialized = true;

		setAnisotropicFilteringParameter();

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.TEXTURE_UPLOAD_COUNT);
		if (colorBuffer != null) Noise.getResourceProfiler().addValue(ResourceProfiler.TEXTURE_UPLOAD_SIZE, colorBuffer.remaining());
	}

	private void setAnisotropicFilteringParameter() {
		anisotropicFiltering = Math.min(MAX_ANISOTROPIC_FILTERING, anisotropicFiltering);

		if (!initialized) return;

		if (anisotropicFiltering > 1 && GLWindow.getCurrentWindow().getGLContext().getCapabilities().GL_EXT_texture_filter_anisotropic) {
			glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisotropicFiltering);
		} else if (anisotropicFiltering <= 1 && GLWindow.getCurrentWindow().getGLContext().getCapabilities().GL_EXT_texture_filter_anisotropic) {
			glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, 1);
		}
	}

	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	/**
	 * Sets the new width of this texture.
	 * The width does not change until {@link Texture2D#createTexture createTexture} is called.
	 *
	 * @param width the new width
	 * @return this Texture2D object
	 */
	protected Texture2D setWidth(int width) {
		this.width = width;
		return this;
	}

	/**
	 * Sets the new height of this texture.
	 * The height does not change until {@link Texture2D#createTexture createTexture} is called.
	 *
	 * @param height the new height
	 * @return this Texture2D object
	 */
	protected Texture2D setHeight(int height) {
		this.height = height;
		return this;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * Sets the new anisotropic filtering amount. Anything <= 1 will disable it.
	 * <br>
	 * Note: this binds the texture to the slot 0.
	 *
	 * @param anisotropicFiltering the new amount of anisotropic filtering
	 * @return this Texture2D object
	 */
	public Texture2D setAnisotropicFiltering(float anisotropicFiltering) {
		this.anisotropicFiltering = anisotropicFiltering;

		bind(0);

		setAnisotropicFilteringParameter();

		return this;
	}

	public float getAnisotropicFiltering() {
		return anisotropicFiltering;
	}

	@Override
	public void bind(int slot) {
		glActiveTexture(texturePositions[slot]);
		glBindTexture(GL_TEXTURE_2D, id);

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.TEXTURE_BINDS);
	}

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
		if (obj instanceof Texture2D) return ((Texture2D) obj).id == id;

		return super.equals(obj);
	}

}
