package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.debug.Profiler;
import de.nerogar.noise.util.Color;

class DeferredRendererProfiler extends Profiler {

	private static final int OBJECT = 0;
	private static final int LIGHT = 1;
	private static final int EFFECT = 2;
	private static final int TRIANGLES = 3;


	public static final int OBJECT_COUNT			= 0;
	public static final int OBJECT_RENDER_COUNT		= 1;

	public static final int LIGHT_COUNT				= 2;
	public static final int LIGHT_RENDER_COUNT		= 3;

	public static final int EFFECT_COUNT			= 4;
	public static final int EFFECT_RENDER_COUNT		= 5;

	public static final int TRIANGLE_RENDER_COUNT	= 6;

	public DeferredRendererProfiler() {
		super("deferred renderer", false);

		registerProperty(OBJECT_COUNT,			OBJECT,		new Color(1.0f, 0.0f, 0.0f, 1.0f),	"object count");
		registerProperty(OBJECT_RENDER_COUNT,	OBJECT,		new Color(1.0f, 0.4f, 0.0f, 1.0f),	"object render count");

		registerProperty(LIGHT_COUNT,			LIGHT,		new Color(1.0f, 1.0f, 0.0f, 1.0f),	"light count");
		registerProperty(LIGHT_RENDER_COUNT,	LIGHT,		new Color(1.0f, 1.0f, 0.4f, 1.0f),	"light render count");

		registerProperty(EFFECT_COUNT,			EFFECT,		new Color(0.0f, 1.0f, 0.0f, 1.0f),	"effect count");
		registerProperty(EFFECT_RENDER_COUNT,	EFFECT,		new Color(0.0f, 1.0f, 0.5f, 1.0f),	"effect render count");

		registerProperty(TRIANGLE_RENDER_COUNT,	TRIANGLES,	new Color(0.0f, 0.5f, 1.0f, 1.0f),	"triangle render count");
	}

	@Override
	public void reset() {
		super.reset();

		setValue(OBJECT_RENDER_COUNT, 0);

		setValue(LIGHT_COUNT, 0);
		setValue(LIGHT_RENDER_COUNT, 0);

		setValue(EFFECT_COUNT, 0);
		setValue(EFFECT_RENDER_COUNT, 0);

		setValue(TRIANGLE_RENDER_COUNT, 0);
	}

}
