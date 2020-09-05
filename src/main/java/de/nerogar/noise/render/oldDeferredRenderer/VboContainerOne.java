package de.nerogar.noise.render.oldDeferredRenderer;

import de.nerogar.noise.math.Matrix4f;
import de.nerogar.noise.math.Vector3f;
import de.nerogar.noise.render.IViewRegion;
import de.nerogar.noise.render.Shader;
import de.nerogar.noise.render.VertexBufferObjectIndexed;
import de.nerogar.noise.render.camera.IReadOnlyCamera;
import de.nerogar.noiseInterface.math.IMatrix4f;
import de.nerogar.noiseInterface.math.IVector3f;

import java.util.function.Consumer;

class VboContainerOne implements VboContainer {

	private DeferredRendererProfiler     profiler;
	private DeferredContainer            container;
	private DeferredRenderable           renderable;
	private boolean                      updatedRenderable;
	private Consumer<DeferredRenderable> renderableListener;

	private VertexBufferObjectIndexed vbo;
	private Shader                    gBufferShader;

	private IMatrix4f modelMatrix;
	private IMatrix4f normalMatrix;

	private IVector3f point;

	public VboContainerOne(DeferredRendererProfiler profiler, DeferredContainer container, Shader gBufferShader) {
		this.profiler = profiler;
		this.container = container;
		this.gBufferShader = gBufferShader;

		renderableListener = (renderable) -> updatedRenderable = true;

		modelMatrix = new Matrix4f();
		normalMatrix = new Matrix4f();

		point = new Vector3f();

		int[] componentCounts = new int[5 + container.getMesh().getAdditionalAttributes().length];
		float[][] attributes = new float[5 + container.getMesh().getAdditionalAttributes().length][];

		componentCounts[0] = 3;
		componentCounts[1] = 2;
		componentCounts[2] = 3;
		componentCounts[3] = 3;
		componentCounts[4] = 3;

		attributes[0] = container.getMesh().getPositionArray();
		attributes[1] = container.getMesh().getUVArray();
		attributes[2] = container.getMesh().getNormalArray();
		attributes[3] = container.getMesh().getTangentArray();
		attributes[4] = container.getMesh().getBitangentArray();

		for (int i = 0; i < container.getMesh().getAdditionalAttributes().length; i++) {
			componentCounts[5 + i] = container.getMesh().getAdditionalAttributeComponents()[i];
			attributes[5 + i] = container.getMesh().getAdditionalAttributes()[i];
		}

		vbo = new VertexBufferObjectIndexed(
				componentCounts,
				container.getMesh().getIndexCount(),
				container.getMesh().getVertexCount(),
				container.getMesh().getIndexArray(),
				attributes
		);
	}

	@Override
	public void addObject(DeferredRenderable object) {
		renderable = object;
	}

	@Override
	public void removeObject(DeferredRenderable object) {
		renderable = null;
	}

	@Override
	public boolean prepareRender(IViewRegion frustum) {

		if (renderable == null) return false;
		if (!renderable.getRenderProperties().isVisible()) return false;

		// call update on updated renderables
		if (updatedRenderable) {
			renderable.update();
		}

		profiler.incrementValue(DeferredRendererProfiler.OBJECT_TEST_COUNT);

		point.setX(renderable.getRenderProperties().getX());
		point.setY(renderable.getRenderProperties().getY());
		point.setZ(renderable.getRenderProperties().getZ());

		if (frustum.getPointDistance(point) < renderable.getContainer().getMesh().getBoundingRadius() * renderable.getRenderProperties().getMaxScaleComponent()) {
			modelMatrix = renderable.getRenderProperties().getModelMatrix();
			normalMatrix = renderable.getRenderProperties().getNormalMatrix();
		} else {
			return false;
		}

		return true;
	}

	@Override
	public void render(IReadOnlyCamera camera) {
		Shader currentShader;

		if (container.getSurfaceShader() == null) {
			currentShader = gBufferShader;
		} else {
			currentShader = container.getSurfaceShader();
		}
		currentShader.activate();

		if (currentShader != gBufferShader) {
			currentShader.setUniformMat4f("viewMatrix_N", camera.getViewMatrix().asBuffer());
			currentShader.setUniformMat4f("projectionMatrix_N", camera.getProjectionMatrix().asBuffer());
		}

		currentShader.setUniformMat4f("modelMatrix_N", modelMatrix.asBuffer());
		currentShader.setUniformMat4f("normalMatrix_N", normalMatrix.asBuffer());

		container.bindTextures();

		vbo.render();

		currentShader.deactivate();

		profiler.incrementValue(DeferredRendererProfiler.OBJECT_RENDER_COUNT);
		profiler.addValue(DeferredRendererProfiler.TRIANGLE_RENDER_COUNT, container.getMesh().getTriangleCount());
	}

	@Override
	public Consumer<DeferredRenderable> getRenderableListener() {
		return renderableListener;
	}

	@Override
	public boolean isEmpty() {
		return renderable == null;
	}

	@Override
	public void cleanup() {
		vbo.cleanup();
	}

}
