package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.render.Camera;
import de.nerogar.noise.render.IViewRegion;

import java.util.function.Consumer;

interface VboContainer {

	boolean prepareRender(IViewRegion frustum);

	void render(Camera camera);

	void addObject(DeferredRenderable object);

	void removeObject(DeferredRenderable object);

	Consumer<DeferredRenderable> getRenderableListener();

	void cleanup();

	boolean isEmpty();
}
