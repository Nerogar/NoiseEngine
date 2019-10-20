package de.nerogar.noise.game.client.systems;

import de.nerogar.noise.event.EventListener;
import de.nerogar.noise.game.CoreMap;
import de.nerogar.noise.game.LogicSystem;
import de.nerogar.noise.game.client.event.RenderEvent;
import de.nerogar.noise.game.client.event.WindowSizeChangeEvent;
import de.nerogar.noise.render.camera.PerspectiveCamera;
import de.nerogar.noise.render.deferredRenderer.DeferredRenderer;
import de.nerogar.noise.game.client.event.ActiveMapChangeEvent;

public class RenderSystem extends LogicSystem {

	private CoreMap           map;
	private DeferredRenderer  renderer;
	private PerspectiveCamera camera;
	private boolean           active;

	private EventListener<WindowSizeChangeEvent> windowSizeChangeListener;
	private EventListener<RenderEvent>           renderEventListener;
	private EventListener<ActiveMapChangeEvent>  activeMapChangeListener;

	public RenderSystem(CoreMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		windowSizeChangeListener = this::onWindowSizeChange;
		getEventManager().register(WindowSizeChangeEvent.class, windowSizeChangeListener);

		renderEventListener = this::onRender;
		getEventManager().registerImmediate(RenderEvent.class, renderEventListener);

		activeMapChangeListener = this::onActiveMapChange;
		getEventManager().registerImmediate(ActiveMapChangeEvent.class, activeMapChangeListener);

		renderer = new DeferredRenderer(100, 100);
		renderer.setSunLightBrightness(1.0f);

		camera = new PerspectiveCamera(90, 1, 0.1f, 1000f);
	}

	public DeferredRenderer getRenderer() { return renderer; }

	public PerspectiveCamera getCamera()  { return camera; }

	public boolean isActive()             { return active; }

	private void onWindowSizeChange(WindowSizeChangeEvent event) {
		renderer.setFrameBufferResolution(event.getWidth(), event.getHeight());
		camera.setAspect(event.getAspect());
	}

	private void onRender(RenderEvent event) {
		if (active) {
			renderer.render(camera);
		}
	}

	private void onActiveMapChange(ActiveMapChangeEvent event) {
		active = event.getNewMap() == map;
	}

	@Override
	public void cleanup() {
		getEventManager().unregister(WindowSizeChangeEvent.class, windowSizeChangeListener);
		getEventManager().unregister(RenderEvent.class, renderEventListener);

		renderer.cleanup();
	}

}
