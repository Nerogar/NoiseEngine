package de.nerogar.noise.render.oldDeferredRenderer;

import de.nerogar.noise.render.IViewRegion;
import de.nerogar.noise.render.Shader;
import de.nerogar.noise.render.VertexBufferObjectInstanced;
import de.nerogar.noise.render.camera.IReadOnlyCamera;
import de.nerogar.noise.util.Bounding;
import de.nerogar.noise.util.Matrix4f;
import de.nerogar.noise.util.SpaceOctree;
import de.nerogar.noise.util.Vector3f;

import java.util.*;
import java.util.function.Consumer;

class VboContainerMany implements VboContainer {

	private static final int OCTREE_FILTER_THRESHOLD = 40;

	private DeferredRendererProfiler profiler;
	private DeferredContainer container;
	private List<DeferredRenderable> filteredRenderables;
	private SpaceOctree<DeferredRenderable> renderables;
	private Set<DeferredRenderable> updatedRenderables;
	private Consumer<DeferredRenderable> renderableListener;

	private VertexBufferObjectInstanced vbo;
	private Shader                      gBufferShader;

	private ArrayList<Matrix4f> instanceModelMatrices;
	private ArrayList<Matrix4f> instanceNormalMatrices;
	private float[]             modelMatrix1, modelMatrix2, modelMatrix3, modelMatrix4;
	private float[] normalMatrix1, normalMatrix2, normalMatrix3;

	private final int[] instanceComponentCounts = new int[] { 4, 4, 4, 4, 3, 3, 3 };

	public VboContainerMany(DeferredRendererProfiler profiler, DeferredContainer container, Shader gBufferShader) {
		this.profiler = profiler;
		this.container = container;
		this.gBufferShader = gBufferShader;

		renderables = new SpaceOctree<>(DeferredRenderable::getBoundingSphere, 64, 0.1f);

		filteredRenderables = new ArrayList<>();

		updatedRenderables = new HashSet<>();
		renderableListener = (renderable) -> updatedRenderables.add(renderable);

		instanceModelMatrices = new ArrayList<>();
		instanceNormalMatrices = new ArrayList<>();

		vbo = new VertexBufferObjectInstanced(
				new int[] { 3, 2, 3, 3, 3 },
				container.getMesh().getIndexCount(),
				container.getMesh().getVertexCount(),
				container.getMesh().getIndexArray(),
				container.getMesh().getPositionArray(),
				container.getMesh().getUVArray(),
				container.getMesh().getNormalArray(),
				container.getMesh().getTangentArray(),
				container.getMesh().getBitangentArray()
		);
	}

	@Override
	public void addObject(DeferredRenderable object) {
		renderables.add(object);
	}

	@Override
	public void removeObject(DeferredRenderable object) {
		renderables.remove(object);
	}

