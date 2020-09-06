package de.nerogar.noise.render.oldDeferredRenderer;

import de.nerogar.noise.math.BoundingSphere;
import de.nerogar.noise.math.Vector3f;
import de.nerogar.noise.math.Transformation;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * an object that can be added to the {@link DeferredRenderable DeferredRenderer}
 */
public class DeferredRenderable {

	private DeferredContainer container;
	private Transformation    renderProperties;
	private BoundingSphere    boundingSphere;

	private   Transformation.RenderPropertiesListener renderPropertiesListener;
	protected Set<Consumer<DeferredRenderable>>       listeners;

	/**
	 * @param container        the {@link DeferredContainer DeferredContainer} describing the appearance
	 * @param renderProperties the {@link Transformation RenderProperties3f} describing properties like position and rotation
	 */
	public DeferredRenderable(DeferredContainer container, Transformation renderProperties) {
		this.container = container;
		this.renderProperties = renderProperties;

		this.boundingSphere = new BoundingSphere(new Vector3f(), 0);
		update();
	}

	/**
	 * update the bounding sphere with new values from the render properties
	 */
	/*package private*/ void update() {
		boundingSphere.setRadius(container.getMesh().getBoundingRadius() * renderProperties.getMaxScaleComponent());
		boundingSphere.setCenter(renderProperties.getX(), renderProperties.getY(), renderProperties.getZ());
	}

	/**
	 * @return the {@link DeferredContainer DeferredContainer}
	 */
	public DeferredContainer getContainer() {
		return container;
	}

	/**
	 * @return the {@link Transformation RenderProperties3f}
	 */
	public Transformation getRenderProperties() {
		return renderProperties;
	}

	/**
	 * @return the {@link BoundingSphere BoundingSphere}
	 */
	public BoundingSphere getBoundingSphere() {
		return boundingSphere;
	}

	public boolean addListener(Consumer<DeferredRenderable> listener) {
		if (listeners == null) {
			renderPropertiesListener = (rp, p, r, s) -> {if (p || s) updateListener(); };
			renderProperties.setListener(renderPropertiesListener);
			listeners = new HashSet<>();
		}

		if (!listeners.contains(listener)) {
			listeners.add(listener);
			return true;
		} else {
			return false;
		}

	}

	public boolean removeListener(Consumer<DeferredRenderable> listener) {
		if (listeners != null && listeners.contains(listener)) {
			listeners.remove(listener);
			return true;
		} else {
			return false;
		}

	}

	private void updateListener() {
		if (listeners != null) {
			for (Consumer<DeferredRenderable> listener : listeners) {
				listener.accept(this);
			}
		}
	}

}
