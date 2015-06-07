package de.nerogar.noise.render.deferredRenderer;

import static org.lwjgl.opengl.GL11.*;

import java.util.*;

import de.nerogar.noise.render.*;
import de.nerogar.noise.util.*;

public class DeferredRenderer {

	private class VboContainer {
		public DeferredContainer container;
		public List<DeferredRenderable> renderables;
		public VertexBufferObjectInstanced vbo;

		private ArrayList<Matrix4f> instanceMatrices;
		private float[] modelMatrix1;
		private float[] modelMatrix2;
		private float[] modelMatrix3;
		private float[] modelMatrix4;

		public VboContainer(DeferredContainer container) {
			this.container = container;
			renderables = new ArrayList<DeferredRenderable>();

			instanceMatrices = new ArrayList<Matrix4f>();

			vbo = new VertexBufferObjectInstanced(new int[] { 3, 2, 3 },
					container.getMesh().getIndexArray(),
					container.getMesh().getPositionArray(),
					container.getMesh().getUVArray(),
					container.getMesh().getNormalArray());
		}

		public void rebuildInstanceData(ViewFrustum frustum) {

			instanceMatrices.clear();

			Vector3f point = new Vector3f();

			for (int i = 0; i < renderables.size(); i++) {
				point.setX(renderables.get(i).getRenderProperties().getX());
				point.setY(renderables.get(i).getRenderProperties().getY());
				point.setZ(renderables.get(i).getRenderProperties().getZ());

				if (frustum.getPointDistance(point) < renderables.get(i).getContainer().getMesh().getboundingRadius() * renderables.get(i).getRenderProperties().getMaxScaleComponent()) {
					instanceMatrices.add(renderables.get(i).getRenderProperties().getModelMatrix());
				}
			}

			//if arrays are too short, resize them
			if (modelMatrix1 == null || modelMatrix1.length < instanceMatrices.size() * 4) {
				modelMatrix1 = new float[instanceMatrices.size() * 4];
				modelMatrix2 = new float[instanceMatrices.size() * 4];
				modelMatrix3 = new float[instanceMatrices.size() * 4];
				modelMatrix4 = new float[instanceMatrices.size() * 4];
			}

			for (int i = 0; i < instanceMatrices.size(); i++) {
				Matrix4f mat = instanceMatrices.get(i);

				modelMatrix1[i * 4 + 0] = mat.get(0, 0);
				modelMatrix1[i * 4 + 1] = mat.get(0, 1);
				modelMatrix1[i * 4 + 2] = mat.get(0, 2);
				modelMatrix1[i * 4 + 3] = mat.get(0, 3);

				modelMatrix2[i * 4 + 0] = mat.get(1, 0);
				modelMatrix2[i * 4 + 1] = mat.get(1, 1);
				modelMatrix2[i * 4 + 2] = mat.get(1, 2);
				modelMatrix2[i * 4 + 3] = mat.get(1, 3);

				modelMatrix3[i * 4 + 0] = mat.get(2, 0);
				modelMatrix3[i * 4 + 1] = mat.get(2, 1);
				modelMatrix3[i * 4 + 2] = mat.get(2, 2);
				modelMatrix3[i * 4 + 3] = mat.get(2, 3);

				modelMatrix4[i * 4 + 0] = mat.get(3, 0);
				modelMatrix4[i * 4 + 1] = mat.get(3, 1);
				modelMatrix4[i * 4 + 2] = mat.get(3, 2);
				modelMatrix4[i * 4 + 3] = mat.get(3, 3);
			}

			System.out.println(instanceMatrices.size());
			
			int[] instanceComponentCounts = { 4, 4, 4, 4 };
			vbo.setInstanceData(instanceMatrices.size(), instanceComponentCounts, modelMatrix1, modelMatrix2, modelMatrix3, modelMatrix4);
		}
	}

	private Map<DeferredContainer, VboContainer> vboMap;

	private Shader gBufferShader;
	private FrameBufferObject gBuffer;

	private Shader lightShader;
	private FrameBufferObject lightFrameBuffer;

	private LightContainer lightContainer;
	private VertexBufferObjectInstanced lightVbo;
	private Shader finalShader;
	private FrameBufferObject finalFrameBuffer;

	private VertexBufferObjectIndexed fullscreenQuad;

