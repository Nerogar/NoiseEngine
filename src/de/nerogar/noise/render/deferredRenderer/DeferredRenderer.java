package de.nerogar.noise.render.deferredRenderer;

import static org.lwjgl.opengl.GL11.*;

import java.util.*;

import de.nerogar.noise.Noise;
import de.nerogar.noise.render.*;
import de.nerogar.noise.util.*;

public class DeferredRenderer {

	private static class VboContainer {
		public DeferredContainer container;
		public List<DeferredRenderable> renderables;
		public VertexBufferObjectInstanced vbo;

		private ArrayList<Matrix4f> instanceModelMatrices;
		private ArrayList<Matrix4f> instanceNormalMatrices;
		private float[] modelMatrix1, modelMatrix2, modelMatrix3, modelMatrix4;
		private float[] normalMatrix1, normalMatrix2, normalMatrix3;

		private static final int[] instanceComponentCounts = new int[] { 4, 4, 4, 4, 3, 3, 3 };

		public VboContainer(DeferredContainer container) {
			this.container = container;
			renderables = new ArrayList<DeferredRenderable>();

			instanceModelMatrices = new ArrayList<Matrix4f>();
			instanceNormalMatrices = new ArrayList<Matrix4f>();

			vbo = new VertexBufferObjectInstanced(new int[] { 3, 2, 3, 3, 3 },
					container.getMesh().getIndexCount(),
					container.getMesh().getVertexCount(),
					container.getMesh().getIndexArray(),
					container.getMesh().getPositionArray(),
					container.getMesh().getUVArray(),
					container.getMesh().getNormalArray(),
					container.getMesh().getTangentArray(),
					container.getMesh().getBitangentArray());
		}

		public void rebuildInstanceData(ViewFrustum frustum) {

			instanceModelMatrices.clear();
			instanceNormalMatrices.clear();

			Vector3f point = new Vector3f();

			for (int i = 0; i < renderables.size(); i++) {
				if (!renderables.get(i).getRenderProperties().isVisible()) continue;

				point.setX(renderables.get(i).getRenderProperties().getX());
				point.setY(renderables.get(i).getRenderProperties().getY());
				point.setZ(renderables.get(i).getRenderProperties().getZ());

				if (frustum.getPointDistance(point) < renderables.get(i).getContainer().getMesh().getBoundingRadius() * renderables.get(i).getRenderProperties().getMaxScaleComponent()) {
					instanceModelMatrices.add(renderables.get(i).getRenderProperties().getModelMatrix());
					instanceNormalMatrices.add(renderables.get(i).getRenderProperties().getNormalMatrix());
				}
			}

			//if arrays are too short, resize them
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
				modelMatrix1[i * 4 + 1] = modelMat.get(0, 1);
				modelMatrix1[i * 4 + 2] = modelMat.get(0, 2);
				modelMatrix1[i * 4 + 3] = modelMat.get(0, 3);

				modelMatrix2[i * 4 + 0] = modelMat.get(1, 0);
				modelMatrix2[i * 4 + 1] = modelMat.get(1, 1);
				modelMatrix2[i * 4 + 2] = modelMat.get(1, 2);
				modelMatrix2[i * 4 + 3] = modelMat.get(1, 3);

				modelMatrix3[i * 4 + 0] = modelMat.get(2, 0);
				modelMatrix3[i * 4 + 1] = modelMat.get(2, 1);
				modelMatrix3[i * 4 + 2] = modelMat.get(2, 2);
				modelMatrix3[i * 4 + 3] = modelMat.get(2, 3);

				modelMatrix4[i * 4 + 0] = modelMat.get(3, 0);
				modelMatrix4[i * 4 + 1] = modelMat.get(3, 1);
				modelMatrix4[i * 4 + 2] = modelMat.get(3, 2);
				modelMatrix4[i * 4 + 3] = modelMat.get(3, 3);

				normalMatrix1[i * 3 + 0] = normalMat.get(0, 0);
				normalMatrix1[i * 3 + 1] = normalMat.get(0, 1);
				normalMatrix1[i * 3 + 2] = normalMat.get(0, 2);

				normalMatrix2[i * 3 + 0] = normalMat.get(1, 0);
				normalMatrix2[i * 3 + 1] = normalMat.get(1, 1);
				normalMatrix2[i * 3 + 2] = normalMat.get(1, 2);

				normalMatrix3[i * 3 + 0] = normalMat.get(2, 0);
				normalMatrix3[i * 3 + 1] = normalMat.get(2, 1);
				normalMatrix3[i * 3 + 2] = normalMat.get(2, 2);
			}

