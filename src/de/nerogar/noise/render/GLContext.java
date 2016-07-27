package de.nerogar.noise.render;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

//TODO expand this class, allow gl context creation without existing window

public class GLContext {

	private long glContextPointer;

	private GLCapabilities capabilities;

	protected GLContext(long glContextPointer) {
		this.glContextPointer = glContextPointer;
		capabilities = GL.createCapabilities();
	}

	public long getGlContextPointer() {
		return glContextPointer;
	}

	public GLCapabilities getCapabilities() {
		return capabilities;
	}

}
