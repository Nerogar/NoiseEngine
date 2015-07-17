package de.nerogar.noise.render;

import de.nerogar.noise.util.Matrix4f;
import de.nerogar.noise.util.Matrix4fUtils;

public class RenderHelper {

	private static VertexBufferObject fullscreenQuad;
	private static Shader shader;
	private static Matrix4f projectionMatrix;

	public static void blitTexture(Texture2D texture) {
		shader.activate();
		texture.bind(0);
		fullscreenQuad.render();
		shader.deactivate();
	}

	private static VertexBufferObjectIndexed createFullscreenQuad() {
		int[] componentCounts = { 2, 2 };

		int[] indices = { 0, 2, 1, 0, 3, 2 };
		float[] pos = { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f };
		float[] uv = { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f };

		return new VertexBufferObjectIndexed(componentCounts, 6, 4, indices, pos, uv);
	}

	static {
		fullscreenQuad = createFullscreenQuad();

		projectionMatrix = Matrix4fUtils.getOrthographicProjection(0.0f, 1.0f, 1.0f, 0.0f, -1.0f, 1.0f);

		shader = ShaderLoader.loadShader("<renderHelper/fullscreenBlit.vert>", "<renderHelper/fullscreenBlit.frag>");
		shader.activate();
		shader.setUniform1i("blitTexture", 0);
		shader.setUniformMat4f("projectionMatrix", projectionMatrix.asBuffer());
		shader.deactivate();
	}

}
