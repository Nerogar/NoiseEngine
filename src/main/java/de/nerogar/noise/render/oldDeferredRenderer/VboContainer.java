package de.nerogar.noise.render.oldDeferredRenderer;

import de.nerogar.noise.render.IViewRegion;
import de.nerogar.noise.render.camera.IReadOnlyCamera;

import java.util.function.Consumer;

interface VboContainer {

	boolean prepareRender(IViewRegion frustum);

	void render(IReadOnlyCamera camera);

	void addObject(DeferredRenderable object);

	void removeObject(DeferredRenderable object);

	Consumer<DeferredRenderable> getRenderableListener();

	void cleanup();

	boolean isEmpty();
}
