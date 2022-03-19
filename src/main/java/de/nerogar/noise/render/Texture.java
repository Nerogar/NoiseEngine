package de.nerogar.noise.render;

import de.nerogar.noise.util.NoiseResource;

import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL30.*;

public abstract class Texture extends NoiseResource {

	public enum DataType {
		/**
		 * 1 component, 8 bit, range is [0, 1], input is BGRA
		 */
		BGRA_8I(GL_R8, GL_RGBA, GL_UNSIGNED_BYTE),

		/**
		 * 2 components, 8 bit each, range is [0, 1], input is BGRA
		 */
		BGRA_8_8I(GL_RG8, GL_RGBA, GL_UNSIGNED_BYTE),

		/**
		 * 3 components, 8 bit each, range is [0, 1], input is BGRA
		 */
		BGRA_8_8_8I(GL_RGB8, GL_RGBA, GL_UNSIGNED_BYTE),

		/**
		 * 4 components, 8 bit each, range is [0, 1], input is BGRA
		 */
		BGRA_8_8_8_8I(GL_RGBA8, GL_RGBA, GL_UNSIGNED_BYTE),

		/**
		 * 1 component, 16 bit, range is [0, 1], input is BGRA
		 */
		BGRA_16I(GL_R16, GL_RGBA, GL_UNSIGNED_SHORT),

		/**
		 * 2 components, 16 bit each, range is [0, 1], input is BGRA
		 */
		BGRA_16_16I(GL_RG16, GL_RGBA, GL_UNSIGNED_SHORT),

		/**
		 * 3 components, 16 bit each, range is [0, 1], input is BGRA
		 */
		BGRA_16_16_16I(GL_RGB16, GL_RGBA, GL_UNSIGNED_SHORT),

		/**
		 * 4 components, 16 bit each, range is [0, 1], input is BGRA
		 */
		BGRA_16_16_16_16I(GL_RGBA16, GL_RGBA, GL_UNSIGNED_SHORT),

		/**
		 * 4 components, 10 bits for R, G and B, 2 bits for A, range ist [0, 1], input is BGRA
		 */
		BGRA_10_10_10_2(GL_RGB10_A2, GL_RGBA, GL_FLOAT),

		/**
		 * 1 component, 16 bit, half floating point precision, input is BGRA
		 */
		BGRA_16F(GL_R16F, GL_RGBA, GL_HALF_FLOAT),

		/**
		 * 2 components, 16 bit each, half floating point precision, input is BGRA
		 */
		BGRA_16_16F(GL_RG16F, GL_RGBA, GL_HALF_FLOAT),

		/**
		 * 3 components, 16 bit each, half floating point precision, input is BGRA
		 */
		BGRA_16_16_16F(GL_RGB16F, GL_RGBA, GL_HALF_FLOAT),

		/**
		 * 4 components, 16 bit each, half floating point precision, input is BGRA
		 */
		BGRA_16_16_16_16F(GL_RGBA16F, GL_RGBA, GL_HALF_FLOAT),

		/**
		 * 1 components, 32 bit, floating point precision, input is R (a single float)
		 */
		BGRA_32F(GL_R32F, GL_R, GL_FLOAT),

		/**
		 * 2 components, 32 bit each, floating point precision, input is RG (2 floats)
		 */
		BGRA_32_32F(GL_RG32F, GL_RG, GL_FLOAT),

		/**
		 * 3 components, 32 bit each, floating point precision, input is RGB (3 floats)
		 */
		BGRA_32_32_32F(GL_RGB32F, GL_RGB, GL_FLOAT),

		/**
		 * 4 components, 32 bit each, floating point precision, input is RGBA (4 floats)
		 */
		BGRA_32_32_32_32F(GL_RGBA32F, GL_RGBA, GL_FLOAT),

		/**
		 * 1 component, 32 bits, only used for depth textures, input is a single float
		 */
		DEPTH(GL_DEPTH_COMPONENT32, GL_DEPTH_COMPONENT, GL_FLOAT),

		/**
		 * 2 components, 24 bits depth, 8 bit stencil
		 */
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

	public Texture(String name) {
		super(name);
	}

}