	@Override
	public boolean prepareRender(IViewRegion frustum) {

		instanceModelMatrices.clear();
		instanceNormalMatrices.clear();

		// call update on updated renderables
		if (!updatedRenderables.isEmpty()) {
			for (DeferredRenderable updatedRenderable : updatedRenderables) {
				updatedRenderable.update();
				renderables.update(updatedRenderable);
			}
		}

		updatedRenderables.clear();

		// filter renderables
		Bounding viewRegion = frustum.getBounding();
		Collection<DeferredRenderable> filteredRenderables;
		if (renderables.size() >= OCTREE_FILTER_THRESHOLD) {
			filteredRenderables = renderables.getFiltered(this.filteredRenderables, viewRegion);
		} else {
			filteredRenderables = renderables;
		}

		profiler.addValue(DeferredRendererProfiler.OBJECT_TEST_COUNT, filteredRenderables.size());

		Vector3f point = new Vector3f();
		for (DeferredRenderable renderable : filteredRenderables) {
			if (!renderable.getRenderProperties().isVisible()) continue;

			point.setX(renderable.getRenderProperties().getX());
			point.setY(renderable.getRenderProperties().getY());
			point.setZ(renderable.getRenderProperties().getZ());

			if (frustum.getPointDistance(point) < renderable.getContainer().getMesh().getBoundingRadius() * renderable.getRenderProperties().getMaxScaleComponent()) {
				instanceModelMatrices.add(renderable.getRenderProperties().getModelMatrix());
				instanceNormalMatrices.add(renderable.getRenderProperties().getNormalMatrix());
			}

		}

		// if arrays are too short, resize them
		if (modelMatrix1 == null || modelMatrix1.length < instanceModelMatrices.size() * 4) {
			modelMatrix1 = new float[instanceModelMatrices.size() * 4];
			modelMatrix2 = new float[instanceModelMatrices.size() * 4];
			modelMatrix3 = new float[instanceModelMatrices.size() * 4];
			modelMatrix4 = new float[instanceModelMatrices.size() * 4];

			normalMatrix1 = new float[instanceModelMatrices.size() * 3];
			normalMatrix2 = new float[instanceModelMatrices.size() * 3];
			normalMatrix3 = new float[instanceModelMatrices.size() * 3];
		}

		for (int i = 0; i < instanceModelMatrices.size(); i++) {
			Matrix4f modelMat = instanceModelMatrices.get(i);
			Matrix4f normalMat = instanceNormalMatrices.get(i);

			modelMatrix1[i * 4 + 0] = modelMat.get(0, 0);
			modelMatrix1[i * 4 + 1] = modelMat.get(1, 0);
			modelMatrix1[i * 4 + 2] = modelMat.get(2, 0);
			modelMatrix1[i * 4 + 3] = modelMat.get(3, 0);

			modelMatrix2[i * 4 + 0] = modelMat.get(0, 1);
			modelMatrix2[i * 4 + 1] = modelMat.get(1, 1);
			modelMatrix2[i * 4 + 2] = modelMat.get(2, 1);
			modelMatrix2[i * 4 + 3] = modelMat.get(3, 1);

			modelMatrix3[i * 4 + 0] = modelMat.get(0, 2);
			modelMatrix3[i * 4 + 1] = modelMat.get(1, 2);
			modelMatrix3[i * 4 + 2] = modelMat.get(2, 2);
			modelMatrix3[i * 4 + 3] = modelMat.get(3, 2);

			modelMatrix4[i * 4 + 0] = modelMat.get(0, 3);
			modelMatrix4[i * 4 + 1] = modelMat.get(1, 3);
			modelMatrix4[i * 4 + 2] = modelMat.get(2, 3);
			modelMatrix4[i * 4 + 3] = modelMat.get(3, 3);

			normalMatrix1[i * 3 + 0] = normalMat.get(0, 0);
			normalMatrix1[i * 3 + 1] = normalMat.get(1, 0);
			normalMatrix1[i * 3 + 2] = normalMat.get(2, 0);

			normalMatrix2[i * 3 + 0] = normalMat.get(0, 1);
			normalMatrix2[i * 3 + 1] = normalMat.get(1, 1);
			normalMatrix2[i * 3 + 2] = normalMat.get(2, 1);

			normalMatrix3[i * 3 + 0] = normalMat.get(0, 2);
			normalMatrix3[i * 3 + 1] = normalMat.get(1, 2);
			normalMatrix3[i * 3 + 2] = normalMat.get(2, 2);
		}

		// don't update instance data, if no instance will be drawn
		// outdated data doesn't matter in that case
		if (!instanceModelMatrices.isEmpty()) {
			vbo.setInstanceData(instanceModelMatrices.size(), instanceComponentCounts,
			                    modelMatrix1, modelMatrix2, modelMatrix3, modelMatrix4,
			                    normalMatrix1, normalMatrix2, normalMatrix3
			                   );
		}

		return !instanceModelMatrices.isEmpty();
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

		container.bindTextures();

		vbo.render();

		currentShader.deactivate();

		int instanceCount = instanceModelMatrices.size();
		profiler.addValue(DeferredRendererProfiler.OBJECT_RENDER_COUNT, instanceCount);
		profiler.addValue(DeferredRendererProfiler.TRIANGLE_RENDER_COUNT, instanceCount * container.getMesh().getTriangleCount());
	}

	@Override
	public Consumer<DeferredRenderable> getRenderableListener() {
		return renderableListener;
	}

	@Override
	public boolean isEmpty() {
		return renderables.isEmpty();
	}

	@Override
	public void cleanup() {
		vbo.cleanup();
	}

}
