package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.exception.NotImplementedException;
import de.nerogar.noise.math.Vector3f;
import de.nerogar.noise.util.Color;
import de.nerogar.noiseInterface.math.IReadOnlyTransformation;
import de.nerogar.noiseInterface.math.IReadonlyVector3f;
import de.nerogar.noiseInterface.render.deferredRenderer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DebugRenderableContainer implements IRenderableContainer {

	private List<IRenderableGeometry>  geometry;
	private List<ILight>               lights;
	private List<IRenderableContainer> containers;

	public DebugRenderableContainer() {
		geometry = new ArrayList<>();
		lights = new ArrayList<>();
		containers = new ArrayList<>();
	}

	public void clear() {
		geometry.clear();
		lights.clear();
		containers.clear();
	}

	public void addLineStrip(IReadonlyVector3f[] points, Color color) {
		DebugLinesRenderable linesRenderable = new DebugLinesRenderable();
		int lines = points.length - 1;
		for (int i = 0; i < lines; i++) {
			linesRenderable.addLine(points[i], points[i + 1], color);
		}
		geometry.add(linesRenderable);
	}

	public void addLines(IReadonlyVector3f[] points, Color color) {
		DebugLinesRenderable linesRenderable = new DebugLinesRenderable();
		int lines = points.length / 2;
		for (int i = 0; i < lines; i++) {
			linesRenderable.addLine(points[i * 2], points[i * 2 + 1], color);
		}
		geometry.add(linesRenderable);
	}

	// TODO
	public void addPoints() {
		throw new NotImplementedException();
	}

	@Override
	public void getGeometry(IRenderContext renderContext, Consumer<IRenderableGeometry> adder) {
		for (IRenderableGeometry g : geometry) {
			adder.accept(g);
		}

		for (IRenderableContainer c : containers) {
			c.getGeometry(renderContext, adder);
		}
	}

	@Override
	public void getLights(IRenderContext renderContext, Consumer<ILight> adder) {
		for (ILight l : lights) {
			adder.accept(l);
		}

		for (IRenderableContainer c : containers) {
			c.getLights(renderContext, adder);
		}
	}

	@Override
	public void setTransformation(IReadOnlyTransformation transformation) {
		for (IRenderableGeometry g : geometry) {
			g.setTransformation(transformation);
		}

		for (ILight l : lights) {
			l.setTransformation(transformation);
		}

		for (IRenderableContainer c : containers) {
			c.setTransformation(transformation);
		}
	}
}