	public DeferredRenderer(int width, int height) {

		vboMap = new HashMap<DeferredContainer, VboContainer>();
		lightContainer = new LightContainer();
		Mesh sphere = WavefrontLoader.loadObject("noiseEngine/meshes/icoSphere.obj");
		lightVbo = new VertexBufferObjectInstanced(new int[] { 3 }, sphere.getIndexArray(), sphere.getPositionArray());

		gBuffer = new FrameBufferObject(width, height, true,
				Texture2D.DataType.BGRA_8_8_8_8I,
				Texture2D.DataType.BGRA_32_32_32F,
				Texture2D.DataType.BGRA_16_16_16F,
				Texture2D.DataType.BGRA_8_8I);

		lightFrameBuffer = new FrameBufferObject(width, height, false, Texture2D.DataType.BGRA_16_16_16F);

		finalFrameBuffer = new FrameBufferObject(width, height, false, Texture2D.DataType.BGRA_8_8_8_8I);

		fullscreenQuad = new VertexBufferObjectIndexed(
				new int[] { 2, 2 },
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

	public void removeObject(DeferredRenderable object) {
		VboContainer container = vboMap.get(object.getContainer());
		container.renderables.remove(object);
	}

	public LightContainer getLightContainer() {
		return lightContainer;
	}

	private void rebuildLightVbo() {
		List<Light> lights = lightContainer.getLights();

		float[] position = new float[lights.size() * 3];
		float[] color = new float[lights.size() * 3];
		float[] reach = new float[lights.size()];
		float[] intensity = new float[lights.size()];

		for (int i = 0; i < lights.size(); i++) {
			position[i * 3 + 0] = lights.get(i).position.getX();
			position[i * 3 + 1] = lights.get(i).position.getY();
			position[i * 3 + 2] = lights.get(i).position.getZ();

			color[i * 3 + 0] = lights.get(i).color.getR();
			color[i * 3 + 1] = lights.get(i).color.getG();
			color[i * 3 + 2] = lights.get(i).color.getB();

			reach[i] = lights.get(i).reach;

			intensity[i] = lights.get(i).intensity;
		}

		lightVbo.setInstanceData(lights.size(), new int[] { 3, 3, 1, 1 }, position, color, reach, intensity);
	}

	//TODO: remove
	public void loadShaders() {
		gBufferShader = new Shader("noiseEngine/shaders/deferredRenderer/gBuffer.vert", "noiseEngine/shaders/deferredRenderer/gBuffer.frag");
		gBufferShader.activate();
		gBufferShader.setUniform1i("textureColor", 0);
		gBufferShader.setUniform1i("textureNormal", 1);
		gBufferShader.setUniform1i("textureLight", 2);
		gBufferShader.deactivate();

		lightShader = new Shader("noiseEngine/shaders/deferredRenderer/lights.vert", "noiseEngine/shaders/deferredRenderer/lights.frag");
		lightShader.activate();
		lightShader.setUniform1i("textureNormal", 1);
		lightShader.setUniform1i("texturePosition", 2);
		lightShader.deactivate();

		finalShader = new Shader("noiseEngine/shaders/deferredRenderer/final.vert", "noiseEngine/shaders/deferredRenderer/final.frag");
		finalShader.activate();
		finalShader.setUniform1i("textureColor", 0);
		finalShader.setUniform1i("textureNormal", 1);
		finalShader.setUniform1i("texturePosition", 2);
		finalShader.setUniform1i("textureLight", 3);
		finalShader.setUniform1i("textureLights", 4);
		finalShader.setUniformMat4f("projectionMatrix", Matrix4fUtils.getOrthographicProjection(0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f).asBuffer());
		finalShader.deactivate();
	}

	public void setFrameBufferResolution(int width, int height) {
		gBuffer.setResolution(width, height);
		lightFrameBuffer.setResolution(width, height);
		finalFrameBuffer.setResolution(width, height);

		lightShader.activate();
		lightShader.setUniform2f("inverseResolution", 1.0f / width, 1.0f / height);
		lightShader.deactivate();

		finalShader.activate();
		finalShader.setUniform2f("inverseResolution", 1.0f / width, 1.0f / height);
		finalShader.deactivate();
	}

	public void render(PerspectiveCamera camera) {

		//render gBuffer
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);

		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);

		gBuffer.bind();
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		gBufferShader.activate();
		gBufferShader.setUniformMat4f("viewMatrix", camera.getViewMatrix().asBuffer());
		gBufferShader.setUniformMat4f("projectionMatrix", camera.getProjectionMatrix().asBuffer());

		for (VboContainer container : vboMap.values()) {
			container.rebuildInstanceData(camera.getViewFrustum());

			container.container.getColorTexture().bind(0);
			container.container.getNormalTexture().bind(1);
			container.container.getLightTexture().bind(2);

			container.vbo.render();
		}

		gBufferShader.deactivate();
		glDisable(GL_DEPTH_TEST);

		//bind gBuffer textures
		gBuffer.getTextureAttachment(0).bind(0);
		gBuffer.getTextureAttachment(1).bind(1);
		gBuffer.getTextureAttachment(2).bind(2);
		gBuffer.getTextureAttachment(3).bind(3);

		//render lights
		rebuildLightVbo();

		glCullFace(GL_FRONT);

		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE);

		lightFrameBuffer.bind();
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		lightShader.activate();
		lightShader.setUniformMat4f("viewMatrix", camera.getViewMatrix().asBuffer());
		lightShader.setUniformMat4f("projectionMatrix", camera.getProjectionMatrix().asBuffer());

		lightVbo.render();
		lightShader.deactivate();

		glDisable(GL_CULL_FACE);
		glDisable(GL_BLEND);

		lightFrameBuffer.getTextureAttachment(0).bind(4);

		//final pass
		finalFrameBuffer.bind();
		finalShader.activate();
		fullscreenQuad.render();
		finalShader.deactivate();
	}

	public FrameBufferObject getRenderTarget() {
		return finalFrameBuffer;
	}

}
