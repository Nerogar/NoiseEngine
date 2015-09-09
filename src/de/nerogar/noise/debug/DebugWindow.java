package de.nerogar.noise.debug;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import de.nerogar.noise.Noise;
import de.nerogar.noise.render.*;
import de.nerogar.noise.render.InputHandler.KeyboardKeyEvent;
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

	private float scrollOffset;
	private float renderedScrollOffset;

	public DebugWindow(Profiler... profiler) {
		profilerList = new ArrayList<Profiler>();

		for (Profiler p : profiler) {
			addProfiler(p);
		}
		activeProfiler = 0;

		if (!Noise.DEBUG) return;

		window = new GLWindow("debug", 800, 400, true, 0, null, null);

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
			if (s != null) s.cleanup();
		}

		stringList.clear();

		for (ProfilerStatisticsCollection collection : profilerList.get(activeProfiler).getProfilerCollections()) {
			for (ProfilerStatistic statistic : collection.statisticList) {
				String s = statistic.name;

				stringList.add(new FontRenderableString(font, s, statistic.color, projectionMatrix, 1.0f, 1.0f));
			}

			stringList.add(null);
		}
	}

	public void addProfiler(Profiler profiler) {
		profilerList.add(profiler);
	}

	public void removeProfiler(Profiler profiler) {
		profilerList.remove(profiler);

		if (activeProfiler >= profilerList.size()) {
			activeProfiler = 0;
			createFont();
		}
	}

	public void update() {
		if (!Noise.DEBUG) return;
		if (window.isClosed()) return;
		if (window.shouldClose()) window.cleanup();

		long currentContext = GLWindow.getCurrentContext();
		window.bind();

		scrollOffset += window.getInputHandler().getScrollDeltaY() * 0.2f;
		scrollOffset = MathHelper.clamp(scrollOffset, 0, profilerList.get(activeProfiler).getProfilerCollections().size() - 1);

		renderedScrollOffset += (scrollOffset - renderedScrollOffset) * 0.1f;

		boolean activeProfilerChanged = false;
		for (KeyboardKeyEvent event : window.getInputHandler().getKeyboardKeyEvents()) {
			if (event.action == GLFW.GLFW_PRESS) {
				if (event.key == GLFW.GLFW_KEY_RIGHT) {
					activeProfiler++;
					activeProfilerChanged = true;
					event.setProcessed();
				} else if (event.key == GLFW.GLFW_KEY_LEFT) {
					activeProfiler--;
					activeProfilerChanged = true;
					event.setProcessed();
				}
			}
		}
		if (activeProfilerChanged) {
			activeProfiler = ((activeProfiler % profilerList.size()) + profilerList.size()) % profilerList.size();

			createFont();
		}
		Profiler profiler = profilerList.get(activeProfiler);

		//Logger.log(Logger.DEBUG, profiler);
		window.setTitle("Debug (Profiler: " + profiler.getName() + ")");

		vertexList.clear();

		float yOffset = -renderedScrollOffset;

		for (ProfilerStatisticsCollection collection : profiler.getProfilerCollections()) {
			for (ProfilerStatistic statistic : collection.statisticList) {

				//vbo

				int lastValue = statistic.getValue(0);
				int lastVertex = vertexList.addVertex(0f, (float) lastValue / collection.maxHistory + yOffset, 0f, 0f, 0f, statistic.color.getR(), statistic.color.getG(), statistic.color.getB());

				for (int i = 1; i < profiler.getHistorySize() - 1; i++) {

					int value = statistic.getValue(i);
					int nextValue = statistic.getValue(i + 1);

					if (value == lastValue && nextValue == lastValue) continue;

					float x = (float) i / profiler.getHistorySize();
					float y = (float) value / collection.maxHistory + yOffset;

					int vertex = vertexList.addVertex(x, y, 0f, 0f, 0f, statistic.color.getR(), statistic.color.getG(), statistic.color.getB());

					vertexList.addIndex(lastVertex, vertex);

					lastVertex = vertex;
					lastValue = value;
				}

				int vertex = vertexList.addVertex(1f, (float) statistic.getValue(profiler.getHistorySize() - 1) / collection.maxHistory + yOffset, 0f, 0f, 0f, statistic.color.getR(), statistic.color.getG(), statistic.color.getB());
				vertexList.addIndex(lastVertex, vertex);
			}

			//render border
			int v1 = vertexList.addVertex(0f, 0f + yOffset, 0f, 0f, 0f, 1.0f, 1.0f, 1.0f);
			int v2 = vertexList.addVertex(1f, 0f + yOffset, 0f, 0f, 0f, 1.0f, 1.0f, 1.0f);
			int v3 = vertexList.addVertex(1f, 1f + yOffset, 0f, 0f, 0f, 1.0f, 1.0f, 1.0f);
			int v4 = vertexList.addVertex(0f, 1f + yOffset, 0f, 0f, 0f, 1.0f, 1.0f, 1.0f);

			vertexList.addIndex(v1, v2);
			vertexList.addIndex(v2, v3);
			vertexList.addIndex(v3, v4);
			vertexList.addIndex(v4, v1);

			yOffset += 1.0f;
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
		shader.setUniform2f("cornerBig", window.getWidth() - SIDEBAR_WIDTH, 300 - 10);
		vbo.render();
		shader.deactivate();
		vbo.cleanup();

		int y = 10;

		for (FontRenderableString s : stringList) {
			if (s != null) s.render(window.getWidth() - SIDEBAR_WIDTH, y);
			y += 20;
		}

		GLWindow.makeContextCurrent(currentContext);
	}

}
