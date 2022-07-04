package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noiseInterface.math.IReadOnlyTransformation;
import de.nerogar.noiseInterface.render.deferredRenderer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SimpleRenderableContainer implements IRenderableContainer {

	private List<IRenderableGeometry>  geometry;
	private List<ILight>               lights;
	private List<IRenderableContainer> containers;

	public SimpleRenderableContainer() {
		geometry = new ArrayList<>();
		lights = new ArrayList<>();
		containers = new ArrayList<>();
	}

	public void addGeometry(IRenderableGeometry geometry) {
		this.geometry.add(geometry);
	}

	public void removeGeometry(IRenderableGeometry geometry) {
		this.geometry.remove(geometry);
	}

	public void addTransparentGeometry(IRenderableGeometry transparentGeometry) {
		this.geometry.add(transparentGeometry);
	}

	public void removeTransparentGeometry(IRenderableGeometry transparentGeometry) {
		this.geometry.remove(transparentGeometry);
	}

	public void addLight(ILight light) {
		lights.add(light);
	}

	public void removeLight(ILight light) {
		lights.remove(light);
	}

	public void addContainer(IRenderableContainer container) {
		containers.add(container);
	}

	public void removeContainer(IRenderableContainer container) {
		containers.add(container);
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
