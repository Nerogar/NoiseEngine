package de.nerogar.noise.debug;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import de.nerogar.noise.Noise;
import de.nerogar.noise.render.*;
import de.nerogar.noise.render.fontRenderer.Font;
import de.nerogar.noise.render.fontRenderer.FontRenderableString;
import de.nerogar.noise.util.*;

public class DebugWindow {

	private static final int SIDEBAR_WIDTH = 150;

	public GLWindow window;

	private List<Profiler> profilerList;
	private int activeProfiler;

	private Matrix4f projectionMatrix;
	private Shader shader;
	private VertexList vertexList;

	private Font font;

	private List<FontRenderableString> stringList;

	public DebugWindow(Profiler... profiler) {
		profilerList = new ArrayList<Profiler>();

		for (Profiler p : profiler) {
			addProfiler(p);
		}
		activeProfiler = 0;

		if (!Noise.DEBUG) return;

		window = new GLWindow("debug", 800, 300, true, 0, null, null);

		shader = ShaderLoader.loadShader("<debug/profiler.vert>", "<debug/profiler.frag>");
		vertexList = new VertexList();

		projectionMatrix = new Matrix4f();
		setProjectionMatrix(window.getWidth(), window.getHeight());

		window.setSizeChangeListener(new GLWindowSizeChangeListener() {
			@Override
			public void onChange(int width, int height) {
				setProjectionMatrix(width, height);
			}
		});

		font = new Font("calibri", 14);
		stringList = new ArrayList<FontRenderableString>();
		createFont();
	}

	private void setProjectionMatrix(int width, int height) {
		Matrix4fUtils.setOrthographicProjection(projectionMatrix, 0f, width, height, 0, 1, -1);
	}

	private void createFont() {
		for (FontRenderableString s : stringList) {
			s.cleanup();
		}

		stringList.clear();

		for (int id : profilerList.get(activeProfiler).getPropertyList()) {
			String s = profilerList.get(activeProfiler).getName(id);

			stringList.add(new FontRenderableString(font, s, profilerList.get(activeProfiler).getColor(id), projectionMatrix, 1.0f, 1.0f));
		}
	}

	public void addProfiler(Profiler profiler) {
		profilerList.add(profiler);
	}

	public void removeProfiler(Profiler profiler) {
		profilerList.remove(profiler);

		if (activeProfiler >= profilerList.size()) activeProfiler = 0;
	}

	public void update() {
		if (!Noise.DEBUG) return;
		if (window.isClosed()) return;
		if (window.shouldClose()) window.cleanup();

		long currentContext = GLWindow.getCurrentContext();
		window.bind();
		
		int scrollDelta = (int) window.getInputHandler().getScrollDeltaY();
		if (scrollDelta != 0) {
			activeProfiler += scrollDelta;
			activeProfiler = ((activeProfiler % profilerList.size()) + profilerList.size()) % profilerList.size();

			createFont();
		}
		Profiler profiler = profilerList.get(activeProfiler);

		//Logger.log(Logger.DEBUG, profiler);
		window.setTitle("Debug (Profiler: " + profiler.getName() + ")");

		vertexList.clear();
		for (int id : profiler.getPropertyList()) {
			List<Integer> history = profiler.getHistory(id);
			int maxHistory = profiler.getMaxHistory(id);
			Color color = profiler.getColor(id);

			//vbo

			int lastValue = history.get(0);
			int lastVertex = vertexList.addVertex(0f, (float) lastValue / maxHistory, 0f, 0f, 0f, color.getR(), color.getG(), color.getB());

			for (int i = 1; i < profiler.getHistoryLength() - 1; i++) {

				int value = history.get(i);
				int nextValue = history.get(i + 1);

				if (value == lastValue && nextValue == lastValue) continue;

				float x = (float) i / profiler.getHistoryLength();
				float y = (float) value / maxHistory;
				int vertex = vertexList.addVertex(x, y, 0f, 0f, 0f, color.getR(), color.getG(), color.getB());

				vertexList.addIndex(lastVertex, vertex);

				lastVertex = vertex;
				lastValue = value;
			}

			int vertex = vertexList.addVertex(1f, (float) history.get(history.size() - 1) / maxHistory, 0f, 0f, 0f, color.getR(), color.getG(), color.getB());
			vertexList.addIndex(lastVertex, vertex);

		}

		

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
		shader.setUniform2f("cornerSmall", 0, 10);
		shader.setUniform2f("cornerBig", window.getWidth() - SIDEBAR_WIDTH, window.getHeight() - 10);
		vbo.render();
		shader.deactivate();
		vbo.cleanup();

		int y = 10;

		for (FontRenderableString s : stringList) {
			s.render(window.getWidth() - SIDEBAR_WIDTH, y);
			y += 20;
		}

		GLWindow.makeContextCurrent(currentContext);
	}

}
