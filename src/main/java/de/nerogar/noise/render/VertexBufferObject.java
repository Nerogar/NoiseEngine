package de.nerogar.noise.render;

import de.nerogar.noise.util.NoiseResource;
import de.nerogar.noiseInterface.render.IVertexBufferObject;

import static org.lwjgl.opengl.GL11.*;

import java.util.HashMap;

public abstract class VertexBufferObject extends NoiseResource implements IVertexBufferObject {

	/**Renders points using 1 vertex. Equal to GL_POINTS*/
	public static final int POINTS = GL_POINTS;
	/**Renders lines using 2 vertices. Equal to GL_LINES*/
	public static final int LINES = GL_LINES;
	/**Renders triangles using 3 vertices. Equal to GL_TRIANGLES*/
	public static final int TRIANGLES = GL_TRIANGLES;

	protected final int renderType;
	protected final HashMap<Long, Integer> glContextVaoHandles;

	public VertexBufferObject(int renderType) {
		this.renderType = renderType;
		this.glContextVaoHandles = new HashMap<>();
	}

}
