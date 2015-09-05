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

		registerName(TEXTURE_COUNT, 100, "texture count");
		registerName(TEXTURE_BINDS, 200, "texture binds");
		registerName(TEXTURE_UPLOAD_COUNT, 10, "texture upload count");
		registerName(TEXTURE_UPLOAD_SIZE, 20000000, "texture upload size");

		registerName(VBO_COUNT, 100, "vbo count");
		registerName(VBO_CALLS, 80, "vbo calls");
		registerName(VBO_UPLOAD_COUNT, 10, "vbo upload count");
		registerName(VBO_UPLOAD_SIZE, 1000000, "vbo upload size");

		//registerName(SOUND_COUNT, "sound count");

		registerName(FRAMEBUFFER_COUNT, 20, "framebuffer count");
		registerName(FRAMEBUFFER_BINDS, 10, "framebuffer binds");

		registerName(SHADER_COUNT, 30, "shader count");
		registerName(SHADER_BINDS, 50, "shader binds");
		registerName(SHADER_COMPILE_COUNT, 10, "shader compile count");
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
