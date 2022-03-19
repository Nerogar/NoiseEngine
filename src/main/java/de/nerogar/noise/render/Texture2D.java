package de.nerogar.noise.render;

import de.nerogar.noise.Noise;
import de.nerogar.noise.debug.ResourceProfiler;
import de.nerogar.noise.exception.InvalidStateException;
import org.lwjgl.opengl.ARBBindlessTexture;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.*;

public class Texture2D extends Texture {

	public static final float MAX_ANISOTROPIC_FILTERING = glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);

	private int width;
	private int height;

	private int     name;
	private long    handle;
	private boolean isResident;

	private InterpolationType interpolationType;
	private DataType          dataType;
	private float             anisotropicFiltering;
	private int               wrapMode;

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

		this.width = width;
		this.height = height;
		this.interpolationType = interpolationType;
		this.dataType = dataType;
		this.anisotropicFiltering = anisotropicFiltering;
		this.wrapMode = GL_REPEAT;

		this.name = glCreateTextures(GL_TEXTURE_2D);
		createTexture(colorBuffer);

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.TEXTURE_COUNT);
	}

	private void createTexture(ByteBuffer colorBuffer) {
		glTextureParameteri(name, GL_TEXTURE_MIN_FILTER, interpolationType.openglConstantMin);
		glTextureParameteri(name, GL_TEXTURE_MAG_FILTER, interpolationType.openglConstantMag);
		glTextureParameteri(name, GL_TEXTURE_WRAP_S, wrapMode);
		glTextureParameteri(name, GL_TEXTURE_WRAP_T, wrapMode);

		int maxDim = Math.max(width, height);
		int levels = 1 + Integer.numberOfTrailingZeros(Integer.highestOneBit(maxDim));
		glTextureStorage2D(name, levels, dataType.internal, width, height);

		if (colorBuffer != null) {
			glTextureSubImage2D(name, 0, 0, 0, width, height, dataType.format, dataType.type, colorBuffer);
		}

		if (interpolationType.generateMipMaps) {
			glGenerateTextureMipmap(name);
		}

		setAnisotropicFilteringParameter();

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.TEXTURE_UPLOAD_COUNT);
		if (colorBuffer != null) {
			Noise.getResourceProfiler().addValue(ResourceProfiler.TEXTURE_UPLOAD_SIZE, colorBuffer.remaining());
		}
	}

	public int getName() {
		return name;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	private void setAnisotropicFilteringParameter() {
		anisotropicFiltering = Math.min(MAX_ANISOTROPIC_FILTERING, anisotropicFiltering);
		anisotropicFiltering = Math.max(1, anisotropicFiltering);

		if (anisotropicFiltering > 1 && GLWindow.getCurrentWindow().getGLContext().getCapabilities().GL_EXT_texture_filter_anisotropic) {
			glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisotropicFiltering);
		}
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
		if (isResident) {
			throw new InvalidStateException("Resident Textures can not be modified");
		}

		this.anisotropicFiltering = anisotropicFiltering;
		setAnisotropicFilteringParameter();
		return this;
	}

	public float getAnisotropicFiltering() {
		return anisotropicFiltering;
	}

	/**
	 * Sets the texture wrap mode. This action is effective immediately.
	 * The texture is bound to slot 0 in the process.
	 *
	 * @param wrapMode the OpenGL wrap mode.
	 */
	public void setWrapMode(int wrapMode) {
		if (isResident) {
			throw new InvalidStateException("Resident Textures can not be modified");
		}

		this.wrapMode = wrapMode;
		glTextureParameteri(name, GL_TEXTURE_WRAP_S, wrapMode);
		glTextureParameteri(name, GL_TEXTURE_WRAP_T, wrapMode);
	}

	public int getWrapMode() {
		return wrapMode;
	}

	public DataType getDataType() {
		return dataType;
	}

	public InterpolationType getInterpolationType() {
		return interpolationType;
	}

	private void makeResident() {
		handle = ARBBindlessTexture.glGetTextureHandleARB(name);
		ARBBindlessTexture.glMakeTextureHandleResidentARB(handle);
	}

	public long getHandle() {
		if (!isResident) {
			makeResident();
		}
		return handle;
	}

	@Override
	public boolean cleanup() {
		if (!super.cleanup()) return false;

		if (isResident) {
			ARBBindlessTexture.glMakeTextureHandleNonResidentARB(handle);
			handle = 0;
		}
		glDeleteTextures(name);
		name = 0;

		Noise.getResourceProfiler().decrementValue(ResourceProfiler.TEXTURE_COUNT);

		return true;
	}

	@Override
	public int hashCode() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Texture2D) return ((Texture2D) obj).name == name;

		return super.equals(obj);
	}

}
