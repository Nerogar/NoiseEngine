package de.nerogar.noise.render;

import de.nerogar.noise.util.NoiseResource;

import static org.lwjgl.opengl.GL13.*;

public abstract class Texture extends NoiseResource {

	public static final int MAX_TEXTURE_COUNT = 32;

	protected static int[] texturePositions = {
			GL_TEXTURE0, GL_TEXTURE1, GL_TEXTURE2, GL_TEXTURE3,
			GL_TEXTURE4, GL_TEXTURE5, GL_TEXTURE6, GL_TEXTURE7,
			GL_TEXTURE8, GL_TEXTURE9, GL_TEXTURE10, GL_TEXTURE11,
			GL_TEXTURE12, GL_TEXTURE13, GL_TEXTURE14, GL_TEXTURE15,
			GL_TEXTURE16, GL_TEXTURE17, GL_TEXTURE18, GL_TEXTURE19,
			GL_TEXTURE20, GL_TEXTURE21, GL_TEXTURE22, GL_TEXTURE23,
			GL_TEXTURE24, GL_TEXTURE25, GL_TEXTURE26, GL_TEXTURE27,
			GL_TEXTURE28, GL_TEXTURE29, GL_TEXTURE30, GL_TEXTURE31,
	};

	public Texture(String name) {
		super(name);
	}

	public abstract void bind(int slot);

}
