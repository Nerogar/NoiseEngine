package de.nerogar.noise.render;

import static org.lwjgl.opengl.GL11.*;

import java.util.HashMap;

public abstract class VertexBufferObject {

	/**Renders points using 1 vertex. Equal to GL_POINTS*/
	public static final int POINTS = GL_POINTS;
	/**Renders lines using 2 vertices. Equal to GL_LINES*/
	public static final int LINES = GL_LINES;
	/**Renders triangles using 3 vertices. Equal to GL_TRIANGLES*/
	public static final int TRIANGLES = GL_TRIANGLES;

	protected int renderType;
	protected HashMap<Long, Integer> glContextVaoHandles;
	protected boolean deleted;
	
	public abstract void render();

	public abstract void cleanup();

}