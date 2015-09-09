package de.nerogar.noise.debug;

import de.nerogar.noise.util.Color;

public class RessourceProfiler extends Profiler {

	public static final int TEXTURE_COUNT			= 0;
	public static final int TEXTURE_BINDS			= 1;
	public static final int TEXTURE_UPLOAD_COUNT	= 2;
	public static final int TEXTURE_UPLOAD_SIZE		= 3;

	public static final int VBO_COUNT				= 4;
	public static final int VBO_CALLS				= 5;
	public static final int VBO_UPLOAD_COUNT		= 6;
	public static final int VBO_UPLOAD_SIZE			= 7;

	public static final int SOUND_COUNT				= 8;

	public static final int FRAMEBUFFER_COUNT		= 9;
	public static final int FRAMEBUFFER_BINDS		= 10;

	public static final int SHADER_COUNT			= 11;
	public static final int SHADER_BINDS			= 12;
	public static final int SHADER_COMPILE_COUNT	= 13;

	public RessourceProfiler() {
		super("ressource");

		registerProperty(TEXTURE_COUNT,			new Color(0.2f, 0.0f, 1.0f, 0.0f),	"texture count");
		registerProperty(TEXTURE_BINDS,			new Color(0.4f, 0.0f, 1.0f, 0.0f),	"texture binds");
		registerProperty(TEXTURE_UPLOAD_COUNT,	new Color(0.0f, 0.2f, 1.0f, 0.0f),	"texture upload count");
		registerProperty(TEXTURE_UPLOAD_SIZE,	new Color(0.0f, 0.4f, 1.0f, 0.0f),	"texture upload size");

		registerProperty(VBO_COUNT,				new Color(1.0f, 0.2f, 0.0f, 0.0f),	"vbo count");
		registerProperty(VBO_CALLS,				new Color(1.0f, 0.4f, 0.0f, 0.0f),	"vbo calls");
		registerProperty(VBO_UPLOAD_COUNT,		new Color(1.0f, 0.0f, 0.2f, 0.0f),	"vbo upload count");
		registerProperty(VBO_UPLOAD_SIZE,		new Color(1.0f, 0.0f, 0.4f, 0.0f),	"vbo upload size");

		registerProperty(FRAMEBUFFER_COUNT,		new Color(1.0f, 0.8f, 0.0f, 0.0f),	"framebuffer count");
		registerProperty(FRAMEBUFFER_BINDS,		new Color(1.0f, 1.0f, 0.0f, 0.0f),	"framebuffer binds");

		registerProperty(SHADER_COUNT,			new Color(0.5f, 0.5f, 1.0f, 0.0f),	"shader count");
		registerProperty(SHADER_BINDS,			new Color(0.5f, 0.7f, 1.0f, 0.0f),	"shader binds");
		registerProperty(SHADER_COMPILE_COUNT,	new Color(0.5f, 0.9f, 1.0f, 0.0f),	"shader compile count");
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