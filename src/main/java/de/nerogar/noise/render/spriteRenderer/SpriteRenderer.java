package de.nerogar.noise.render.spriteRenderer;

import static org.lwjgl.opengl.GL11.*;

import java.util.*;

import de.nerogar.noise.render.*;
import de.nerogar.noise.util.*;

/**
 * A renderer that renders simple 2D sprites with a single texture.
 * Sprites are static, moving them is not supported.
 * <p>
 * A new VertexBufferObject is created for every texture used. Try to keep texture count low.
 */
public class SpriteRenderer implements IRenderer<Sprite2D> {

	private class VboContainer {
		public List<Sprite2D> spriteList;
		public VertexBufferObjectIndexed vbo;
		public boolean dirty;

		public VboContainer() {
			this.spriteList = new ArrayList<Sprite2D>();
		}
	}

	private HashMap<Texture2D, VboContainer> vboMap;
	private Shader shader;

	private Matrix4f projectionMatrix;
	private boolean projectionMatrixUpdated;

	/**
	 * Creates a SpriteRenderer. the camera can see everything between (0, 0, 0) and (width, height, depth).
	 * Higher z values are near to the camera.
	 * 
	 * @param width the initial width
	 * @param height the initial height
	 * @param depth the initial depth
	 */
	public SpriteRenderer(float width, float height, float depth) {
		vboMap = new HashMap<Texture2D, SpriteRenderer.VboContainer>();

		projectionMatrix = new Matrix4f();
		Matrix4fUtils.setOrthographicProjection(projectionMatrix, 0.0f, width, height, 0.0f, depth, 0.0f);
		projectionMatrixUpdated = true;

		shader = ShaderLoader.loadShader("<spriteRenderer/sprite.vert>", "<spriteRenderer/sprite.frag>");
	}

	@Override
	public void addObject(Sprite2D object) {
		VboContainer container = vboMap.get(object.texture);

		if (container == null) {
			container = new VboContainer();

			//container.spriteList = new ArrayList<Sprite2D>();
			//spriteLists.put(object.texture, container.spriteList);

			vboMap.put(object.texture, container);
		}

		container.spriteList.add(object);
		container.dirty = true;

		//rebuildVBO(object.texture, container.spriteList);
	}

	@Override
	public void removeObject(Sprite2D object) {
		VboContainer container = vboMap.get(object.texture);

		if (container != null) {
			container.spriteList.remove(object);
			rebuildVBO(container);
		}
	}

	private void rebuildVBO(VboContainer container) {
		if (container.vbo != null) {
			container.vbo.cleanup();
		}

		int[] componentCount = { 3, 2 };
		int[] indices = new int[container.spriteList.size() * 6];

		float[] pos = new float[container.spriteList.size() * 4 * 3];
		float[] uv = new float[container.spriteList.size() * 4 * 2];

		Vector3f[] spritePos;
		Vector2f[] spriteUV;

		for (int i = 0; i < container.spriteList.size(); i++) {
			spritePos = container.spriteList.get(i).pos;
			spriteUV = container.spriteList.get(i).uv;

			pos[i * 12 + 0] = spritePos[0].getX();
			pos[i * 12 + 1] = spritePos[0].getY();
			pos[i * 12 + 2] = spritePos[0].getZ();
			uv[i * 8 + 0] = spriteUV[0].getX();
			uv[i * 8 + 1] = spriteUV[0].getY();

			pos[i * 12 + 3] = spritePos[1].getX();
			pos[i * 12 + 4] = spritePos[1].getY();
			pos[i * 12 + 5] = spritePos[1].getZ();
			uv[i * 8 + 2] = spriteUV[1].getX();
			uv[i * 8 + 3] = spriteUV[1].getY();

			pos[i * 12 + 6] = spritePos[2].getX();
			pos[i * 12 + 7] = spritePos[2].getY();
			pos[i * 12 + 8] = spritePos[2].getZ();
			uv[i * 8 + 4] = spriteUV[2].getX();
			uv[i * 8 + 5] = spriteUV[2].getY();

			pos[i * 12 + 9] = spritePos[3].getX();
			pos[i * 12 + 10] = spritePos[3].getY();
			pos[i * 12 + 11] = spritePos[3].getZ();
			uv[i * 8 + 6] = spriteUV[3].getX();
			uv[i * 8 + 7] = spriteUV[3].getY();

			indices[i * 6 + 0] = i * 4 + 0;
			indices[i * 6 + 1] = i * 4 + 1;
			indices[i * 6 + 2] = i * 4 + 2;
			indices[i * 6 + 3] = i * 4 + 2;
			indices[i * 6 + 4] = i * 4 + 3;
			indices[i * 6 + 5] = i * 4 + 0;
		}

		container.vbo = new VertexBufferObjectIndexed(componentCount, indices.length, container.spriteList.size() * 4, indices, pos, uv);

		container.dirty = false;
	}

	@Override
	public void setFrameBufferResolution(int width, int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void rebuild() {
		for (VboContainer container : vboMap.values()) {
			rebuildVBO(container);
		}
	}

	@Override
	public void render(Matrix4f viewMtrix) {
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);

		shader.activate();
		shader.setUniform1i("textureColor", 0);
		shader.setUniformMat4f("viewMatrix", viewMtrix.asBuffer());
		if (projectionMatrixUpdated) shader.setUniformMat4f("projectionMatrix", projectionMatrix.asBuffer());

		for (Texture2D texture : vboMap.keySet()) {
			VboContainer container = vboMap.get(texture);

			if (container.dirty) rebuildVBO(container);

			texture.bind(0);
			container.vbo.render();
		}

		shader.deactivate();

		glDisable(GL_DEPTH_TEST);
	}

	public void setProjectionSize(float width, float height, float depth) {
		Matrix4fUtils.setOrthographicProjection(projectionMatrix, 0.0f, width, height, 0.0f, depth, 0.0f);
	}

	public void cleanup() {
		for (VboContainer container : vboMap.values()) {
			container.vbo.cleanup();
		}
	}

	@Override
	public FrameBufferObject getRenderTarget() {
		// TODO implement getRenderTarget
		return null;
	}

}
