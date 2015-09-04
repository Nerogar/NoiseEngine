package de.nerogar.noise.debug;

public class RessourceProfiler extends Profiler {

	public static final int TEXTURE_COUNT = 0;
	public static final int TEXTURE_BINDS = 1;
	public static final int TEXTURE_UPLOAD_COUNT = 2;
	public static final int TEXTURE_UPLOAD_SIZE = 3;

	public static final int VBO_COUNT = 4;
	public static final int VBO_CALLS = 5;
	public static final int VBO_UPLOAD_COUNT = 6;
	public static final int VBO_UPLOAD_SIZE = 7;

	public static final int SOUND_COUNT = 8;

	public static final int FRAMEBUFFER_COUNT = 9;
	public static final int FRAMEBUFFER_BINDS = 10;

	public static final int SHADER_COUNT = 11;
	public static final int SHADER_BINDS = 12;
	public static final int SHADER_COMPILE_COUNT = 13;

	public RessourceProfiler() {
		super("ressource", 256);

		registerName(TEXTURE_COUNT, "texture count");
		registerName(TEXTURE_BINDS, "texture binds");
		registerName(TEXTURE_UPLOAD_COUNT, "texture upload count");
		registerName(TEXTURE_UPLOAD_SIZE, "texture upload size");

		registerName(VBO_COUNT, "vbo count");
		registerName(VBO_CALLS, "vbo calls");
		registerName(VBO_UPLOAD_COUNT, "vbo upload count");
		registerName(VBO_UPLOAD_SIZE, "vbo upload size");

		//registerName(SOUND_COUNT, "sound count");

		registerName(FRAMEBUFFER_COUNT, "framebuffer count");
		registerName(FRAMEBUFFER_BINDS, "framebuffer binds");

		registerName(SHADER_COUNT, "shader count");
		registerName(SHADER_BINDS, "shader binds");
		registerName(SHADER_COMPILE_COUNT, "shader compile count");
	}

	@Override
	public void reset() {
		super.reset();

		setValue(TEXTURE_BINDS, 0);

		setValue(TEXTURE_UPLOAD_COUNT, 0);
		setValue(TEXTURE_UPLOAD_SIZE, 0);

		setValue(VBO_CALLS, 0);
		setValue(VBO_UPLOAD_COUNT, 0);
		setValue(VBO_UPLOAD_SIZE, 0);

		setValue(FRAMEBUFFER_BINDS, 0);

		setValue(SHADER_BINDS, 0);
		setValue(SHADER_COMPILE_COUNT, 0);
	}

}
