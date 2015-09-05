package de.nerogar.noise.debug;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import de.nerogar.noise.Noise;
import de.nerogar.noise.render.*;
import de.nerogar.noise.util.*;

public class DebugWindow {

	public GLWindow window;

	private List<Profiler> profilerList;

	private Matrix4f projectionMatrix;
	private Shader shader;
	private VertexList vertexList;

	public DebugWindow(Profiler... profiler) {
		profilerList = new ArrayList<Profiler>();

		for (Profiler p : profiler) {
			profilerList.add(p);
		}

		if (!Noise.DEBUG) return;

		window = new GLWindow("debug", 500, 300, true, 0, null, null);

		projectionMatrix = Matrix4fUtils.getOrthographicProjection(0f, 1f, 1.1f, -0.1f, 1, -1);
		shader = ShaderLoader.loadShader("<debug/profiler.vert>", "<debug/profiler.frag>");
		vertexList = new VertexList();
	}

	public void update() {
		if (!Noise.DEBUG) return;
		if (window.isClosed()) return;
		if (window.shouldClose()) window.cleanup();

		Logger.log(Logger.DEBUG, Noise.getRessourceProfiler().toString());

		Profiler prof = profilerList.get(0);

		vertexList.clear();
		for (int id = 0; id < prof.getPropertyCount(); id++) {
			List<Integer> history = prof.getHistory(id);
			int maxHistory = prof.getMaxHistory(id);

			//vbo

			int[] vertices = new int[prof.getHistoryLength()];

			for (int i = 0; i < prof.getHistoryLength(); i++) {
				float x = (float) i / prof.getHistoryLength();
				float y = (float) history.get(i) / maxHistory;

				vertices[i] = vertexList.addVertex(x, y, 0f, 0f, 0f, 1.0f, 0.0f, 0.0f);
			}

			for (int i = 0; i < vertices.length - 1; i++) {
				vertexList.addIndex(vertices[i], vertices[i + 1]);
			}
		}
		window.bind();

		VertexBufferObjectIndexed vbo = new VertexBufferObjectIndexed(
				VertexBufferObject.LINES,
				new int[] { 3, 3 },
				vertexList.getIndexCount(),
				vertexList.getVertexCount(),
				vertexList.getIndexArray(),
				vertexList.getPositionArray(),
				vertexList.getNormalArray() //normals used as color
		);

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		shader.activate();
		shader.setUniformMat4f("projectionMatrix", projectionMatrix.asBuffer());
		vbo.render();
		shader.deactivate();

		vbo.cleanup();
	}
}