			vbo.setInstanceData(instanceModelMatrices.size(), instanceComponentCounts,
					modelMatrix1, modelMatrix2, modelMatrix3, modelMatrix4,
					normalMatrix1, normalMatrix2, normalMatrix3);
		}
	}

	private Map<DeferredContainer, VboContainer> vboMap;
	private VertexBufferObjectIndexed fullscreenQuad;

	//gBuffer
	private Shader gBufferShader;
	private FrameBufferObject gBuffer;

	//lights
	private LightContainer lightContainer;
	private VertexBufferObjectInstanced lightVbo;
	private Shader lightShader;
	private FrameBufferObject lightFrameBuffer;

	//effects
	private EffectContainer effectContainer;
	private FrameBufferObject effectFrameBuffer;

	//final pass
	private Shader finalShader;
	private FrameBufferObject finalFrameBuffer;

	//filter
	private Shader filterShader;
	private FrameBufferObject filterFrameBuffer;

	//settings
	private TextureCubeMap reflectionTexture;
	private Color sunLightColor;
	private Vector3f sunLightDirectionInternal;
	private Vector3f sunLightDirection;
	private float sunLightBrightness;
	private float minAmbientBrightness;

	public DeferredRenderer(int width, int height) {

		sunLightColor = new Color(1.0f, 1.0f, 0.9f, 0.0f);
		sunLightDirectionInternal = new Vector3f(-1.0f);
		sunLightBrightness = 1.5f;
		recalcSunLight();
		minAmbientBrightness = 0.3f;

		vboMap = new HashMap<DeferredContainer, VboContainer>();
		lightContainer = new LightContainer();
		effectContainer = new EffectContainer();
		Mesh sphere = WavefrontLoader.loadObject(Noise.RESSOURCE_DIR + "meshes/icoSphere.obj");
		lightVbo = new VertexBufferObjectInstanced(new int[] { 3 }, sphere.getIndexCount(), sphere.getVertexCount(), sphere.getIndexArray(), sphere.getPositionArray());

		gBuffer = new FrameBufferObject(width, height, true,
				Texture2D.DataType.BGRA_8_8_8_8I, //color
				Texture2D.DataType.BGRA_16_16_16F, //normal
				Texture2D.DataType.BGRA_32_32_32F, //position
				Texture2D.DataType.BGRA_8_8_8_8I //light
		);

		lightFrameBuffer = new FrameBufferObject(width, height, false, Texture2D.DataType.BGRA_16_16_16F);

		effectFrameBuffer = new FrameBufferObject(width, height, false, Texture2D.DataType.BGRA_8_8_8_8I);
		effectFrameBuffer.attachTexture(-1, gBuffer.getTextureAttachment(-1));

		finalFrameBuffer = new FrameBufferObject(width, height, false, Texture2D.DataType.BGRA_8_8_8_8I);

		filterFrameBuffer = new FrameBufferObject(width, height, false, Texture2D.DataType.BGRA_8_8_8_8I);

		fullscreenQuad = new VertexBufferObjectIndexed(
				new int[] { 2, 2 },
				6,
				4,
				new int[] { 0, 1, 2, 2, 3, 0 },
				new float[] { 0.0f, 0.0f,/**/0.0f, 1.0f, /**/1.0f, 1.0f,/**/1.0f, 0.0f },
				new float[] { 0.0f, 0.0f,/**/0.0f, 1.0f, /**/1.0f, 1.0f,/**/1.0f, 0.0f }
				);

		loadShaders();
		setFrameBufferResolution(width, height);
	}

	public void addObject(DeferredRenderable object) {
		VboContainer container = vboMap.get(object.getContainer());

		if (container == null) {
			container = new VboContainer(object.getContainer());
		}

		container.renderables.add(object);

		vboMap.put(object.getContainer(), container);
	}

	public void addAllObjects(Collection<DeferredRenderable> objects) {
		for (DeferredRenderable object : objects) {
			addObject(object);
		}
	}

	public void removeObject(DeferredRenderable object) {
		VboContainer container = vboMap.get(object.getContainer());
		container.renderables.remove(object);

		if (container.renderables.size() == 0) {
			container.vbo.cleanup();
			vboMap.remove(object.getContainer());
		}
	}

	public void removeAllObjects(Collection<DeferredRenderable> objects) {
		for (DeferredRenderable object : objects) {
			removeObject(object);
		}
	}

	public void clear() {
		for (VboContainer container : vboMap.values()) {
			container.vbo.cleanup();
		}

		vboMap.clear();
	}

	public LightContainer getLightContainer() {
		return lightContainer;
	}

	public EffectContainer getEffectContainer() {
		return effectContainer;
	}

	private static final int[] lightInstanceComponents = new int[] { 3, 3, 1, 1 };

	private void rebuildLightVbo(PerspectiveCamera camera) {
		float[] position = new float[lightContainer.size() * 3];
		float[] color = new float[lightContainer.size() * 3];
		float[] reach = new float[lightContainer.size()];
		float[] intensity = new float[lightContainer.size()];

		int i = 0;
		for (Light light : lightContainer) {
			if (camera.getViewFrustum().getPointDistance(light.position) < light.reach) {
				position[i * 3 + 0] = light.position.getX();
				position[i * 3 + 1] = light.position.getY();
				position[i * 3 + 2] = light.position.getZ();

				color[i * 3 + 0] = light.color.getR();
				color[i * 3 + 1] = light.color.getG();
				color[i * 3 + 2] = light.color.getB();

				reach[i] = light.reach;

				intensity[i] = light.intensity;

				i++;
			}
		}

		lightVbo.setInstanceData(i, lightInstanceComponents, position, color, reach, intensity);
	}

	//TODO: remove
	public void loadShaders() {
		gBufferShader = ShaderLoader.loadShader("<deferredRenderer/gBuffer.vert>", "<deferredRenderer/gBuffer.frag>");
		gBufferShader.activate();
		gBufferShader.setUniform1i("textureColor_N", 0);
		gBufferShader.setUniform1i("textureNormal_N", 1);
		gBufferShader.setUniform1i("textureLight_N", 2);
		gBufferShader.deactivate();

		lightShader = ShaderLoader.loadShader("<deferredRenderer/lights.vert>", "<deferredRenderer/lights.frag>");
		lightShader.activate();
		lightShader.setUniform1i("textureNormal", 1);
		lightShader.setUniform1i("texturePosition", 2);
		lightShader.deactivate();

		finalShader = ShaderLoader.loadShader("<deferredRenderer/final.vert>", "<deferredRenderer/final.frag>");
		finalShader.activate();
		finalShader.setUniform1i("textureColor", 0);
		finalShader.setUniform1i("textureNormal", 1);
		finalShader.setUniform1i("texturePosition", 2);
		finalShader.setUniform1i("textureLight", 3);
		finalShader.setUniform1i("textureLights", 4);
		finalShader.setUniform1i("textureEffects", 5);
		finalShader.setUniform1i("textureReflection", 6);
		finalShader.setUniformMat4f("projectionMatrix", Matrix4fUtils.getOrthographicProjection(0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f).asBuffer());
		finalShader.deactivate();

		filterShader = ShaderLoader.loadShader("<deferredRenderer/filter.vert>", "<deferredRenderer/filter.frag>");
		filterShader.activate();
		filterShader.setUniform1i("textureColor", 0);
		filterShader.setUniformMat4f("projectionMatrix", Matrix4fUtils.getOrthographicProjection(0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f).asBuffer());
		filterShader.deactivate();
	}

	public void setFrameBufferResolution(int width, int height) {
		gBuffer.setResolution(width, height);
		lightFrameBuffer.setResolution(width, height);
		effectFrameBuffer.setResolution(width, height);
		finalFrameBuffer.setResolution(width, height);
		filterFrameBuffer.setResolution(width, height);

		lightShader.activate();
		lightShader.setUniform2f("inverseResolution", 1.0f / width, 1.0f / height);
		lightShader.deactivate();

		finalShader.activate();
		finalShader.setUniform2f("inverseResolution", 1.0f / width, 1.0f / height);
		finalShader.deactivate();

		filterShader.activate();
		filterShader.setUniform2f("inverseResolution", 1.0f / width, 1.0f / height);
		filterShader.deactivate();
	}

	public void setReflectionTexture(TextureCubeMap reflectionTexture) {
		this.reflectionTexture = reflectionTexture;
	}

	public void setSunLightColor(Color sunLightColor) {
		this.sunLightColor = sunLightColor;
	}

	public void setSunLightDirection(Vector3f sunLightDirection) {
		this.sunLightDirectionInternal = sunLightDirection;
		recalcSunLight();
	}

	public void setSunLightBrightness(float sunLightBrightness) {
		this.sunLightBrightness = sunLightBrightness;
		recalcSunLight();
	}

	public void setMinAmbientBrightness(float minAmbientBrightness) {
		this.minAmbientBrightness = minAmbientBrightness;
	}

	private void recalcSunLight() {
		sunLightDirection = sunLightDirectionInternal.clone();
		sunLightDirection.setValue(sunLightBrightness);
	}

	public void render(PerspectiveCamera camera) {
		long time1 = System.nanoTime();

		//render gBuffer
		gBuffer.bind();
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);

		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);

		gBufferShader.activate();
		gBufferShader.setUniformMat4f("viewMatrix_N", camera.getViewMatrix().asBuffer());
		gBufferShader.setUniformMat4f("projectionMatrix_N", camera.getProjectionMatrix().asBuffer());
		gBufferShader.deactivate();

		Shader currentShader;

		for (VboContainer container : vboMap.values()) {
			container.rebuildInstanceData(camera.getViewFrustum());

			if (container.container.getSurfaceShader() == null) {
				currentShader = gBufferShader;
			} else {
				currentShader = container.container.getSurfaceShader();
			}
			currentShader.activate();

			if (currentShader != gBufferShader) {
				currentShader.setUniformMat4f("viewMatrix_N", camera.getViewMatrix().asBuffer());
				currentShader.setUniformMat4f("projectionMatrix_N", camera.getProjectionMatrix().asBuffer());
			}

			container.container.bindTextures();

			container.vbo.render();

			currentShader.deactivate();
		}

		glDisable(GL_DEPTH_TEST);

		//bind gBuffer textures
		gBuffer.getTextureAttachment(0).bind(0);
		gBuffer.getTextureAttachment(1).bind(1);
		gBuffer.getTextureAttachment(2).bind(2);
		gBuffer.getTextureAttachment(3).bind(3);

		//render lights
		lightFrameBuffer.bind();
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		rebuildLightVbo(camera);

		glCullFace(GL_FRONT);

		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE);

		lightShader.activate();
		lightShader.setUniformMat4f("viewMatrix", camera.getViewMatrix().asBuffer());
		lightShader.setUniformMat4f("projectionMatrix", camera.getProjectionMatrix().asBuffer());

		lightVbo.render();
		lightShader.deactivate();

		glDisable(GL_CULL_FACE);

		lightFrameBuffer.getTextureAttachment(0).bind(4);
		if (reflectionTexture != null) reflectionTexture.bind(6);

		//render effects
		effectFrameBuffer.bind();
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT);

		glBlendFunc(GL_ONE, GL_ONE);

		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glDepthMask(false);

		Vector3f point = new Vector3f();
		for (Effect effect : effectContainer) {
			if (!effect.getRenderProperties().isVisible()) continue;

			point.setX(effect.getRenderProperties().getX());
			point.setY(effect.getRenderProperties().getY());
			point.setZ(effect.getRenderProperties().getZ());

			if (camera.getViewFrustum().getPointDistance(point) < effect.getBoundingRadius() * effect.getRenderProperties().getMaxScaleComponent()) {
				effect.render(camera.getViewMatrix(), camera.getProjectionMatrix());
			}
		}

		glDepthMask(true);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_BLEND);

		effectFrameBuffer.getTextureAttachment(0).bind(5);

		//final pass
		finalFrameBuffer.bind();

		finalShader.activate();
		finalShader.setUniform3f("cameraPosition", camera.getX(), camera.getY(), camera.getZ());
		finalShader.setUniform3f("sunLightColor", sunLightColor.getR(), sunLightColor.getG(), sunLightColor.getB());
		finalShader.setUniform3f("sunLightDirection", sunLightDirection.getX(), sunLightDirection.getY(), sunLightDirection.getZ());
		finalShader.setUniform1f("minAmbientBrightness", minAmbientBrightness);
		fullscreenQuad.render();
		finalShader.deactivate();

		//filter
		finalFrameBuffer.getTextureAttachment(0).bind(0);
		filterFrameBuffer.bind();
		filterShader.activate();
		fullscreenQuad.render();
		filterShader.deactivate();

		long time2 = System.nanoTime();
		System.out.println("deferred: " + ((double) (time2 - time1) / 1000000.0));
	}

	public Texture2D getColorOutput() {
		return filterFrameBuffer.getTextureAttachment(0);
	}

	public Texture2D getDepthBuffer() {
		return gBuffer.getTextureAttachment(-1);
	}

	public Texture2D getColorBuffer() {
		return gBuffer.getTextureAttachment(0);
	}

	public Texture2D getNormalBuffer() {
		return gBuffer.getTextureAttachment(1);
	}

	public Texture2D getPositionBuffer() {
		return gBuffer.getTextureAttachment(2);
	}

	public Texture2D getLightBuffer() {
		return gBuffer.getTextureAttachment(3);
	}

	public Texture2D getLightsBuffer() {
		return lightFrameBuffer.getTextureAttachment(0);
	}

	public Texture2D getEffectsBuffer() {
		return effectFrameBuffer.getTextureAttachment(0);
	}

	public void cleanup() {
		clear();

		gBuffer.cleanup();
		lightFrameBuffer.cleanup();
		finalFrameBuffer.cleanup();
		filterFrameBuffer.cleanup();

		gBufferShader.cleanup();
		lightShader.cleanup();
		finalShader.cleanup();
		filterShader.cleanup();

		fullscreenQuad.cleanup();
		lightVbo.cleanup();
	}

}
