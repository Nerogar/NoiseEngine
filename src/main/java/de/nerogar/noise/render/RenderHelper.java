package de.nerogar.noise.render;

import de.nerogar.noise.math.Matrix4fUtils;
import de.nerogar.noiseInterface.math.IMatrix4f;

import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

public class RenderHelper {

	private static IMatrix4f projectionMatrix;

	// do this for an instance of RenderHelper per context later
	private static HashMap<Long, VertexBufferObject> glContextVboMap;
	private static HashMap<Long, Shader>             glContextShaderMap;

	public static void blitTexture(Texture2D texture) {
		long currentContext = GLWindow.getCurrentContext();

		VertexBufferObject vbo = glContextVboMap.get(currentContext);
		Shader shader = glContextShaderMap.get(currentContext);

		if (vbo == null && shader == null) {
			vbo = createFullscreenQuad();
			glContextVboMap.put(currentContext, vbo);

			shader = getBlitShader();
			glContextShaderMap.put(currentContext, shader);
		}

		shader.activate();
		texture.bind(0);
		vbo.render();
		shader.deactivate();
	}

	public static void overlayTexture(Texture2D texture) {
		glEnable(GL_BLEND);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA, GL_ONE);

		blitTexture(texture);

		glDisable(GL_BLEND);
	}

	public static void overlayPremultipliedTexture(Texture2D texture) {
		glEnable(GL_BLEND);
		glBlendFuncSeparate(GL_ONE, GL_ONE_MINUS_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA, GL_ONE);

		blitTexture(texture);

		glDisable(GL_BLEND);
	}

	private static VertexBufferObjectIndexed createFullscreenQuad() {
		int[] componentCounts = { 2, 2 };

		int[] indices = { 0, 2, 1, 0, 3, 2 };
		float[] pos = { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f };
		float[] uv = { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f };

		return new VertexBufferObjectIndexed(componentCounts, 6, 4, indices, pos, uv);
	}

	private static Shader getBlitShader() {
		Shader shader = ShaderLoader.loadShader("<renderHelper/fullscreenBlit.vert>", "<renderHelper/fullscreenBlit.frag>");
		shader.activate();
		shader.setUniform1i("blitTexture", 0);
		shader.setUniformMat4f("projectionMatrix", projectionMatrix.asBuffer());
		shader.deactivate();

		return shader;
	}

	static {
		glContextVboMap = new HashMap<>();
		glContextShaderMap = new HashMap<>();

		projectionMatrix = Matrix4fUtils.getOrthographicProjection(0.0f, 1.0f, 1.0f, 0.0f, -1.0f, 1.0f);
	}

}
